# SmartDAB - Documentazione per lo Sviluppo

## Stato del Progetto: Gennaio 2026

### Casi d'Uso in Implementazione
1. **Gestione delle stanze** - CRUD stanze
2. **Gestione dei dispositivi ed EventBus** - Comunicazione publisher/subscriber
3. **Database** - Persistenza MySQL
4. **Interfaccia grafica** - UI Swing con pattern MVC
5. **Impostazione parametro manuale** - Controllo diretto parametri
6. **CRUD e attivazione/disattivazione Scenari** - Gestione scenari personalizzati

---

## Struttura del Progetto

Il progetto segue un'architettura a strati con pattern MVC per l'interfaccia grafica.

### Layer Core (`core/`)
- **domain/model/** - Entita di dominio
  - `casa/` - Casa, Stanza, Hub
  - `dispositivo/` - Dispositivo (astratto), attuatori (Lampada_ON_OFF), sensori
  - `scenario/` - Scenario e StanzaConfig
  - `parametro/` - IParametroValue, ParametroValue, ObservableParameter
- **domain/enums/** - Enumerazioni
  - `DispositivoParameter` - 47 parametri (TEMPERATURA, UMIDITA, LUMINOSITA, ecc.) con vincoli min/max
  - `DispositivoStates` - Stati del dispositivo (ALIVE, ecc.)
  - `EnumScenarioType` - PREDEFINITO, PERSONALIZZATO
  - `ParameterType` - NUMERIC, BOOLEAN, ENUM
- **beans/** - POJO per trasporto dati
  - `DispositivoPOJO` - Rappresentazione semplificata dispositivo
  - `MisuraPOJO`, `CommunicationPOJO`
- **service/** - Servizi di dominio
  - `GestoreStanze` - CRUD stanze con persistenza
  - `ScenarioManager` - CRUD scenari e attivazione/disattivazione
  - `ParametroManager` - Gestione parametri e applicazione configurazioni
- **port/** - Interfacce per comunicazione esterna
  - `ICommunicator`, `DevicePort`, `RoomPort`
  - `observer/` - Pattern Observer

### Layer Infrastructure (`infrastructure/`)
- **persistence/mysql/dao/** - Data Access Objects
  - `StanzaDAO/StanzaDAOImpl` - Completo e funzionante
  - `DispositivoDAO/DispositivoDAOImpl` - **INCOMPLETO (errore riga 48)**
  - `ScenarioDAO/ScenarioDAOImpl` - Implementato
  - `StanzaConfigDAO/StanzaConfigDAOImpl` - Implementato
  - `MisuraDAO/MisuraDAOImpl` - Implementato
  - `DatabaseConnection` - Singleton connessione DB
- **messaging/** - Sistema eventi pub/sub
  - `EventBus` - Broker messaggi (Singleton)
  - `Topic`, `Request` - Routing messaggi

### Layer UI (`ui/`)
- **view/** - Componenti Swing
  - `MainFrame` - Finestra principale
  - `MainPanel` - Pannello con tab (Stanze, Scenari, Dispositivi)
  - `stanze/` - StanzePanel, StanzeFormPanel
  - `scenari/` - ScenariPanel, ScenarioDetailPanel, ScenarioFormPanel
  - `dispositivi/` - DispositivoPanel, DispositivoFormPanel
- **controller/** - Controller MVC
  - `MainController` - Coordinatore principale
  - `StanzeController` - Gestione stanze
  - `ScenariController` - Gestione scenari
  - `DispositivoController` - Gestione dispositivi

### Factory (`factory/`)
- `EventBusFactory` - Creazione EventBus
- `ParametroValueFactory` - Creazione IParametroValue
- `StanzaConfigFactory` - Creazione StanzaConfig con validazione

---

## Pattern Utilizzati

### MVC (Model-View-Controller)
- **Model**: Classi in `core/domain/model/` e `core/service/`
- **View**: Classi in `ui/view/`
- **Controller**: Classi in `ui/controller/`

### Observer
- `Stanza` implementa Observable e Observer
- `ObservableParameter` notifica cambiamenti ai dispositivi
- Usato per propagare cambiamenti di parametri

### Command
- `ICommand` per azioni sui dispositivi
- `CommandDispatcher` per esecuzione comandi

### Facade
- `SensoreFacade`, `AttuatoreFacade` per semplificare interazione

### Factory
- Factory per creazione oggetti complessi con validazione

### Publisher/Subscriber (Event-Driven)
- Sensori pubblicano su Topic
- Attuatori ricevono notifiche
- EventBus come broker centrale

---

## ANALISI CRITICITA E PUNTI DA RAFFINARE

### PRIORITA ALTA - Bug e Errori di Compilazione

#### 1. DispositivoDAOImpl - Errore Critico (Riga 48)
**File**: `infrastructure/persistence/mysql/dao/DispositivoDAOImpl.java`
**Problema**: Codice incompleto `r.getr,` che impedisce la compilazione
```java
// Riga 48 - ERRORE
r.getr,  // <-- Incompleto, dovrebbe essere r.getString(7),
```
**Impatto**: Impossibile persistere dispositivi nel database
**Soluzione**: Completare il metodo `selectN()` e implementare tutti i metodi CRUD mancanti (insert, update, delete, readById)

#### 2. Lampada_ON_OFF - Metodo Duplicato
**File**: `core/domain/model/dispositivo/attuatori/lampadaON_OFF/Lampada_ON_OFF.java`
**Problema**: Il metodo `applyVariation()` e definito due volte (righe 54-66 e 71-83)
```java
@Override
public int applyVariation(Object state) { ... }  // Prima definizione

@Override
public int applyVariation(Object state) { ... }  // Seconda definizione DUPLICATA
```
**Impatto**: Errore di compilazione - metodo duplicato
**Soluzione**: Rimuovere una delle due definizioni

#### 3. Import Duplicato in Lampada_ON_OFF
**File**: `core/domain/model/dispositivo/attuatori/lampadaON_OFF/Lampada_ON_OFF.java`
**Problema**: Import duplicato di `DispositivoParameter` (righe 4-5)
**Soluzione**: Rimuovere l'import duplicato

### PRIORITA ALTA - Architettura e Integrazione

#### 4. DispositivoController Non Integrato con Database
**File**: `ui/controller/DispositivoController.java`
**Problema**: I dispositivi sono gestiti solo in memoria (`Map<String, DispositivoPOJO>`)
```java
// Riga 30 - Dati solo in memoria
private Map<String, DispositivoPOJO> dispositivi;
```
**Impatto**: I dispositivi non vengono salvati/caricati dal DB
**Soluzione**:
1. Completare DispositivoDAOImpl
2. Integrare il DAO nel DispositivoController
3. Implementare caricamento iniziale dal DB
4. Salvare ogni operazione CRUD nel DB

#### 5. StanzeController - Architettura Inconsistente
**File**: `ui/controller/StanzeController.java`
**Problema**:
- Non usa pattern listener come DispositivoController
- Non integrato con GestoreStanze per operazioni CRUD
- Manipola direttamente la tabella UI senza passare dal service layer
**Impatto**: Bypass del layer service, possibili inconsistenze
**Soluzione**:
1. Aggiungere interface `StanzePanelListener`
2. Integrare con GestoreStanze
3. Seguire pattern di DispositivoController

#### 6. ScenariController - Manca Integrazione con ScenarioFormPanel
**File**: `ui/controller/ScenariController.java`
**Problema**:
- Il bottone "Nuovo" usa solo `JOptionPane.showInputDialog` per il nome
- ScenarioFormPanel esiste ma non e collegato
- Non si possono aggiungere StanzaConfig durante la creazione
**Impatto**: Gli scenari possono essere creati solo vuoti
**Soluzione**:
1. Integrare ScenarioFormPanel nel flusso di creazione
2. Permettere aggiunta configurazioni durante creazione
3. Implementare listener `ScenarioFormListener`

### PRIORITA MEDIA - Funzionalita Incomplete

#### 7. GestoreStanze - Costruzione DAO Ripetuta
**File**: `core/service/GestoreStanze.java`
**Problema**: Crea una nuova istanza di `StanzaDAOImpl` ad ogni operazione
```java
StanzaDAOImpl stanzaDAO = new StanzaDAOImpl();  // Creato ogni volta
```
**Impatto**: Inefficienza, violazione Dependency Injection
**Soluzione**: Iniettare il DAO nel costruttore

#### 8. EventBus - Lista Dispositivi Mai Popolata
**File**: `infrastructure/messaging/EventBus.java`
**Problema**: `ArrayList<Dispositivo> dispositivi` e inizializzato vuoto e non c'e metodo per aggiungere dispositivi
```java
private ArrayList<Dispositivo> dispositivi = new ArrayList<>();  // Sempre vuoto
```
**Impatto**: `searchDispositivoByName()` e `getSubscribers()` restituiscono sempre risultati vuoti/null
**Soluzione**:
1. Aggiungere metodo `registerDispositivo(Dispositivo d)`
2. Aggiungere metodo `unregisterDispositivo(String id)`
3. Integrare registrazione durante creazione dispositivi

#### 9. Costruttore Lampada_ON_OFF - Signature Non Corretta nel Test
**File**: Test `ImpostazioneManualeFlussoTest.java` vs `Lampada_ON_OFF.java`
**Problema**:
- Test (riga 49): `new Lampada_ON_OFF("Lamp01", communicator, 3000)`
- Classe reale: `Lampada_ON_OFF(Topic topic, Lampada_Communicator c, ObservableParameter o, int intensita)`
**Impatto**: Test non compila
**Soluzione**: Allineare test con signature corretta o aggiungere costruttore alternativo

#### 10. Topic.createTopic - Signature Mismatch
**File**: Test vs implementazione Topic
**Problema**: Nel test si usa `Topic.createTopic(stanzaSoggiorno, lampada, parameter)` ma la signature reale potrebbe essere diversa
**Soluzione**: Verificare e allineare

### PRIORITA MEDIA - Database

#### 11. Schema Database Non Documentato
**Problema**: Manca file SQL con schema completo delle tabelle
**Soluzione**: Creare `schema.sql` con:
```sql
CREATE TABLE stanza (id VARCHAR, nome VARCHAR, mq DOUBLE, created_at TIMESTAMP);
CREATE TABLE dispositivi (id VARCHAR, stanza_id VARCHAR, parametro VARCHAR, tipo VARCHAR, stato VARCHAR, attivo BOOLEAN, model VARCHAR, created_at TIMESTAMP);
CREATE TABLE scenario (id VARCHAR, nome VARCHAR, tipo VARCHAR, attivo BOOLEAN, created_at TIMESTAMP, updated_at TIMESTAMP);
CREATE TABLE stanza_config (id VARCHAR, scenario_id VARCHAR, stanza_id VARCHAR, tipo_parametro VARCHAR, valore VARCHAR);
CREATE TABLE misura (id VARCHAR, dispositivo_id VARCHAR, valore DOUBLE, timestamp TIMESTAMP);
```

#### 12. ScenarioDAO Non Collegato a ScenarioManager
**File**: `core/service/ScenarioManager.java`
**Problema**: ScenarioManager usa solo Map in memoria, non persiste nel DB
**Soluzione**: Integrare ScenarioDAOImpl nel ScenarioManager

### PRIORITA BASSA - Miglioramenti

#### 13. System.out.println da Sostituire con Logging
**Problema**: Uso diffuso di `System.out.println` invece di logging framework
**Files coinvolti**: DispositivoDAOImpl, EventBus, GestoreStanze, Lampada_ON_OFF
**Soluzione**: Implementare logging con SLF4J o java.util.logging

#### 14. Gestione Eccezioni da Migliorare
**Problema**: `e.printStackTrace()` usato senza gestione adeguata
**Files coinvolti**: DispositivoDAOImpl, Lampada_ON_OFF
**Soluzione**: Lanciare eccezioni custom o gestire in modo appropriato

#### 15. Manca Validazione in Diversi Punti
**Problema**: Input non sempre validato prima dell'uso
**Esempi**:
- DispositivoController non valida ID dispositivo
- StanzeController non verifica dispositivi nella stanza prima di eliminare
**Soluzione**: Aggiungere validazione input in tutti i controller

---

## Flussi Implementati

### Flusso 1: Gestione Stanze (CRUD)
```
GUI (StanzePanel) -> StanzeController -> GestoreStanze -> Casa + StanzaDAOImpl -> MySQL
```
**Stato**: Funzionante, ma architettura controller da rivedere

### Flusso 2: Impostazione Manuale Parametro
```
GUI -> StanzaConfigFactory.creaConfig*() -> ParametroManager.impostaParametro()
    -> ParametroManager.getDispositivoIdoneo() -> ParametroManager.inviaComando()
    -> EventBus.sendRequest() -> Dispositivo.action()
```
**Stato**: Logica implementata, integrazione UI parziale

### Flusso 3: Attivazione Scenario
```
GUI (ScenariPanel checkbox) -> ScenariController -> ScenarioManager.attivaScenario()
    -> Per ogni StanzaConfig: ParametroManager.applicaStanzaConfig() -> [Flusso 2]
```
**Stato**: Funzionante per scenari gia creati

### Flusso 4: CRUD Scenari
```
GUI (ScenariPanel) -> ScenariController -> ScenarioManager -> Map<String, Scenario>
```
**Stato**: Creazione solo con nome, manca integrazione ScenarioFormPanel

### Flusso 5: CRUD Dispositivi
```
GUI (DispositivoPanel) -> DispositivoController -> Map<String, DispositivoPOJO> (solo memoria)
```
**Stato**: Solo in memoria, manca persistenza DB

---

## Componenti UI Implementati

### MainController
Coordinatore principale dell'applicazione:
- Inizializza Model (Casa, GestoreStanze, ScenarioManager, ParametroManager)
- Inizializza View (MainFrame, MainPanel)
- Crea e coordina sub-controller (ScenariController, DispositivoController)
- Gestisce eventi cambio tab

### DispositivoPanel
Pannello con due viste:
1. **Vista Globale**: Lista tutti i dispositivi con filtro per stanza
2. **Vista Per Stanza**: Seleziona stanza e vede solo i dispositivi di quella stanza

Componenti:
- Tabella dispositivi (ID, Stanza, Tipo, Parametro, Stato, Attivo, Modello)
- ComboBox filtro/selezione stanza
- Bottoni Nuovo/Elimina
- Doppio click per modifica

### DispositivoFormPanel
Form per creazione/modifica dispositivo:
- ID, Stanza (ComboBox), Tipo (Sensore/Attuatore)
- Parametro (ComboBox con tutti DispositivoParameter)
- Stato, Attivo (checkbox), Modello
- Validazione campi obbligatori

### ScenariPanel
Split panel con:
- Sinistra: Tabella scenari (Attivo checkbox, Nome, Tipo) + bottoni Nuovo/Elimina
- Destra: ScenarioDetailPanel con dettagli scenario selezionato

### ScenarioFormPanel
Form completo per scenari:
- Dati base: Nome, Tipo (PREDEFINITO/PERSONALIZZATO)
- Gestione StanzaConfig: tabella + form aggiunta
- Form aggiunta configurazione:
  - Stanza (ComboBox)
  - Parametro (ComboBox)
  - Valore (dinamico: numerico/booleano/enum basato su ParameterType)
- Bottoni Aggiungi/Rimuovi configurazione

---

## Come Avviare l'Applicazione

### Modo consigliato (MainController)
```java
public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        MainController controller = new MainController();
        controller.mostraApplicazione();
    });
}
```

### Prerequisiti
- Java 17+
- MySQL database configurato
- File `db.properties` in resources con credenziali DB

### Configurazione Database
Creare file `src/main/resources/db.properties`:
```properties
db.url=jdbc:mysql://localhost:3306/smartdab
db.user=root
db.password=password
```

---

## Convenzioni Codice

- Stile semplice e leggibile
- Commenti in italiano
- Package in inglese
- Nomi variabili/metodi in camelCase
- Classi con PascalCase
- Costanti in UPPER_SNAKE_CASE
- Interfacce con prefisso `I` (es. ICommand, IParametroValue)

---

## Test Esistenti

### Test Unitari
- `StanzaTest.java` - Test model Stanza
- `DispositivoTest.java` - Test Dispositivo
- `ParametroValueTest.java` - Test validazione parametri
- `GestoreStanzeTest.java` - Test service stanze

### Test Integrazione
- `ImpostazioneManualeFlussoTest.java` - Flusso completo impostazione parametro
- `EventBusTest.java`, `RequestTest.java`, `TopicTest.java` - Messaging

### Test Database
- `DatabaseConnectionTest.java` - Connessione
- `StanzaDAOImplTest.java` - CRUD stanze
- `MisuraDAOTest.java` - Misurazioni

### Test UI
- `TestUIMainFrame.java`, `TestUIStanze.java`, `TestUIScenariController.java`

---

## Checklist Prossimi Passi

### Immediati (Blockers)
- [ ] Correggere DispositivoDAOImpl (errore compilazione riga 48)
- [ ] Rimuovere metodo duplicato in Lampada_ON_OFF
- [ ] Registrare dispositivi nell'EventBus

### A Breve Termine
- [ ] Integrare DispositivoDAO nel DispositivoController
- [ ] Rifattorizzare StanzeController con pattern listener
- [ ] Integrare ScenarioFormPanel in ScenariController
- [ ] Integrare ScenarioDAO in ScenarioManager

### Medio Termine
- [ ] Creare schema.sql completo
- [ ] Implementare logging framework
- [ ] Aggiungere validazione input completa
- [ ] Completare test per nuovi componenti

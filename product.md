# SmartDAB - Product Document

## Vision

SmartDAB e un sistema software per la gestione di una casa intelligente (smart home) che consente agli utenti di monitorare ambienti, dispositivi e parametri domestici. L'obiettivo e migliorare la vivibilita e il comfort delle abitazioni tramite dispositivi che interagiscono seguendo una logica ben definita.

---

## Casi d'Uso Principali

### Attualmente in Implementazione

| Caso d'Uso | Descrizione | Stato |
|------------|-------------|-------|
| UC1 - Gestione Dispositivi | CRUD dispositivi, registrazione nell'EventBus | In corso |
| UC2 - Impostazione Manuale | Controllo parametri stanza (temperatura, luminosita, ecc.) | Parziale |
| UC3 - Creazione Scenari | Creazione scenari personalizzati con configurazioni | Parziale |
| UC4 - Attivazione Scenario | Attivazione/disattivazione scenari esistenti | Funzionante |
| UC5 - CRUD Stanze | Creazione, modifica, eliminazione stanze | Funzionante |
| UC6 - Comunicazioni EventBus | Publisher/Subscriber tra sensori e attuatori | In corso |

### Futuri (Non ancora implementati)

| Caso d'Uso | Descrizione | Priorita |
|------------|-------------|----------|
| Autenticazione PIN | Accesso con PIN per funzionalita avanzate | Alta |
| Regole Automatiche | Attivazione scenari basata su condizioni | Media |
| Monitoraggio Consumi | Visualizzazione consumi energetici | Media |
| Statistiche | Storico azioni e misurazioni | Bassa |
| Previsioni | Previsioni parametri nelle prossime 24h | Bassa |
| Notifiche Guasti | Rilevazione e notifica malfunzionamenti | Alta |

---

## Architettura Sistema

### Diagramma dei Layer

```
+--------------------------------------------------+
|                    UI LAYER                       |
|  +----------+  +-----------+  +---------------+  |
|  |MainFrame |  |MainPanel  |  | *Panel/*Form  |  |
|  +----------+  +-----------+  +---------------+  |
+--------------------------------------------------+
                        |
+--------------------------------------------------+
|              CONTROLLER LAYER                     |
|  +---------------+  +------------------+          |
|  |MainController |  |Scenari/Dispositivo/Stanze |
|  +---------------+  +------------------+          |
+--------------------------------------------------+
                        |
+--------------------------------------------------+
|               SERVICE LAYER                       |
|  +---------------+  +---------------+            |
|  |GestoreStanze  |  |ScenarioManager|            |
|  +---------------+  +---------------+            |
|  +---------------+                               |
|  |ParametroManager|                              |
|  +---------------+                               |
+--------------------------------------------------+
                        |
+--------------------------------------------------+
|                DOMAIN LAYER                       |
|  Casa -> Stanza -> Dispositivo                   |
|  Scenario -> StanzaConfig -> IParametroValue     |
+--------------------------------------------------+
                        |
+--------------------------------------------------+
|            INFRASTRUCTURE LAYER                   |
|  +----------+  +----------+  +----------+        |
|  | EventBus |  |   DAO    |  | Database |        |
|  +----------+  +----------+  +----------+        |
+--------------------------------------------------+
```

### Pattern Architetturali

1. **MVC** - Separazione Model/View/Controller per UI
2. **Publisher/Subscriber** - Comunicazione event-driven tra dispositivi
3. **Observer** - Notifiche cambiamenti parametri
4. **Factory** - Creazione oggetti con validazione
5. **Facade** - Semplificazione interfaccia dispositivi
6. **Singleton** - EventBus e DatabaseConnection

---

## Stato Implementazione per Componente

### Completi (100%)

| Componente | Note |
|------------|------|
| Model Stanza | Completo con Observer pattern |
| Model Scenario | Completo con StanzaConfig |
| Model ParametroValue | Validazione per NUMERIC/BOOLEAN/ENUM |
| StanzaDAOImpl | CRUD completo |
| DispositivoParameter | 47 parametri definiti con vincoli |
| ScenariPanel | UI completa con tabella e dettagli |
| DispositivoPanel | Due viste (globale e per stanza) |
| ScenarioFormPanel | Form completo con gestione config |

### Parziali (50-90%)

| Componente | Stato | Mancanze |
|------------|-------|----------|
| GestoreStanze | 80% | Dependency Injection DAO |
| ScenarioManager | 70% | Integrazione DAO per persistenza |
| ParametroManager | 90% | Solo test integrazione |
| ScenariController | 60% | Integrazione ScenarioFormPanel |
| StanzeController | 50% | Pattern listener, integrazione service |
| EventBus | 70% | Metodi register/unregister |

### Da Completare (< 50%)

| Componente | Stato | Problema |
|------------|-------|----------|
| DispositivoDAOImpl | 20% | Errore compilazione, metodi mancanti |
| DispositivoController | 40% | Nessuna persistenza DB |
| Lampada_ON_OFF | 30% | Metodo duplicato, import duplicato |

---

## Roadmap di Sviluppo

### FASE 1 - Correzione Bug Bloccanti (Urgente)

**Obiettivo**: Rendere il progetto compilabile e testabile

| Task | File | Descrizione |
|------|------|-------------|
| 1.1 | DispositivoDAOImpl.java:48 | Correggere `r.getr,` -> `r.getString(7)` |
| 1.2 | Lampada_ON_OFF.java:71-83 | Rimuovere metodo `applyVariation()` duplicato |
| 1.3 | Lampada_ON_OFF.java:5 | Rimuovere import duplicato |
| 1.4 | ImpostazioneManualeFlussoTest | Allineare con signature corretta Lampada |

**Tempo stimato**: 1-2 ore

### FASE 2 - Completamento Persistenza (Alta Priorita)

**Obiettivo**: Tutti i dati persistono nel database

| Task | Descrizione |
|------|-------------|
| 2.1 | Completare DispositivoDAOImpl (insert, update, delete, readById, readAll) |
| 2.2 | Integrare DispositivoDAO in DispositivoController |
| 2.3 | Integrare ScenarioDAO in ScenarioManager |
| 2.4 | Creare file schema.sql con DDL completo |
| 2.5 | Refactoring GestoreStanze: iniettare DAO nel costruttore |

**Tempo stimato**: 4-6 ore

### FASE 3 - Integrazione EventBus (Alta Priorita)

**Obiettivo**: Sistema di comunicazione funzionante

| Task | Descrizione |
|------|-------------|
| 3.1 | Aggiungere `registerDispositivo(Dispositivo d)` in EventBus |
| 3.2 | Aggiungere `unregisterDispositivo(String id)` in EventBus |
| 3.3 | Integrare registrazione in DispositivoController.onSalva() |
| 3.4 | Integrare deregistrazione in DispositivoController.onEliminaDispositivo() |
| 3.5 | Test comunicazione Sensore -> EventBus -> Attuatore |

**Tempo stimato**: 3-4 ore

### FASE 4 - Rifattorizzazione Controller (Media Priorita)

**Obiettivo**: Architettura consistente tra tutti i controller

| Task | Descrizione |
|------|-------------|
| 4.1 | Creare interface `StanzePanelListener` |
| 4.2 | Refactoring StanzeController con pattern listener |
| 4.3 | Integrare StanzeController con GestoreStanze |
| 4.4 | Integrare ScenarioFormPanel in ScenariController |
| 4.5 | Collegare MainController con StanzeController |

**Tempo stimato**: 4-5 ore

### FASE 5 - Qualita Codice (Bassa Priorita)

**Obiettivo**: Codice manutenibile e robusto

| Task | Descrizione |
|------|-------------|
| 5.1 | Sostituire System.out.println con logging (java.util.logging) |
| 5.2 | Migliorare gestione eccezioni (eccezioni custom) |
| 5.3 | Aggiungere validazione input in tutti i controller |
| 5.4 | Completare JavaDoc per metodi pubblici |
| 5.5 | Aggiungere test unitari per componenti nuovi |

**Tempo stimato**: 3-4 ore

---

## Analisi Tecnica delle Criticita

### 1. DispositivoDAOImpl - BLOCCANTE

```java
// PRIMA (riga 48 - ERRORE)
r.getr,

// DOPO (correzione)
r.getString(7),
```

**Metodi mancanti da implementare**:
- `insertDispositivo(DispositivoPOJO d)`
- `updateDispositivo(DispositivoPOJO d)`
- `deleteDispositivo(String id)`
- `readDispositivo(String id)`
- `readAllDispositivi()`
- `readDispositiviByStanza(String stanzaId)`

### 2. EventBus - Lista Vuota

```java
// PROBLEMA: lista mai popolata
private ArrayList<Dispositivo> dispositivi = new ArrayList<>();

// SOLUZIONE: aggiungere metodi
public void registerDispositivo(Dispositivo d) {
    if (!dispositivi.contains(d)) {
        dispositivi.add(d);
    }
}

public void unregisterDispositivo(String id) {
    dispositivi.removeIf(d -> d.getId().equals(id));
}
```

### 3. StanzeController - Bypass Service Layer

```java
// PROBLEMA: manipola direttamente UI
elencoPanel.rimuoviRigaTabella(riga);

// SOLUZIONE: passare dal service
boolean eliminato = gestoreStanze.eliminaStanza(nomeStanza);
if (eliminato) {
    aggiornaTabella();
}
```

### 4. ScenariController - Form Non Integrato

```java
// PROBLEMA: creazione solo con nome
String nome = JOptionPane.showInputDialog(panel, "Nome nuovo scenario:");
scenarioManager.creaScenario(nome.trim());

// SOLUZIONE: usare ScenarioFormPanel
private void mostraFormNuovoScenario() {
    ScenarioFormPanel formPanel = new ScenarioFormPanel();
    formPanel.preparaPerInserimento();
    formPanel.setListener(new ScenarioFormListener() {
        @Override
        public void onSalva(String nome, EnumScenarioType tipo, List<StanzaConfig> config) {
            Scenario s = scenarioManager.creaScenario(nome, tipo);
            for (StanzaConfig c : config) {
                scenarioManager.aggiungiConfigurazione(nome, c);
            }
            aggiornaTabella();
        }
        @Override
        public void onAnnulla() { /* chiudi dialog */ }
    });
    // mostra dialog con formPanel
}
```

---

## Matrice Requisiti vs Implementazione

| Requisito | Documento | Stato | Note |
|-----------|-----------|-------|------|
| RFU1 - Config dispositivi | Requisiti.md | Parziale | UI ok, persistenza manca |
| RFU2 - CRUD stanze | Requisiti.md | Funzionante | Da rifattorizzare controller |
| RFU3 - Impostazione manuale | Requisiti.md | Parziale | Logica ok, UI parziale |
| RFU4 - Visualizzazione info | Requisiti.md | Funzionante | 3 tab implementati |
| RFU5 - Scenari personalizzati | Requisiti.md | Parziale | Creazione vuota, form pronto |
| RFU6 - Attivazione scenario | Requisiti.md | Funzionante | Via checkbox |
| RFU7 - Autenticazione PIN | Requisiti.md | Non implementato | Hub.getAutenticazione() esiste |
| RFS1-RFS15 | Requisiti.md | Parziale | Vedi dettaglio sopra |

---

## Dipendenze Tecniche

### Attuali (pom.xml)
- Java 17
- JUnit 5.10.1
- Mockito 5.8.0
- MySQL Connector/J 9.1.0

### Suggerite per Miglioramenti
- SLF4J + Logback (logging)
- Lombok (riduzione boilerplate)
- H2 Database (test in-memory)

---

## Metriche Progetto

| Metrica | Valore |
|---------|--------|
| File Java | ~50 |
| Classi di test | 19 |
| DAO implementati | 4/5 (80%) |
| Controller | 4 |
| Panel UI | 8 |
| Parametri supportati | 47 |

---

## Contatti e Riferimenti

**Autori**: Alessandro Geremia Ingenito, Davide Vitello, Beatrice Bertone

**Documenti di riferimento**:
- `Documenti/Documento di visione.md`
- `Documenti/Requisiti.md`
- `Documenti/Documento dei Casi D'uso.md`
- `Documenti/Architettura.md`

**Scadenza progetto**: Fine Gennaio 2026

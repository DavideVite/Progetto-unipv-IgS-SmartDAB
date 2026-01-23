# CLAUDE.md

Guida operativa per Claude Code quando lavora su questo repository.

## Tech Stack

### Build System
- **Maven** 3.x
- **GroupId**: `it.unipv.posfw`
- **ArtifactId**: `smartdab`
- **Version**: `1.0.0-SNAPSHOT`
- **Packaging**: JAR

### Linguaggio e Runtime
- **Java 17** (features: LocalDateTime, Generics, Interface-based design)
- **Compiler**: Maven Compiler Plugin 3.11.0

### Dipendenze (da pom.xml)
| Dipendenza | Versione | Scopo |
|------------|----------|-------|
| JUnit Jupiter | 5.10.1 | Testing framework |
| Mockito | 5.8.0 | Mocking per test |
| MySQL Connector/J | 9.1.0 | Driver database |

### Database
- **MySQL** con driver Connector/J 9.1.0
- Configurazione in `src/main/resources/db.properties`

---

## Struttura Cartelle

```
Progetto-unipv-IgS-SmartDAB/
├── pom.xml                              # Configurazione Maven
├── CLAUDE.md                            # Questa guida
├── product.md                           # Documentazione prodotto
├── README.md                            # README progetto
├── CONTRIBUTING.md                      # Linee guida contributi
├── MAVEN_SETUP.md                       # Setup Maven
├── Documenti/                           # Documentazione progetto (UML, requisiti)
├── properties/                          # File di configurazione
├── target/                              # Output build Maven
└── src/
    ├── main/
    │   ├── java/it/unipv/posfw/smartdab/
    │   │   ├── adapter/                 # Adapter layer (Hexagonal)
    │   │   │   └── facade/              # AttuatoreFacade, SensoreFacade
    │   │   ├── core/                    # Business logic
    │   │   │   ├── beans/               # POJOs/DTOs
    │   │   │   ├── controller/          # ScenarioController
    │   │   │   ├── domain/
    │   │   │   │   ├── enums/           # DispositivoParameter, Message, Stati...
    │   │   │   │   └── model/
    │   │   │   │       ├── casa/        # Casa, Stanza, Hub
    │   │   │   │       ├── command/     # ICommand
    │   │   │   │       ├── dispositivo/ # Dispositivo, Lampada_ON_OFF, Dispatcher
    │   │   │   │       ├── parametro/   # IParametroValue, ParametroValue, Observable
    │   │   │   │       ├── scenario/    # Scenario, StanzaConfig
    │   │   │   │       └── utente/      # Autenticazione
    │   │   │   ├── port/                # Interfacce (Hexagonal ports)
    │   │   │   │   ├── communication/   # ICommunicator, Observer pattern
    │   │   │   │   ├── device/          # DevicePort
    │   │   │   │   ├── messaging/       # IEventBusClient
    │   │   │   │   └── room/            # RoomPort
    │   │   │   └── service/             # GestoreStanze, ScenarioManager, ParametroManager
    │   │   ├── factory/                 # ParametroValueFactory, StanzaConfigFactory, EventBusFactory
    │   │   └── infrastructure/          # Implementazioni tecniche
    │   │       ├── messaging/           # EventBus, Topic, Request
    │   │       └── persistence/mysql/   # DAO e DatabaseConnection
    │   └── resources/
    │       └── db.properties            # Configurazione database
    └── test/
        └── java/it/unipv/posfw/smartdab/  # Test JUnit 5
```

---

## Architettura

### Pattern Principale: Hexagonal (Ports & Adapters)

```
                    ┌─────────────────────────────────────┐
                    │              CORE                    │
                    │  ┌───────────────────────────────┐  │
     Adapters  ←────│──│  Domain Models (Casa, Stanza, │  │────→  Ports
   (Facade)         │  │  Dispositivo, Scenario)       │  │    (Interfaces)
                    │  └───────────────────────────────┘  │
                    │  ┌───────────────────────────────┐  │
                    │  │  Services (GestoreStanze,     │  │
                    │  │  ScenarioManager, Parametro)  │  │
                    │  └───────────────────────────────┘  │
                    └─────────────────────────────────────┘
                                     │
                    ┌────────────────┴────────────────┐
                    │        INFRASTRUCTURE           │
                    │  EventBus, DAOs, Database       │
                    └─────────────────────────────────┘
```

### Design Patterns Utilizzati

| Pattern | Implementazione | Location |
|---------|-----------------|----------|
| **Singleton** | EventBus, Hub | `EventBus.getInstance()`, `Hub.getInstance()` |
| **Observer** | Observable/Observer interfaces | `core/port/communication/observer/` |
| **Command** | ICommand + CommandDispatcher | `core/domain/model/command/`, `dispatcher/` |
| **Facade** | SensoreFacade, AttuatoreFacade | `adapter/facade/` |
| **Factory** | ParametroValueFactory, StanzaConfigFactory | `factory/` |
| **DAO** | DispositivoDAO, StanzaDAO, etc. | `infrastructure/persistence/mysql/dao/` |
| **Pub/Sub** | EventBus con Request/Topic | `infrastructure/messaging/` |

### Comunicazione Event-Driven

**Formato Topic**: `home/{room}/{device_id}/{parameter}`

```java
// Creazione Topic
Topic topic = Topic.createTopic(stanza, dispositivo, DispositivoParameter.TEMPERATURA);

// Creazione e invio Request
Request request = Request.createRequest(topic, "SETPOINT", valore);
eventBus.setRequest(request);
Message response = eventBus.sendRequest(request);
```

---

## Regole di Stile

### Naming Conventions
- **Italiano** per entità di dominio: `Casa`, `Stanza`, `Dispositivo`, `Attuatore`, `Sensore`
- **Italiano** per metodi di dominio: `modificaNomeStanza()`, `impostaParametro()`, `eliminaStanza()`
- **CamelCase** per classi e metodi
- **ID dispositivo**: Pattern regex `[A-Za-z]{1,17}[0-9]{0,3}`

### Gerarchia Dispositivi
```java
Dispositivo (abstract base)
├── SensoreFacade (abstract - publishers)
│   └── [Sensori concreti da implementare]
└── AttuatoreFacade (abstract - subscribers)
    └── Lampada_ON_OFF (implementazione concreta)
```

### Validazione Parametri
- Usa `DispositivoParameter` enum per vincoli (min/max/unit/allowedValues)
- Valida sempre con `IParametroValue.isValid()` prima di applicare
- Factory gestisce la validazione alla creazione

### Gestione Errori
- Metodi ritornano `boolean` per successo/fallimento
- `null` return indica "non trovato"
- Eccezioni per errori di programmazione (`IllegalArgumentException`)

---

## Comandi Comuni

### Maven Build
```bash
# Compila il progetto
mvn clean compile

# Esegui i test
mvn test

# Build JAR
mvn package

# Clean e install
mvn clean install

# Skip test durante build
mvn clean install -DskipTests
```

### Esecuzione Test Specifici
```bash
# Singola classe test
mvn test -Dtest=ScenarioControllerTest

# Singolo metodo
mvn test -Dtest=ScenarioControllerTest#testAttivaScenario

# Pattern
mvn test -Dtest=*ControllerTest
```

---

## Git Workflow

### Branch Protection
- `main` protetto - NO push diretti
- Tutte le modifiche via Pull Request
- Richiesta almeno 1 review

### Branch Naming
- `feature/<descrizione>` - Nuove funzionalità
- `bugfix/<descrizione>` - Bug fix
- `refactor/<descrizione>` - Refactoring

### Commit Message (Conventional Commits)
```
feat: aggiunge attivazione scenario
fix: corregge validazione parametro temperatura
docs: aggiorna CLAUDE.md
refactor: estrae logica in ParametroManager
test: aggiunge test ScenarioController
```

---

## Note Critiche per lo Sviluppo

### 1. Vincoli di Business
- **Stanze**: Non eliminabili se contengono dispositivi (`s.isEmpty()`)
- **Scenari**: Aggiornare sempre `data_ultima_modifica` su modifiche
- **PIN**: Richiesto per creazione stanze (5 caratteri)

### 2. EventBus
```java
// SEMPRE usare getInstance()
EventBus eventBus = EventBus.getInstance();

// MAI creare nuove istanze
// EventBus eventBus = new EventBus(); // SBAGLIATO
```

### 3. Stato vs Active
```java
// Due proprietà diverse in Dispositivo:
dispositivo.isActive();     // boolean - acceso/spento
dispositivo.getState();     // DispositivoStates enum - ALIVE/CONFLICT/UNKNOWN/DISABLED
```

### 4. Factory per Parametri
```java
// Usa SEMPRE le factory per creare parametri
StanzaConfig config = StanzaConfigFactory.creaConfigNumerico(
    "Soggiorno",
    DispositivoParameter.TEMPERATURA,
    22.5
);

// Validazione automatica dei range
```

### 5. Flusso Attivazione Scenario
```
ScenarioController.attivaScenario(nome, parametroManager)
    └── ScenarioManager.attivaScenario()
        └── for each StanzaConfig:
            └── ParametroManager.applicaStanzaConfig(config)
                └── ParametroManager.impostaParametro()
                    └── EventBus.sendRequest()
```

---

## Test Patterns

### Mock EventBus
```java
private static class MockEventBusClient implements IEventBusClient {
    private Request lastRequest;
    private boolean requestSent = false;

    @Override
    public void setRequest(Request request) {
        this.lastRequest = request;
    }

    @Override
    public Message sendRequest(Request request) {
        this.requestSent = true;
        return Message.ACK;
    }

    public boolean isRequestSent() { return requestSent; }
}
```

### Setup Standard Test
```java
@BeforeEach
void setUp() {
    casa = new Casa();
    stanza = new Stanza("S01", "Soggiorno", 25.0);
    casa.nuovaStanza(stanza);

    // Setup dispositivo con topic PRIMA di switchDispositivo()
    Lampada_Communicator communicator = new Lampada_Communicator();
    lampada = new Lampada_ON_OFF("Lamp01", communicator, 3000);
    Topic topic = Topic.createTopic(stanza, lampada, DispositivoParameter.LUMINOSITA);
    lampada.setTopic(topic);
    lampada.switchDispositivo();
    stanza.addDispositivo(lampada);
}
```

---

## File Documentazione Aggiuntiva

| File | Contenuto |
|------|-----------|
| `Documenti/Architettura.md` | Decisioni architetturali |
| `Documenti/Package-tree.md` | Struttura package dettagliata |
| `Documenti/Documento di visione.md` | Vision document |
| `Documenti/Documento dei Casi D'uso.md` | Use cases |
| `Documenti/Requisiti.md` | Specifiche requisiti |

---

## TODO nel Codice

| File | Linea | Descrizione |
|------|-------|-------------|
| `ScenarioController.java` | 18 | Completare metodi gestione scenari |
| `Lampada_ON_OFF.java` | 15 | Check ObservableParameter esistente |
| `Lampada_ON_OFF.java` | 48 | Implementare controllo ON/OFF |
| `Scenario.java` | 65 | Gestione modifiche scenario |
| `CommandDispatcher.java` | 25 | Eccezione personalizzata |

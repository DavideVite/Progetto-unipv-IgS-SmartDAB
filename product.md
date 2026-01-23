# Product Requirements Document - SmartDAB

## 1. Obiettivo del Progetto

### Scopo
SmartDAB (Smart Domotics Assistant Box) è un sistema di gestione domotica progettato per migliorare il comfort e la qualità della vita in ambienti residenziali attraverso il controllo automatizzato dei dispositivi.

### Obiettivi
- Gestione automatizzata della casa con minimo intervento dell'utente
- Controllo intuitivo dei parametri ambientali (temperatura, umidità, luminosità)
- Creazione di scenari personalizzati e regole di automazione
- Monitoraggio dei consumi energetici e rilevamento malfunzionamenti
- Comunicazione sicura tra tutti i dispositivi connessi

### Target Users
Proprietari di casa senza competenze tecniche che desiderano un'esperienza smart home semplice e intuitiva.

---

## 2. Stato Attuale - Funzionalità Implementate

### 2.1 Gestione Stanze (Casa/Stanza)
| Stato | Funzionalità |
|-------|-------------|
| ✅ Completo | Creazione stanze con validazione PIN |
| ✅ Completo | Modifica nome stanza |
| ✅ Completo | Eliminazione stanza (solo se vuota) |
| ✅ Completo | Visualizzazione lista stanze |
| ✅ Completo | Aggiunta/rimozione dispositivi dalla stanza |

**Classi coinvolte**: `Casa`, `Stanza`, `GestoreStanze`, `Hub`

### 2.2 Gestione Dispositivi
| Stato | Funzionalità |
|-------|-------------|
| ✅ Completo | Gerarchia base dispositivi (Dispositivo → AttuatoreFacade/SensoreFacade) |
| ✅ Completo | Implementazione Lampada ON/OFF con communicator |
| ✅ Completo | Sistema di Topic per identificazione dispositivi |
| ✅ Completo | Stati dispositivo (ALIVE, CONFLICT, UNKNOWN, DISABLED) |
| ✅ Completo | Attivazione/disattivazione dispositivi |
| ✅ Completo | Command Dispatcher per traduzione comandi |

**Classi coinvolte**: `Dispositivo`, `AttuatoreFacade`, `SensoreFacade`, `Lampada_ON_OFF`, `Lampada_Communicator`, `CommandDispatcher`

### 2.3 Gestione Parametri
| Stato | Funzionalità |
|-------|-------------|
| ✅ Completo | Interfaccia IParametroValue con validazione |
| ✅ Completo | Supporto tipi: NUMERIC, BOOLEAN, ENUM |
| ✅ Completo | 35+ parametri predefiniti con vincoli (min/max/unità) |
| ✅ Completo | Factory per creazione parametri (ParametroValueFactory) |
| ✅ Completo | ObservableParameter per notifiche cambiamenti |

**Classi coinvolte**: `IParametroValue`, `ParametroValue`, `ParametroValueFactory`, `ObservableParameter`, `DispositivoParameter`

### 2.4 Impostazione Manuale Parametri
| Stato | Funzionalità |
|-------|-------------|
| ✅ Completo | ParametroManager per impostazione manuale |
| ✅ Completo | Ricerca dispositivo idoneo per parametro |
| ✅ Completo | Validazione valori prima dell'invio |
| ✅ Completo | Invio comandi tramite EventBus |

**Classi coinvolte**: `ParametroManager`, `StanzaConfig`, `StanzaConfigFactory`

### 2.5 Gestione Scenari
| Stato | Funzionalità |
|-------|-------------|
| ✅ Completo | Creazione scenario vuoto (PERSONALIZZATO) |
| ✅ Completo | Aggiunta StanzaConfig a scenario |
| ✅ Completo | Rimozione StanzaConfig da scenario |
| ✅ Completo | Modifica StanzaConfig (rimuovi + aggiungi) |
| ✅ Completo | Attivazione scenario (applica tutte le config) |
| ✅ Completo | Disattivazione scenario |
| ✅ Completo | Eliminazione scenario |
| ✅ Completo | Tracciamento timestamp (creazione/modifica) |

**Classi coinvolte**: `Scenario`, `StanzaConfig`, `ScenarioManager`, `ScenarioController`, `StanzaConfigFactory`

### 2.6 Sistema di Comunicazione (EventBus)
| Stato | Funzionalità |
|-------|-------------|
| ✅ Completo | EventBus Singleton come broker centrale |
| ✅ Completo | Formato Topic strutturato (home/room/device/parameter) |
| ✅ Completo | Sistema Request per incapsulamento messaggi |
| ✅ Completo | Logica retry (fino a 10 tentativi) |
| ✅ Completo | Ricerca dispositivi per nome/topic |

**Classi coinvolte**: `EventBus`, `Topic`, `Request`, `IEventBusClient`

### 2.7 Persistenza Database
| Stato | Funzionalità |
|-------|-------------|
| ✅ Completo | Pattern DAO per accesso dati |
| ✅ Completo | DAO per Dispositivo, Stanza, Communication, Misura |
| ✅ Completo | Connessione MySQL |

**Classi coinvolte**: `DispositivoDAO`, `StanzaDAO`, `CommunicationDAO`, `MisuraDAO`, `DatabaseConnection`

### 2.8 Autenticazione
| Stato | Funzionalità |
|-------|-------------|
| ✅ Completo | Password produttore |
| ✅ Completo | PIN utente (5 caratteri) |
| ✅ Completo | Verifica PIN per operazioni sensibili |

**Classi coinvolte**: `Autenticazione`, `Hub`

---

## 3. Roadmap - Funzionalità da Completare

### 3.1 Alta Priorità (TODO nel codice)

| Priorità | Funzionalità | Note |
|----------|-------------|------|
| 🔴 Alta | Controllo ON/OFF lampada | Commento in Lampada_ON_OFF.java:48 |
| 🔴 Alta | Eccezione personalizzata CommandDispatcher | Commento in CommandDispatcher.java:25 |
| 🔴 Alta | Check ObservableParameter esistente | Commento in Lampada_ON_OFF.java:15 |
| 🟡 Media | Gestione modifiche Scenario | Commento in Scenario.java:65 |

### 3.2 Funzionalità Non Ancora Implementate

| Categoria | Funzionalità | Requisito |
|-----------|-------------|-----------|
| Scenari | Scenari PREDEFINITI (Night/Day/Away Mode) | RFU6 |
| Automazione | Regole di automazione (trigger condizionali) | RFU8, RFU9 |
| Conflitti | Rilevamento e risoluzione conflitti | RFU10, RFS10 |
| Monitoraggio | Statistiche consumo energetico | RFU11 |
| Monitoraggio | Previsioni 24 ore | RFU12 |
| Monitoraggio | Statistiche sensori e storico azioni | RFU13 |
| Notifiche | Notifiche malfunzionamento | RFU14 |
| Diagnostica | Health check periodico dispositivi | RFS14 |
| GUI | Interfaccia utente touchscreen | NFR Usability |

### 3.3 Sensori da Implementare

Attualmente solo `Lampada_ON_OFF` è implementata. Mancano:
- Sensore temperatura
- Sensore umidità
- Sensore movimento
- Sensore contatto porta/finestra
- Termostato
- Altri attuatori (tapparelle, prese smart, ecc.)

---

## 4. Struttura Dati

### 4.1 Entità Principali

```
Casa (Root Aggregate)
├── Set<Stanza>
│   ├── id: String
│   ├── nome: String
│   ├── mq: Double
│   ├── List<Dispositivo>
│   │   ├── id: String (pattern: [A-Za-z]{1,17}[0-9]{0,3})
│   │   ├── topic: Topic
│   │   ├── active: boolean
│   │   ├── state: DispositivoStates
│   │   └── communicator: ICommunicator
│   └── Map<String, Double> parametri
└── Hub (Singleton)
    ├── Autenticazione
    └── GestoreStanze
```

### 4.2 Scenario Structure

```
Scenario
├── nome: String
├── tipo: EnumScenarioType (PREDEFINITO | PERSONALIZZATO)
├── isActive: boolean
├── data_creazione: LocalDateTime
├── data_ultima_modifica: LocalDateTime
└── List<StanzaConfig>
    ├── stanzaId: String
    ├── tipo_parametro: DispositivoParameter
    └── parametro: IParametroValue
```

### 4.3 Parametri Supportati (DispositivoParameter)

| Tipo | Parametri |
|------|-----------|
| NUMERIC | TEMPERATURA (15-30°C), UMIDITA (0-100%), LUMINOSITA (0-10000lux), PRESSIONE, CO2, PM25, RUMORE, LIVELLO_GAS, TENSIONE, CORRENTE, POTENZA, CONSUMO_ENERGIA, FLUSSO_ACQUA, LIVELLO_LIQUIDO, VELOCITA_VENTO, PIOGGIA, UV, APERTURA_PERCENTUALE (0-100%) |
| BOOLEAN | CONTATTO_PORTA, MOVIMENTO, FUMO, ALLAGAMENTO, VIBRAZIONE, PRESENZA, VETRO_ROTTO |
| ENUM | STATO_SERRATURA (LOCKED/UNLOCKED/JAMMED), MODO_HVAC (OFF/HEAT/COOL/AUTO/FAN), STATO_TAPPARELLA (OPEN/CLOSED/PARTIAL), DIREZIONE_VENTO, QUALITA_ARIA (GOOD/MODERATE/POOR/HAZARDOUS) |

### 4.4 Schema Database (MySQL)

**Tabelle previste**:
- `dispositivi` - Registrazione dispositivi
- `stanze` - Configurazione stanze
- `comunicazioni` - Log comunicazioni
- `misure` - Storico misurazioni sensori

---

## 5. Requisiti Non Funzionali

| Categoria | Requisito | Stato |
|----------|-----------|-------|
| **Tecnologia** | Java 17 | ✅ |
| **Database** | MySQL | ✅ |
| **Build** | Maven | ✅ |
| **Testing** | JUnit 5 + Mockito | ✅ |
| **Architettura** | Hexagonal/Clean Architecture | ✅ |
| **Pattern** | Publisher/Subscriber con EventBus | ✅ |
| **Usabilità** | GUI touchscreen | ❌ Non iniziata |
| **Deadline** | Fine Gennaio 2026 | In corso |

---

## 6. Test Coverage

| Area | Test Class | Metodi |
|------|------------|--------|
| Domain - Stanza | StanzaTest | Test entità stanza |
| Domain - Dispositivo | DispositivoTest | Test entità dispositivo |
| Domain - Parametro | ParametroValueTest | Test valori parametro |
| Service - Stanze | GestoreStanzeTest | CRUD stanze |
| Service - Parametri | ImpostazioneManualeFlussoTest | Flusso impostazione manuale |
| Controller | ScenarioControllerTest | 15 test (CRUD + attivazione scenari) |
| Infrastructure | EventBusTest | Test EventBus |
| Infrastructure | RequestTest | Test validazione Request |
| Infrastructure | TopicTest | Test parsing Topic |
| Persistence | StanzaDAOImplTest | Test DAO stanze |
| Persistence | DatabaseConnectionTest | Test connessione DB |

---

## 7. Vincoli e Dipendenze

### Vincoli
- Ambiente di simulazione (non comunicazione hardware reale)
- La comunicazione deve seguire standard industriali (simulazione protocollo ZigBee)

### Dipendenze
- Le stanze non possono essere eliminate se contengono dispositivi
- Gli scenari tracciano timestamp di creazione e modifica
- Il PIN è richiesto per operazioni sensibili (creazione stanze)

### Architettura
- **Pattern**: Publisher/Subscriber con Event Bus (Singleton)
- **Formato Comunicazione**: `home/{room}/{device}/{parameter}`
- **Tipi Dispositivo**: Sensori (publisher) e Attuatori (subscriber)

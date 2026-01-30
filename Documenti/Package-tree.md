```
src/
├── core/
│   │
│   ├── domain/                                     # --- ENTITIES LAYER ---
│   │   │
│   │   ├── model/                                  # Entità di dominio
│   │   │   ├── casa/
│   │   │   │   ├── Casa.java
│   │   │   │   ├── Stanza.java
│   │   │   │   └── Hub.java
│   │   │   │
│   │   │   ├── dispositivo/
│   │   │   │   ├── Dispositivo.java
│   │   │   │   ├── attuatori/
│   │   │   │   │   └── lampadaON_OFF/
│   │   │   │   │       ├── Lampada_ON_OFF.java
│   │   │   │   │       ├── Lampada_Communicator.java
│   │   │   │   │       ├── Lampada_DispatcherBootstrap.java
│   │   │   │   │       └── commands/
│   │   │   │   │           ├── StateUpdateCommand.java
│   │   │   │   │           └── SwitchCommand.java
│   │   │   │   └── dispatcher/
│   │   │   │       ├── CommandDispatcher.java
│   │   │   │       └── IDispatcherBootstrap.java
│   │   │   │
│   │   │   ├── command/
│   │   │   │   └── ICommand.java
│   │   │   │
│   │   │   ├── scenario/
│   │   │   │   ├── Scenario.java
│   │   │   │   └── StanzaConfig.java
│   │   │   │
│   │   │   ├── parametro/
│   │   │   │   ├── IParametroValue.java
│   │   │   │   ├── ObservableParameter.java
│   │   │   │   └── ParametroValue.java
│   │   │   │
│   │   │   └── utente/
│   │   │       └── Autenticazione.java
│   │   │
│   │   └── enums/
│   │       ├── DispositivoParameter.java
│   │       ├── DispositivoStates.java
│   │       ├── EnumScenarioType.java
│   │       ├── EnumTipoParametro.java
│   │       ├── Message.java
│   │       └── ParameterType.java
│   │
│   ├── beans/
│   │   ├── CommunicationPOJO.java
│   │   ├── DispositivoPOJO.java
│   │   └── MisuraPOJO.java
│   │
│   ├── controller/
│   │   └── ScenarioController.java
│   │
│   ├── service/                                    # --- DOMAIN SERVICES ---
│   │   ├── GestoreStanze.java
│   │   ├── ScenarioManager.java
│   │   └── ParametroManager.java
│   │
│   └── port/
│       ├── communication/
│       │   ├── ICommunicator.java
│       │   └── observer/
│       │       ├── Observable.java
│       │       └── Observer.java
│       │
│       ├── device/
│       │   └── DevicePort.java
│       │
│       ├── messaging/
│       │   └── IEventBusClient.java
│       │
│       └── room/
│           └── RoomPort.java
│
├── infrastructure/                                 # === OUTER LAYER - Frameworks & Drivers ===
│   │
│   ├── persistence/                                # --- DATABASE ---
│   │   └── mysql/
│   │       ├── DatabaseConnection.java
│   │       └── dao/
│   │           ├── CommunicationDAO.java
│   │           ├── CommunicationDAOImpl.java
│   │           ├── DispositivoDAO.java
│   │           ├── DispositivoDAOImpl.java
│   │           ├── MisuraDAO.java
│   │           ├── MisuraDAOImpl.java
│   │           ├── ScenarioDAO.java
│   │           ├── ScenarioDAOImpl.java
│   │           ├── StanzaConfigDAO.java
│   │           ├── StanzaConfigDAOImpl.java
│   │           ├── StanzaDAO.java
│   │           └── StanzaDAOImpl.java
│   │
│   └── messaging/                                  # --- EVENT BUS ---
│       ├── EventBus.java
│       ├── DispositiviObserver.java
│       ├── request/
│       │   └── Request.java
│       └── topic/
│           └── Topic.java
│
├── adapter/                                        # === INTERFACE ADAPTERS ===
│   └── facade/
│       ├── SensoreFacade.java
│       └── AttuatoreFacade.java
│
├── factory/                                        # === CONFIGURATION ===
│   ├── EventBusFactory.java
│   ├── ParametroValueFactory.java
│   └── StanzaConfigFactory.java
│
└── ui/                                             # === USER INTERFACE ===
    ├── SmartDABApp.java
    │
    ├── controller/
    │   ├── DispositivoController.java
    │   ├── MainController.java
    │   ├── ScenariController.java
    │   └── StanzeController.java
    │
    └── view/
        ├── MainFrame.java
        ├── MainPanel.java
        ├── dispositivi/
        │   ├── DispositivoFormPanel.java
        │   └── DispositivoPanel.java
        ├── scenari/
        │   ├── ScenarioDetailPanel.java
        │   ├── ScenarioFormPanel.java
        │   └── ScenariPanel.java
        └── stanze/
            ├── StanzeFormPanel.java
            └── StanzePanel.java
```

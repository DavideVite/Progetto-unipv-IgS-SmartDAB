```
src/
├── core/                                   
│   │
│   ├── domain/                              # --- ENTITIES LAYER ---
│   │   │
│   │   ├── model/                           # Entità di dominio
│   │   │   ├── casa/
│   │   │   │   ├── Casa.java
│   │   │   │   └── Stanza.java
│   │   │   │   └── Hub.java
│   │   │   │
│   │   │   ├── dispositivo/
│   │   │   │   ├── Dispositivo.java
│   │   │   │   ├── sensori/
│   │   │   │   │   ├── Sensore.java        
│   │   │   │   │   ├── SensorI.java
│   │   │   │   │   └── SensorII.java
│   │   │   │   └── attuatori/
│   │   │   │       ├── Attuatore.java       
│   │   │   │       ├── AttuatoreLocale.java
│   │   │   │       └── AttuatoreII.java
│   │   │   │
│   │   │   ├── scenario/
│   │   │   │   ├── Scenario.java
│   │   │   │   └── ScenarioRoomConfig.java
│   │   │   │
│   │   │   ├── parametro/
│   │   │   │   └── ObservableParameter.java
│   │   │   │
│   │   │   └── utente/
│   │   │       └── Autenticazione.java
│   │   │
│   │   ├── valueobject/                     
│   │   │   ├── ParametroValue.java
│   │   │   ├── Conflict.java
│   │   │   ├── ObserverParameter.java
│   │   │
│   │   ├── enums/
│   │   │   ├── CommunicationStatus.java     
│   │   │   ├── DeviceState.java             
│   │   │   ├── ConflictType.java
│   │   │   └── ScenarioState.java
│   │   │
│   │   └── exception/                       # Eccezioni di dominio
│   │       ├── DomainException.java
│   │       ├── ScenarioNotFoundException.java
│   │       ├── ConflictException.java
│   │       └── InvalidParameterException.java
│   │
│   |                          
│   │── dto/                            
│   │   ├── ScenarioDTO.java
│   │   ├── ParametroDTO.java
│   │   └── ConflictDTO.java
│   │
│   ├── service/                             # --- DOMAIN SERVICES ---
│   │   │                                   
│   │   ├── ScenarioManager.java
│   │   ├── ParametroManager.java
│   │   ├── ConflictValidator.java
│   │   │
│   │   └── strategy/                        
│   │       ├── TransitionStrategy.java      
│   │       ├── ManualVideoStrategy.java
│   │       ├── BruceSimulaStrategy.java
│   │       └── EventBusStrategy.java
│   │
│   └── port/                                
│       │
│       ├── outbound/                       
│       │   ├── repository/
│       │   │   ├── IScenarioRepository.java
│       │   │   ├── IParametroRepository.java
│       │   │   └── ILogRepository.java
│       │   │
│       │   ├── registry/                    
│       │   │   ├── IDeviceRegistry.java
│       │   │   └── IStanzaRegistry.java
│       │   │
│       │   └── communication/
│       │       └── ICommunicator.java
│       │
│       └── inbound/                         # Porte in entrata (driving) - opzionale
│           ├── IScenarioService.java
│           └── IParametroService.java
│
├── infrastructure/                          # === OUTER LAYER - Frameworks & Drivers ===
│   │
│   ├── persistence/                         # --- DATABASE ---
│   │   │
│   │   ├── mysql/
│   │   │   ├── repository/
│   │   │   │   ├── ScenarioRepositoryImpl.java
│   │   │   │   ├── ParametroRepositoryImpl.java
│   │   │   │   └── LogRepositoryImpl.java
│   │   │   │
│   │   │   ├── entity/                      # Entità JPA/database (se diverso da domain)
│   │   │   │   ├── ScenarioEntity.java
│   │   │   │   └── ParametroEntity.java
│   │   │   │
│   │   │   └── mapper/                      # Mapping Entity <-> Domain
│   │   │       ├── ScenarioMapper.java
│   │   │       └── ParametroMapper.java
│   │   │
│   │   ├── DatabaseConnection.java
│   │   └── StoricoDatiImpl.java
│   │
│   ├── messaging/                           # --- EVENT BUS ---
│   │   ├── EventBus.java
│   │
│   └── logging/
│       └── LogAdmin.java
│
├── adapter/                                 # === INTERFACE ADAPTERS ===
│   │
│   ├── inbound/                             # --- Driving Adapters ---
│   │   │
│   │   ├── controller/                      # REST/API Controllers
│   │   │   ├── ScenarioController.java
│   │   │   ├── ParametroController.java
│   │   │   └── StanzaController.java
│   │   │
│   │   ├── facade/                          # Facade Pattern
│   │   │   ├── GestoreStanze.java
│   │   │   └── SensoreFacade.java
│   │   │   └── AttuatoreFacade.java
│   │   │
│   │   └── presenter/                       # Formattazione output
│   │       ├── ScenarioPresenter.java
│   │       └── ConflictPresenter.java
│
├── ui/                                      # === PRESENTATION LAYER ===
│   │
│   └── view/
│       ├── console/                         # CLI (se presente)
│       │   └── ConsoleView.java
│       │
│       └── gui/                             # GUI (se presente)
│           └── MainView.java
│
├── config/                                  # === CONFIGURATION ===
│   │
│   ├── ApplicationContext.java              # DI Container / Bootstrap
│   ├── DatabaseConfig.java
│   ├── EventBusConfig.java
│   │
│   └── di/                                 
│       ├── RepositoryModule.java
│       ├── ServiceModule.java
│       └── ControllerModule.java
│
└── shared/                                 
    │
    ├── util/
    │   ├── DateTimeUtils.java
    │   
    │
    └── constant/
        └── ApplicationConstants.java

```

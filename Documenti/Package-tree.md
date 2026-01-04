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
│   │   │   │
│   │   │   ├── dispositivo/
│   │   │   │   ├── Dispositivo.java
│   │   │   │       ├── Sensore.java
│   │   │   │       ├── Attuatore.java   
│   │   │   │   ├── Hub.java
│   │   │   │           
│   │   │   ├── scenario/
│   │   │   │   ├── Scenario.java
│   │   │   │   └── ScenarioRoomConfig.java
│   │   │   │
│   │   │   ├── parametro/
│   │   │   │   └── Parametro.java
│   │   │   │
│   │   │   └── utente/
│   │   │       └── Autenticazione.java
│   │   │
│   │   ├── valueobject/                     
│   │   │   ├── ParametroValue.java
│   │   │   ├── Conflict.java
│   │   │   ├── ObserverParameter.java
│   │   │   └── DeviceStatus.java
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
│   │   
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
│       │   ├── messaging/
│       │   │   └── IEventBus.java
│       │   │
│       │   └── communication/
│       │       └── ICommunicationModule.java
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
│   │   ├── EventBusImpl.java
│   │   │
│   │   └── event/                           # Eventi concreti
│   │       ├── DomainEvent.java             # Base class
│   │       ├── ScenarioActivatedEvent.java
│   │       ├── ParameterChangedEvent.java
│   │       └── ConflictDetectedEvent.java
│   │
│   ├── communication/                       # --- MODULO COMUNICAZIONE ---
│   │   ├── CommunicationModuleImpl.java
│   │   └── adapter/
│   │       └── CommunicationAdapter.java
│   │
│   |
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
│   │   │   └── SensorFacade.java
│   │   │
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
    └── constant/
        └── ApplicationConstants.java

```

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
│   │   │   │   ├── sensori
│   │   │   │   │   ├── Sensore.java        
│   │   │   │   │   ├── SensorI.java
│   │   │   │   │   └── SensorII.java
│   │   │   │   └── attuatori/
│   │   │   │       ├── Attuatore.java       
│   │   │   │       ├── AttuatoreI.java
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
│   │   │   
│   │   ├── enums/
│   │   │   ├── Messaggio.java     
│   │   │   ├── DeviceState.java             
│   │   │   ├── ConflictType.java
│   │   │   └── ScenarioState.java
│   │   │
│   │   └── exception/                       # Eccezioni di dominio
│   |                          
│   │── beans/                            
│   │   ├── ScenarioPOJO.java
│   │   ├── ParametroPOJO.java
│   │   ├── LogAzionePOJO.java
│   │   └── ConflictPOJO.java
│   │
│   ├── service/
│   │   ├── GestoreStanze.java                            # --- DOMAIN SERVICES ---                                
│   │   ├── ScenarioManager.java
│   │   ├── ParametroManager.java
│   │   ├── ConflictValidator.java
│   │   └── strategy/                        
│   │
│   └── port/                                
│       │
│       ├── outbound/                       
│             ├── repository/
│         │   ├── IScenarioRepository.java
│         │   ├── IParametroRepository.java
│         │   └── ILogRepository.java
│         │
│         ├── registry/                    
│         │   ├── IDeviceRegistry.java
│         │   └── IStanzaRegistry.java
│         │
│         └── communication/
│             └── ICommunicator.java
│
├── infrastructure/                          # === OUTER LAYER - Frameworks & Drivers ===
│   │
│   ├── persistence/                         # --- DATABASE ---
│   │   │
│   │   ├── mysql/
│   │   │   ├── repository/
│   │   │          ├── ScenarioRepositoryImpl.java
│   │   │          ├── ParametroRepositoryImpl.java
│   │   │          └── LogRepositoryImpl.jav
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
│   │   ├── facade/                         
│   │   │   └── SensoreFacade.java
│   │   │   └── AttuatoreFacade.java
│   │   │
│
├── Factory/                                  # === CONFIGURATION ===
│   │
│   ├── EventBusFactory.java
│
└── shared/                                 
    │
    ├── util/
    │   ├── DateTimeUtils.java
    └── constant/
        └── ApplicationConstants.java

```

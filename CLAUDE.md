# CLAUDE.md - Contesto per Claude Code

## Panoramica del Progetto

SmartDAB e' un sistema domotico basato su architettura Publisher/Subscriber con EventBus centrale.

## Struttura dei Package Principali

```
it.unipv.posfw.smartdab/
├── core/
│   ├── domain/          # Modelli di dominio (Dispositivo, Stanza, Scenario, etc.)
│   ├── port/            # Interfacce (porte) per comunicazione
│   │   ├── communication/   # ICommunicator, Observable, Observer
│   │   ├── device/          # DevicePort
│   │   ├── messaging/       # IEventBusClient
│   │   └── room/            # RoomPort
│   └── service/         # Servizi di dominio (ParametroManager, GestoreStanze, ScenarioManager)
├── infrastructure/
│   └── messaging/       # EventBus, Request, Topic
├── adapter/
│   └── facade/          # AttuatoreFacade, SensoreFacade
└── factory/             # Factory per creazione oggetti
```

## Ultime Modifiche (Gennaio 2026)

### Integrazione ParametroManager con EventBus

**Obiettivo**: Disaccoppiare `ParametroManager` da `EventBus` tramite interfaccia e rispettare il flusso `setRequest()` -> `sendRequest()`.

**File creati**:
- `core/port/messaging/IEventBusClient.java` - Nuova interfaccia che espone solo `setRequest()` e `sendRequest()`

**File modificati**:
- `infrastructure/messaging/EventBus.java` - Implementa `IEventBusClient`
- `core/service/ParametroManager.java` - Usa `IEventBusClient` invece di `EventBus` direttamente

**Dettagli implementazione**:

1. **IEventBusClient** (interfaccia):
   ```java
   public interface IEventBusClient {
       void setRequest(Request request);
       Message sendRequest(Request request);
   }
   ```

2. **ParametroManager** ora usa il flusso corretto:
   ```java
   private boolean inviaComando(Dispositivo dispositivo, DispositivoParameters tipo, IParametroValue valore) {
       Request request = Request.createRequest(dispositivo.getTopic(), "SETPOINT", valore);
       eventBusClient.setRequest(request);
       Message result = eventBusClient.sendRequest(request);
       return result == Message.ACK;
   }
   ```

**Vantaggi**:
- Disaccoppiamento: ParametroManager non conosce i dettagli di EventBus
- Interface Segregation: esposti solo i 2 metodi necessari
- Testabilita': si puo' iniettare un mock di IEventBusClient nei test

## Flusso di Comunicazione

```
ParametroManager
    │
    ├─ impostaParametro(stanzaId, tipoParametro, valore)
    │   └─ inviaComando(dispositivo, tipo, valore)
    │       ├─ Request.createRequest(topic, "SETPOINT", valore)
    │       ├─ eventBusClient.setRequest(request)
    │       └─ eventBusClient.sendRequest(request) → Message
    │
    └─ applicaScenarioConfig(config)
        └─ impostaParametro(...)
```

## Pattern Utilizzati

- **Observer**: Stanza, SensoreFacade, ObservableParameter
- **Command**: ICommand, CommandDispatcher, SwitchCommand
- **Singleton**: EventBus
- **Factory**: ParametroValueFactory, EventBusFactory
- **Facade**: AttuatoreFacade, SensoreFacade
- **Interface Segregation**: IEventBusClient

## Convenzioni

- Topic format: `home/{stanzaId}/{dispositivoId}/{parametro}`
- Request type per comandi: `"SETPOINT"`
- Risposte: `Message.ACK` per successo, `Message.ERROR` per errore

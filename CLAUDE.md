# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

SmartDAB is a smart home domotics system developed as a university project (Università di Pavia). The system enables automated control of home devices (sensors and actuators) through scenarios, with event-driven communication using a Publisher/Subscriber architecture.

## Architecture

### Core Design Pattern: Publisher/Subscriber (Event-Driven)

The system uses an **EventBus** as the central communication hub:
- **Sensors** (Publishers) publish measurements to topics (e.g., `home/livingroom/sensor1/temperature/23.5`)
- **Actuators** (Subscribers) subscribe to specific topics and react to events
- Topic format: `home/<room>/<device>/<parameter>/<payload>`

This architecture provides scalability, flexibility for scenarios, real-time event-driven communication, and maintainability through decoupling.

### Package Structure (Hexagonal/Clean Architecture)

```
src/main/java/it/unipv/posfw/smartdab/
├── core/                          # Business logic (domain-driven)
│   ├── domain/
│   │   ├── model/                 # Domain entities
│   │   │   ├── casa/              # Casa, Stanza (House, Room)
│   │   │   ├── dispositivo/       # Dispositivo (Device) base class
│   │   │   ├── parametro/         # ObservableParameter, parameter values
│   │   │   ├── scenario/          # Scenario, ScenarioStanzaConfig
│   │   │   └── command/           # Command pattern interfaces
│   │   └── enums/                 # DispositivoStates, Message, EnumScenarioType, etc.
│   ├── service/                   # Domain services
│   │   └── GestoreStanze.java     # Room management service
│   └── port/                      # Interfaces for external dependencies
│       ├── communication/         # ICommunicator, Observer pattern
│       ├── device/                # DevicePort
│       └── room/                  # RoomPort
│
├── infrastructure/                # Technical implementations
│   └── messaging/
│       ├── EventBus.java          # Central message broker (Singleton)
│       ├── DispositiviObserver.java
│       ├── request/Request.java
│       └── topic/Topic.java
│
└── adapter/                       # Interface adapters (Hexagonal Architecture)
    ├── facade/
    │   ├── SensoreFacade.java     # Sensor facade
    │   └── AttuatoreFacade.java   # Actuator facade
    └── interfaces/
        └── DispatcherAdapter.java # Command dispatcher adapter
```

**Key Architectural Note**: The nested `src` folder issue has been resolved. All packages now correctly start with `it.unipv.posfw.smartdab` (no `main.java` or `src` in package names).

### Design Patterns in Use

1. **Singleton**: EventBus uses singleton pattern for centralized message management
2. **Observer**: ICommunicator extends Observable; devices observe parameter changes
3. **Command Pattern**: Actuators use command dispatchers to translate system messages into device-specific commands
4. **Facade Pattern**: SensoreFacade and AttuatoreFacade simplify device interaction
5. **Factory Pattern**: `Factory/IParametroValueFactory` creates parameter values (BooleanValue, NumericValue, EnumValue)

## Key Domain Concepts

### Dispositivo (Device) Hierarchy
- Base class: `Dispositivo` - has id, topic, active state, communicator
- Two main types:
  - **Sensori** (Sensors): Publish measurements to EventBus
  - **Attuatori** (Actuators): Subscribe to topics and execute actions

### Casa and Stanza (House and Room)
- `Casa` contains multiple `Stanza` objects
- Each `Stanza` contains multiple `Dispositivo` objects
- `GestoreStanze` service manages room CRUD operations
- **Critical constraint**: Cannot delete a room unless it's empty (no devices)

### Scenario System
- `Scenario` represents automated behavior configurations
- Contains `ScenarioStanzaConfig` for room-specific settings
- Types: PREDEFINITO (predefined) or PERSONALIZZATO (custom)
- Tracks creation and modification timestamps
- Can be activated/deactivated

### Parameter Management
- `ObservableParameter` represents environmental parameters (temperature, humidity, light)
- Parameter values: `BooleanValue`, `NumericValue`, `EnumValue` (all implement `IParametroValue`)
- Factory pattern used for creation

## Git Workflow (IMPORTANT)

This project follows **GitHub Flow** with strict branch protection:

### Branch Protection Rules
- `main` branch is protected - **NO direct pushes allowed**
- All changes must go through Pull Requests
- Requires at least 1 code review approval
- Stale approvals are dismissed when new commits are pushed

### Branch Naming Convention
- `feature/<description>` - New functionality (e.g., `feature/login-oauth`)
- `bugfix/<description>` - Bug fixes (e.g., `bugfix/header-mobile`)
- `refactor/<description>` - Code refactoring without logic changes (e.g., `refactor/db-queries`)

### Commit Message Format
Use **Conventional Commits**:
- `feat: <description>` - New feature
- `fix: <description>` - Bug fix
- `docs: <description>` - Documentation changes

### Daily Workflow
```bash
# Always start from updated main
git switch main
git pull origin main
git switch -c feature/new-feature

# During development (if task takes multiple days)
git pull origin main  # Daily sync to avoid large conflicts
```

### Pull Request Guidelines
- Keep PRs small (max 200-400 lines) for easier review
- Use **Squash and Merge** strategy - all commits squashed into one clean commit on main
- Write descriptive PR descriptions explaining the "why"

## .gitignore Strategy

The project uses `.gitignore` to keep IDE-specific files local:
```
.project
.classpath
.settings/
bin/
```
This allows different developers to use different IDEs (Eclipse, IntelliJ, etc.) without conflicts.

## Build and Development

### Maven Configuration

The project is configured as a **Maven project** with the following setup:
- **GroupId**: `it.unipv.posfw`
- **ArtifactId**: `smartdab`
- **Version**: `1.0.0-SNAPSHOT`
- **Java Version**: 17

### Common Maven Commands

```bash
# Compile the project
mvn clean compile

# Run tests
mvn test

# Build JAR
mvn package

# Clean and install
mvn clean install

# Skip tests during build
mvn clean install -DskipTests
```

### Project Structure (Maven Standard)

```
src/
├── main/
│   ├── java/              # Source code
│   │   └── it/unipv/posfw/smartdab/
│   └── resources/         # Application resources
└── test/
    ├── java/              # Test code (JUnit 5)
    │   └── it/unipv/posfw/smartdab/
    └── resources/         # Test resources
```

### Dependencies

The project uses:
- **JUnit 5.10.1** - Testing framework
- **Mockito 5.8.0** - Mocking framework for tests

### Eclipse Integration

The project includes Eclipse configuration files (`.project`, `.classpath`, `.settings/`) for easy import:
1. Import as existing Maven project
2. Maven will auto-download dependencies
3. See [MAVEN_SETUP.md](MAVEN_SETUP.md) for detailed setup instructions

### Java Version
The code uses Java 17 features including:
- `LocalDateTime` for timestamp management
- Generics and Collections framework
- Interface-based design

## Working with EventBus

The EventBus is the heart of device communication:

### Topic Format
Topics follow a 5-level hierarchy:
```
topicLayers[0] = "home"
topicLayers[1] = room name (e.g., "r1", "soggiorno")
topicLayers[2] = device id
topicLayers[3] = parameter type (e.g., "temperature", "state")
topicLayers[4] = payload/value
```

### Using EventBus
```java
// Method 1: Send request to specific device
eventBus.setRequest(request);
Message response = eventBus.sendRequest(request);

// Method 2: Sensor updates (for publishers)
eventBus.update(payload);  // EventBus notifies all subscribers
```

### Important EventBus Behaviors
- Implements retry logic (up to 10 attempts) for non-responsive devices
- Uses `searchDispositivoByName()` to find devices by ID
- `getSubrscribers()` filters devices by matching room and parameter topic
- Maintains a list of all registered devices

## Device Communication Interface

All devices must implement communication through `ICommunicator`:
```java
public interface ICommunicator extends Observable {
    final String FORMAT = "home/%s/%s/%s/%s";

    Message processRequest(String request, Object val);
    void addObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers(Object args);
}
```

For actuators, use `DispatcherAdapter` which translates system messages into device-specific commands.

## Documentation Location

Additional project documentation is in `Documenti/`:
- `Architettura.md` - Architecture decisions and rationale
- `Package-tree.md` - Detailed package structure
- `Documento di visione.md` - Vision document with requirements
- `Documento dei Casi D'uso.md` - Use cases
- `Requisiti.md` - Requirements specification
- Various UML diagrams (sequence diagrams, domain model, use case diagrams)

## Critical Implementation Notes

1. **Package Names**: All Java packages start with `it.unipv.posfw.smartdab` (the nested `src` issue has been fixed)

2. **Factory Location**: The factory has been moved to `src/main/java/it/unipv/posfw/smartdab/factory/` with correct package name `it.unipv.posfw.smartdab.factory`

3. **Room Deletion Constraint**: `GestoreStanze.eliminaStanza()` enforces that rooms can only be deleted if empty (`s.isEmpty()`)

4. **Scenario State Management**: Scenarios track `isActive`, `data_creazione`, and `data_ultima_modifica` - always update `data_ultima_modifica` when modifying configurations

5. **Device State**: Devices have both `active` (boolean) and `state` (DispositivoStates enum) - don't confuse these two properties

6. **EventBus Singleton**: EventBus should be accessed through `getInstance()` - avoid creating new instances

## Code Style Observations

- Italian naming conventions used throughout (e.g., `Stanza`, `Casa`, `Dispositivo`)
- Comments often in Italian
- Console output for debugging (e.g., `System.out.println()`) present in production code
- Some incomplete implementations (e.g., `ICommand` interface is empty)

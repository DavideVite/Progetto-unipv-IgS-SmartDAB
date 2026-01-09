# Guida Setup Progetto Maven con Eclipse

## Struttura del Progetto

Il progetto è ora configurato come progetto Maven standard con la seguente struttura:

```
Progetto-unipv-IgS-SmartDAB/
├── pom.xml                          # File di configurazione Maven
├── .project                         # File di progetto Eclipse
├── .classpath                       # Classpath Eclipse (integrato con Maven)
├── .settings/                       # Configurazioni Eclipse
│   ├── org.eclipse.jdt.core.prefs
│   └── org.eclipse.m2e.core.prefs
├── src/
│   ├── main/
│   │   ├── java/                    # Codice sorgente principale
│   │   │   └── it/unipv/posfw/smartdab/
│   │   │       ├── adapter/
│   │   │       ├── core/
│   │   │       ├── factory/         # Factory spostato qui
│   │   │       └── infrastructure/
│   │   └── resources/               # Risorse applicazione (config, properties, etc.)
│   └── test/
│       ├── java/                    # Test JUnit
│       │   └── it/unipv/posfw/smartdab/
│       └── resources/               # Risorse per i test
├── target/                          # Cartella build Maven (generata, ignorata da git)
├── Documenti/                       # Documentazione progetto
└── README.md
```

## Setup in Eclipse

### Prerequisiti
1. Eclipse IDE con supporto Maven (m2e plugin)
2. Java JDK 17 o superiore installato

### Primo Import del Progetto

1. **Apri Eclipse**

2. **Importa come progetto Maven esistente:**
   - `File` → `Import...`
   - Seleziona `Maven` → `Existing Maven Projects`
   - Click `Next`
   - Browse alla cartella del progetto: `Progetto-unipv-IgS-SmartDAB`
   - Assicurati che `pom.xml` sia selezionato
   - Click `Finish`

3. **Maven scaricherà automaticamente le dipendenze:**
   - JUnit 5.10.1
   - Mockito 5.8.0
   - Questo può richiedere qualche minuto la prima volta

4. **Aggiorna il progetto Maven (se necessario):**
   - Click destro sul progetto → `Maven` → `Update Project...`
   - Seleziona il progetto
   - Spunta `Force Update of Snapshots/Releases`
   - Click `OK`

### Se Eclipse Mostra Errori

Gli errori che vedi ora (import JUnit non risolti) sono normali e scompariranno dopo che Maven scarica le dipendenze. Se persistono:

1. **Pulisci e ricostruisci il progetto:**
   - `Project` → `Clean...`
   - Seleziona il progetto
   - Click `OK`

2. **Forza aggiornamento Maven:**
   - Click destro sul progetto → `Maven` → `Update Project...`
   - Spunta `Force Update of Snapshots/Releases`
   - Click `OK`

3. **Verifica il JDK:**
   - Click destro sul progetto → `Properties`
   - `Java Build Path` → `Libraries`
   - Assicurati che sia configurato JDK 17
   - Se necessario, modifica in `Java Compiler` la compliance level a 17

## Comandi Maven Utili

### Da Eclipse (Click destro sul progetto → Run As):
- `Maven build...` → Inserisci goal come `clean install`
- `Maven test` → Esegue tutti i test
- `Maven clean` → Pulisce la cartella target
- `Maven install` → Compila e installa nel repository locale

### Da Linea di Comando (Terminal in Eclipse o esterno):

```bash
# Compilare il progetto
mvn clean compile

# Eseguire i test
mvn test

# Creare il JAR
mvn package

# Pulire e compilare tutto
mvn clean install

# Saltare i test (durante lo sviluppo)
mvn clean install -DskipTests

# Vedere l'albero delle dipendenze
mvn dependency:tree

# Aggiornare le dipendenze
mvn clean install -U
```

## File di Configurazione

### pom.xml
Contiene:
- **groupId**: `it.unipv.posfw`
- **artifactId**: `smartdab`
- **version**: `1.0.0-SNAPSHOT`
- **Java version**: 17
- **Dipendenze**: JUnit 5, Mockito
- **Plugin**: compiler, surefire (test), jar

### .gitignore
Configurato per ignorare:
- File Eclipse (`.project`, `.classpath`, `.settings/`, `bin/`)
- File Maven (`target/`, `pom.xml.*`)
- File IDE (IntelliJ, VSCode)
- File OS (`.DS_Store`, `Thumbs.db`)

## Struttura Package Java

Tutti i package seguono la convenzione:
```
it.unipv.posfw.smartdab.<modulo>.<subpackage>
```

Esempi:
- `it.unipv.posfw.smartdab.core.domain.model.casa`
- `it.unipv.posfw.smartdab.infrastructure.messaging`
- `it.unipv.posfw.smartdab.factory`

## Note Importanti

1. **Factory spostato**: La cartella `Factory/` nella root è stata spostata in `src/main/java/it/unipv/posfw/smartdab/factory/` con package corretto

2. **Target sempre ignorato**: La cartella `target/` è generata da Maven e non va committata su Git

3. **Settings Eclipse**: I file `.project`, `.classpath`, e `.settings/` sono inclusi nel progetto per facilitare il setup, ma sono normalmente ignorati. Questo è accettabile per progetti universitari piccoli.

4. **Java 17**: Il progetto è configurato per Java 17. Se hai una versione diversa, modifica `maven.compiler.source` e `maven.compiler.target` nel `pom.xml`

## Aggiungere Dipendenze

Per aggiungere nuove dipendenze, modifica il `pom.xml` nella sezione `<dependencies>`:

```xml
<dependency>
    <groupId>group-id</groupId>
    <artifactId>artifact-id</artifactId>
    <version>version</version>
</dependency>
```

Poi aggiorna il progetto Maven in Eclipse.

## Troubleshooting

### "Project configuration is not up-to-date with pom.xml"
- Click destro sul progetto → `Maven` → `Update Project...`

### "The import org.junit cannot be resolved"
- Aspetta che Maven scarichi le dipendenze
- Verifica la connessione internet
- `Maven` → `Update Project...` con `Force Update`

### "Build path contains duplicate entries"
- Pulisci il progetto: `Project` → `Clean...`
- Ricostruisci il progetto

### Modifiche al pom.xml non riconosciute
- Salva il file pom.xml
- Eclipse dovrebbe rilevare automaticamente i cambi
- Se no: `Maven` → `Update Project...`

## Prossimi Passi

1. ✅ Setup progetto Maven completato
2. ⬜ Importare in Eclipse
3. ⬜ Verificare che non ci siano errori di compilazione
4. ⬜ Eseguire `mvn clean test` per verificare il setup
5. ⬜ Iniziare a scrivere i test per le classi esistenti
6. ⬜ (Opzionale) Aggiungere una classe Main se necessario

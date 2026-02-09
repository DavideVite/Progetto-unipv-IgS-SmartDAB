# SmartDAB

> **Sistema domotico intelligente per la gestione di abitazioni smart.**
> Progetto universitario - Università di Pavia (IgS)

---

## Panoramica

SmartDAB è un sistema software Java per la gestione di una **smart home**. Permette di:
- Gestire stanze e dispositivi (sensori e attuatori)
- Creare e attivare scenari personalizzati
- Controllare parametri ambientali (temperatura, umidità, luminosità, ecc.)
- Comunicazione event-driven tramite architettura Publisher/Subscriber

---

## Documentazione

| Documento | Descrizione |
|:----------|:------------|
| [Architettura](./Documenti/Architettura.md) | Architettura Publisher/Subscriber e diagrammi |
| [Requisiti](./Documenti/Requisiti.md) | Requisiti funzionali e non funzionali |
| [Casi d'Uso](./Documenti/Documento%20dei%20Casi%20D'uso.md) | Descrizione dettagliata dei casi d'uso |
| [Documento di Visione](./Documenti/Documento%20di%20visione%20.md) | Obiettivi e visione del progetto |
| [Glossario](./Documenti/Glossario.md) | Definizioni dei termini di dominio |
| [Struttura Package](./Documenti/Package-tree.md) | Albero dei package del progetto |

**Diagrammi:** disponibili in [`Documenti/Diagrammi/`](./Documenti/Diagrammi/)

---

## Installazione

### Prerequisiti
- Java JDK 17+
- Maven 3.8+
- MySQL

### Setup Database
```bash
mysql -u root -p < database/SQL_script_finale.sql
```

Configurare le credenziali in `src/main/resources/db.properties`:
```properties
db.url=jdbc:mysql://localhost:3306/smartdab
db.user=your_user
db.password=your_password
```

### Compilazione
```bash
mvn clean install
```

### Esecuzione
```bash
mvn exec:java -Dexec.mainClass="it.unipv.posfw.smartdab.Main"
```

Oppure da IDE, eseguire `MainController.mostraApplicazione()`:
```java
SwingUtilities.invokeLater(() -> {
    MainController controller = new MainController();
    controller.mostraApplicazione();
});
```

---

## Architettura

Il progetto segue un'**architettura esagonale** (Ports & Adapters) con pattern **MVC** per l'UI.

```
src/main/java/it/unipv/posfw/smartdab/
├── core/           # Dominio e servizi
│   ├── domain/     # Entità (Casa, Stanza, Dispositivo, Scenario)
│   ├── service/    # Logica di business
│   └── port/       # Interfacce per comunicazione esterna
├── adapter/        # Facade per sensori e attuatori
├── infrastructure/ # Persistenza (DAO) e messaging (EventBus)
├── factory/        # Factory per creazione oggetti
└── ui/             # Interfaccia Swing (View + Controller)
```

### Pattern principali
- **MVC** - Separazione UI/logica
- **Observer** - Notifica cambiamenti parametri
- **Publisher/Subscriber** - Comunicazione dispositivi via EventBus
- **Factory** - Creazione dispositivi da POJO
- **Command** - Azioni sui dispositivi

---

## Funzionalità principali

| Funzionalità | Stato |
|:-------------|:------|
| CRUD Stanze | ✅ |
| CRUD Dispositivi | ✅ |
| EventBus (pub/sub) | ✅ |
| Impostazione parametri manuali | ✅ |
| CRUD Scenari | ✅ |
| Attivazione/disattivazione scenari | ✅ |
| Scenari predefiniti (Notte, Giorno, Assenza) | ✅ |
| Interfaccia grafica Swing | ✅ |

---

## Autori

- Alessandro Geremia Ingenito
- Davide Vitello
- Beatrice Bertone

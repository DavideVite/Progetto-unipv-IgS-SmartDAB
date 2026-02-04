# Architettura

L’architettura del sistema è ibrida, in quanto è presente una organizzazione del codice in strati orizzontali, ed ogni livello, con responsabilità specifiche, può comunicare solo con i livelli adiacenti.

Il progetto è un ibrido tra:
Architettura a layer tradizionale (dipendenze top-down con violazioni)
Event-driven (EventBus pub/sub)



Nei paragrafi seguenti si descrivono i principali aspetti architetturali

## 2.1 Architettura a layer

Ha un’architettura a layer::
Presentation Layer: Interfaccia utente (UI).
utilizzo di Swing 
uso di Controllers
Business Logic Layer (BLL): Regole di business e logica applicativa.
con la presenza di vari Gestori (es: ScenarioManager)
Data Access Layer (DAL): Comunicazione con il Database.
Database: Lo storage fisico.
Ma, la porzione dell’EventBus è pub/sub. 

Motivazione della scelta:
L'adozione di un'architettura a layer permette la Separazione delle Responsabilità (Separation of Concerns). Questo facilita la manutenibilità: una modifica alla logica di salvataggio nel database (DAL) non impatta il codice dell'interfaccia grafica. Inoltre, rende il sistema più testabile, permettendo di verificare la logica di business isolandola dalla UI e dal DB.

 ```
┌─────────────────────────────────────┐
│           UI LAYER                  │
│   View (Swing) ←→ Controller        │
└──────────────┬──────────────────────┘
               │ chiama direttamente
               ▼
┌─────────────────────────────────────┐
│         SERVICE LAYER               │
│  GestoreStanze, ScenarioManager     │
│  ParametroManager, DispositiviManager│
└──────────────┬──────────────────────┘
               │ istanzia direttamente
               ▼
┌─────────────────────────────────────┐
│      DATA ACCESS LAYER              │
│   StanzaDAOImpl, DispositivoDAOImpl │
│   DatabaseConnection (Singleton)    │
└─────────────────────────────────────┘
 ```

## 2.2 Il Presentation Layer è attraverso MVC.

Il Presentation Layer adotta il pattern Model–View–Controller (MVC):
le View sono implementate tramite componenti Swing e si occupano della resa grafica e dell’interazione con l’utente;
i Controller gestiscono gli eventi provenienti dalla UI, aggiornano il modello e coordinano le chiamate verso la logica di business;
il Model rappresenta lo stato dell’applicazione esposto alla UI e viene aggiornato a partire dalla Business Logic.
Questa organizzazione separa chiaramente le responsabilità tra presentazione, controllo del flusso e gestione dei dati, migliorando manutenibilità e testabilità.
Motivazione della scelta:
Disaccoppiando la logica di presentazione (View) da quella di controllo (Controller), si ottiene un codice più pulito e modulare, facilitando eventuali aggiornamenti futuri dell'interfaccia.

## 2.3 Componente event-driven (EventBus pub/sub)

Per favorire il disaccoppiamento tra componenti e per gestire in modo efficiente la comunicazione tra i numerosi dispositivi, il sistema utilizza un EventBus interno.
Questa architettura consente di disaccoppiare completamente i client: 
- i sensori (publisher) pubblicano le proprie rilevazioni su topic specifici (es. soggiorno/temperatura, cucina/luminosità),
- gli attuatori (subscriber) ricevono notifiche solo per i topic a cui sono iscritti. 
Questo introduce elementi di architettura event-driven, riducendo le dipendenze dirette tra moduli e semplificando l’integrazione di nuove funzionalità che si basano sugli stessi eventi.

Motivazione della scelta:
In un sistema puramente a layer, la comunicazione "dal basso verso l'alto" o tra componenti dello stesso livello è difficile e crea dipendenze rigide. L'introduzione dell'EventBus serve a garantire il basso accoppiamento (Loose Coupling). Moduli diversi possono reagire allo stesso evento (es. "SalvataggioCompletato") senza doversi conoscere direttamente o referenziare l'uno con l'altro, migliorando l'estensibilità del sistema.


![](img/publisher_subscriber_architecture.png)

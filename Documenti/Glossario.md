# Glossario di Progetto — Smart DAB

**Authors:** Alessandro Geremia Ingenito, Davide Vitello, Beatrice Bertone

---

## Entità principali

| Termine | Definizione (Contesto di Business) |
|:---|:---|
| **Casa** | Abitazione gestita dal sistema. Contiene l'insieme delle stanze configurate dall'utente. |
| **Stanza** | Ambiente fisico dell'abitazione (es. soggiorno, camera). Contiene dispositivi e mantiene le misurazioni dei parametri ambientali rilevati.| 
| **Hub** | Pannello di gestione centrale della casa. Rappresenta il punto di accesso fisico al sistema ed è il componente attraverso cui l'utente interagisce con Smart DAB. Esiste in istanza unica per abitazione.|
| **Dispositivo** | Apparecchio elettronico installato in una stanza. Si specializza in sensore o attuatore. Ogni dispositivo è identificato da un topic di comunicazione e possiede uno stato operativo.|
| **Sensore** | Dispositivo che rileva parametri ambientali (temperatura, umidità, luminosità, ecc.) e pubblica le misurazioni verso il sistema centrale. Viene attivato automaticamente alla configurazione.|
| **Attuatore** | Dispositivo che modifica lo stato fisico di una stanza (es. pompa di calore, lampada, umidificatore) in risposta a comandi manuali, scenari o regole.|
| **Scenario** | Insieme di configurazioni predefinite che coinvolgono una o più stanze. Viene attivato **manualmente** dall'utente. Es. "Scenario Notte": temp 18°C, umidità 45%, illuminazione minima.|
| **Scenario predefinito** | Scenario fornito con il sistema alla prima installazione, basato su best practice di comfort abitativo (Notte, Giorno, Assenza). Può essere modificato o eliminato.|
| **Scenario personalizzato** | Scenario creato dall'utente secondo le proprie preferenze.|
| **Regola** | Meccanismo automatico che associa una condizione di attivazione (es. soglia di temperatura, orario) a uno scenario. Quando la condizione è soddisfatta, il sistema attiva lo scenario collegato senza intervento dell'utente. | Non ancora modellata come classe dedicata. Prevista come evoluzione futura. |
| **Configurazione stanza** | Singola impostazione all'interno di uno scenario: definisce il valore desiderato di un parametro per una determinata stanza. | 
| **Parametro** | Grandezza fisica misurabile o controllabile all'interno di una stanza (es. temperatura, umidità, luminosità). Ogni parametro ha un tipo (numerico, booleano, enumerativo) e vincoli di range. |
| **Misura** | Singola rilevazione effettuata da un sensore in un dato istante. Viene salvata nel database per storico e statistiche. |

---

## Comunicazione e infrastruttura

| Termine | Definizione (Contesto di Business) | 
|:---|:---|
| **Event Bus** | Componente software centrale che gestisce tutte le comunicazioni tra dispositivi secondo un modello publish/subscribe. Riceve le misurazioni dai sensori e inoltra i comandi agli attuatori. | 
| **Topic** | Indirizzo di instradamento dei messaggi nel formato. Identifica univocamente il canale di comunicazione di un dispositivo per un dato parametro. | 
| **Request** | Messaggio scambiato tra componenti del sistema. Contiene il topic di destinazione, il tipo di operazione e il valore. |
| **Comunicatore** | Componente software integrato in ogni dispositivo che traduce i messaggi del sistema in comandi specifici per quel dispositivo. Garantisce l'aggiunta di nuovi dispositivi con logica interna diversa. | 
| **Comando** | Azione eseguibile su un dispositivo (es. accensione, cambio stato, aggiornamento parametro). Segue il pattern Command. | 
| **Dispatcher** | Componente che riceve una request e la smista al comando appropriato in base al tipo di operazione richiesto. | 

---

## Gestione e monitoraggio

| Termine | Definizione (Contesto di Business) | 
|:---|:---|
| **Impostazione manuale** | Azione con cui l'utente modifica direttamente un parametro di una stanza (es. alza la temperatura a 22°C). Il sistema individua l'attuatore idoneo e invia il comando. 
| **Dispositivo idoneo** | Attuatore attivo presente nella stanza che supporta il parametro richiesto dall'utente o dallo scenario. |
| **Stato dispositivo** | Condizione operativa corrente di un dispositivo. |

---

## Note

- I termini **scenario** e **regola** sono concetti distinti: lo scenario è attivabile solo manualmente, la regola è il meccanismo automatico che lega una condizione a uno scenario.


**SMART DAB**

**Authors:** Alessandro Geremia Ingenito  
	     Davide Vitello  
	     Beatrice Bertone

**1\. Introduzione**   
***i. scopo del documento***  
Lo scopo di questo documento è quello di descrivere e riportare i requisiti del progetto Smart DAB. Altro obiettivo fondamentale è quello di mostrare quali sono i principali usi del sistema e le funzionalità richieste dal documento di visione sul sistema

***ii. scopo del prodotto***   
Smart DAB ha l’obiettivo di migliorare la vivibilità e il comfort delle case nelle quali viene installato, tramite l’utilizzo di dispositivi che interagiscono insieme seguendo una logica ben definita. L’obiettivo è quello di fornire un’esperienza confortevole e che dia sempre all’utente in qualunque momento lo desideri, il controllo sulla propria abitazione. Quando l’utente ha delle abitudini precise, può impostare delle regole personalizzate per realizzare il proprio spazio di comfort senza dover reimpostare manualmente le stesse condizioni di volta in volta. Il prodotto agisce in totale autonomia una volta che queste regole sono ben definite. A quel punto sarà compito di Smart DAB far sì che l’utente possa godersi la propria abitazione in modo sicuro, semplice e senza particolari sforzi.

***iii. definizioni, acronimi e abbreviazioni*** 

- **sensore** : dispositivo che rileva parametri ambientali e invia le misurazioni al sistema.  
- **attuatore** : dispositivo che esegue azioni fisiche in risposta a comandi manuali, scenari o regole.   
- **scenario** : insieme di azioni predefinite, attivabili manualmente dall’utente, che coinvolgono una o più stanze e dispositivi.  
- **regola** : meccanismo automatico che attiva azioni al verificarsi di condizioni specifiche.   
- **event bus** : ricevitore e trasmettitore di segnali per i dispositivi

***iv. riferimenti*** 

***v. descrizione del resto del documento***   
Il documento è suddiviso in diverse parti. La prima parte include lo scopo del documento, del prodotto e le varie definizioni delle terminologie tecniche presenti all’interno del documento. La seconda parte mostra la prospettiva e le funzionalità del prodotto,  le caratteristiche tipiche dell’utente che interagisce col sistema, i vincoli tecnici del sistema e i presupposti sui quali si basa. Infine la terza parte contiene, in modo ordinato, l’insieme di tutti i requisiti funzionali e non funzionali del sistema.

**2\. Descrizione generale**   
***i. prospettiva del prodotto***   
Smart DAB è un sistema software per la gestione di una casa intelligente che consente agli utenti di monitorare ambienti, dispositivi e parametri domestici.   
Il sistema supporta funzionalità di gestione delle stanze, configurazione e controllo dei dispositivi, creazione di scenari e regole, monitoraggio di consumi energetici e consultazione di statistiche e previsioni.

***ii. funzioni del prodotto***   
Il sistema consentirà di:

* Avere un sistema di autenticazione e comunicazione tra dispositivi sicuro  
* Gestire i parametri della propria casa (Temperatura, umidità, ecc.)  
* Gestire gli scenari in modo semplice e trasparente  
* Avere un registro con tutte le comunicazioni tra dispositivi  
* Inserire qualunque dispositivo compatibile  
* Ricevere notifiche in casi di malfunzionamenti

***iii. caratteristiche utente***   
Il tipico utente di Smart DAB è qualcuno che abbia acquistato una smart home e non abbia alcuna specifica competenza tecnica o informatica

***iv. vincoli generali***   
Secondo quanto riportato nel documento di visione del progetto, il sistema non presenterebbe alcun particolare vincolo stringente. Alcune  criticità riguardano le comunicazioni che devono essere affidabili e l’hub deve essere in grado di gestire diversi messaggi da vari dispositivi contemporaneamente senza bloccarsi. Tutto ciò per garantire che eventuali segnalazioni importanti possano essere prese in tempo e si possa intervenire tempestivamente alla risoluzione delle problematiche.

***v. presupposti e dipendenze***   
Si assume che nella smart home reale ci sia un sistema di comunicazione fisico o wireless tra i vari dispositivi seguendo gli standard del settore (es. ZigBee). Per motivi pratici il sistema seguirà un processo di simulazione non perfettamente concorde con le prestazioni che potrebbe avere in una vera smart home.

**3\. Requisiti specifici** 

### ***Funzionali:***

**Requisiti utente:**

*RFU1:*

* Una volta installato il sistema l’utente potrà configurare dei dispositivi specificando la stanza in cui sono posizionati.  
* E’ possibile inoltre disabilitare un dispositivo installato in precedenza.

*RFU2:*

* All’avviamento del sistema, dopo aver effettuato l’autenticazione, l’utente può creare una nuova stanza inserendo il nome e i metri quadri attraverso un'interfaccia grafica.  
* L’utente può modificare il nome e i metri quadri di una stanza esistente oppure eliminare una stanza della casa.

*RFU3:*

* L'utente può impostare manualmente i parametri desiderati (es. illuminazione, temperatura) per una stanza specifica e il sistema attiverà i dispositivi idonei presenti nella stanza attraverso un pannello.   
* L’utente può selezionare la stanza di interesse tra tutte quelle dell’abitazione tramite l’interfaccia dell’hub.

*RFU4:*

* L’utente deve avere la possibilità di visualizzare su un’opportuna interfaccia grafica tutte le informazioni aggiornate di stanze, dispositivi e scenari presenti.

*RFU5:*

* l'utente è in grado di definire degli scenari personalizzati che coinvolgono una o più stanze. Quando l’utente crea il nuovo scenario seleziona i dispositivi e ne imposta il valore desiderato per quel particolare caso. 

*RFU6:*

* l'utente può attivare uno scenario esistente (personalizzato o predefinito).

*RFU7:*

* Al primo avvio del sistema, l’utente deve inserire la password fornita dal produttore per poter accedere alla configurazione. Dopo il primo avviamento l’utente deve scegliere un PIN che gli permette il completo accesso al sistema.   
* In assenza di PIN, gli utenti non possono gestire creazione ed eliminazione stanze, creare nuovi scenari e gestire procedure di configurazione/eliminazione dei singoli dispositivi. 

*RFU8:*

* l'utente può creare degli scenari che si attivano automaticamente in base a condizioni ambientali o eventi (es. soglia di temperatura, movimento, orario specifico). 

*RFU9:*

* Dall'interfaccia principale, l’utente accede alla lista delle automazioni esistenti visualizzando lo stato corrente(attivo/disattivo). L’utente può attivare, sospendere temporaneamente, eliminare o modificare le regole. 

*RFU10:*

* l'utente viene informato di incompatibilità tra scenari/regole. In casi critici è il sistema a prendere le decisioni. 

*RFU11:*

* Attraverso l'interfaccia grafica l’utente può visualizzare, cliccando un dispositivo, il consumo energetico di quest’ultimo.  
* Se l’utente lo desidera potrà inoltre visualizzare una classifica dei dispositivi in base al loro consumo energetico.

*RFU12:*

* L’utente può visualizzare previsioni sui principali parametri della casa, tra cui: temperatura nelle diverse stanze e stato generale della casa nelle prossime 24 ore.

*RFU13:*

* L’utente può visualizzare statistiche relative alle misurazioni dei sensori (es. temperatura, umidità, consumi).  
* L’utente può visualizzare lo storico delle azioni eseguite all’interno del sistema (es. attivazione scenari, modifiche ai parametri).

*RFU14:*

* L’utente deve ricevere delle notifiche, in caso di guasti o malfunzionamenti dei dispositivi, su tutte le hub della casa e sullo smartphone dell’utente.

*RFU15:*

* Le richieste e le impostazioni dell’utente devono essere eseguite in maniera rigida evitando errori di comunicazione e perdite di richieste, qualora queste ultime risultino valide e non pericolose.

**Requisiti di sistema:**

*RFS1:*

* I dispositivi configurati devono essere inseriti dal sistema nella stanza di appartenenza e disabilitati qualora fosse richiesto da quest’ultimo.  
* Un dispositivo correttamente registrato deve essere inserito nella base di dati apposita e disabilitato qualora venisse richiesto dal sistema.  
* Il sistema deve verificare che il dispositivo configurato sia compatibile e non già esistente  
* Un sensore configurato correttamente deve essere attivato subito dal sistema, mentre gli attuatori rimangono inattivi finché non vengono attivati dal sistema

*RFS2:*

* Il sistema deve validare i dati inseriti dall’utente: il nome della stanza deve essere univoco.  
* Prima di eliminare una stanza, il sistema deve verificare se contiene dispositivi e informare l’utente.  
* Se la stanza viene eliminata o modificata, il sistema segnala eventuali dipendenze con parametri configurati o scenari attivi.  
* Il sistema aggiorna l'interfaccia per mostrare le stanze attualmente presenti.  
* le stanze vengono salvate nel database di sistema

*RFS3:*

* L’utente accede al pannello di controllo della stanza o dell’hub centrale   
* Una volta selezionata la stanza, il sistema deve mostrare solo i parametri modificabili per quella stanza (basandosi sui dispositivi installati).  
* Il sistema deve fornire un'interfaccia per incrementare, decrementare o impostare un valore specifico per il parametro selezionato  
* Il sistema chiede la conferma della modifica  
* Alla conferma il sistema invia il comando al dispositivo idoneo 

*RFS4:*

* Il sistema deve aggiornare periodicamente le informazioni in suo possesso attraverso un opportuno “comunicatore” collegato ai dispositivi (che comunica lo stato attuale del dispositivo) e tenendo traccia delle nuove modifiche e/o aggiunte di scenari e stanze da parte dell’utente  
* Gli scenari non completi sono segnalati a livello dell’interfaccia grafica.   
* La visualizzazione deve essere fatta attraverso 3 finestre che portano a interfacce diverse, ognuna delle quali visualizza delle informazioni specifiche (p.e. stanze e dispositivi contenuti)

*RFS5:*

* gli scenari vengono salvati nel database di sistema   
* il database di sistema contiene anche scenari predefiniti presenti alla prima attivazione del sistema (es. "scenario notte": temp 20°, umidità 45%, illuminazione minima) che possono essere modificati o cancellati in un secondo momento   
* Il sistema valida che non vi siano conflitti logici nello scenario (es. riscaldamento ON e condizionamento ON nella stessa stanza).

*RFS6:*

* all’attivazione, il sistema invia i comandi a tutti i dispositivi inclusi nello scenario   
* l’attivazione manuale di uno scenario ha una priorità uguale rispetto all’impostazione manuale. Quindi sovrascrive lo stato precedente (cioè l'ultimo comando inviato prevale sullo stato precedente dei dispositivi)  
* il sistema verifica la disponibilità dei dispositivi richiesti. Nel caso un dispositivo non funzionasse o non fosse più presente, i dispositivi disponibili si attiveranno lo stesso. Il sistema invierebbe una notifica per segnalare l'elemento guasto/assente. 

*RFS7:*

* Il sistema richiede la password iniziale fornita all’utente.  
* Successivamente l’utente sceglie un PIN.   
* Il sistema deve validare la lunghezza del PIN, i caratteri ammessi e memorizzare i PIN in modo sicuro   
* Il sistema blocca temporaneamente l’accesso dopo un numero definito di tentativi errati.  
* Il sistema mostra messaggi in caso di PIN o password errati.

*RFS8:*

* il sistema lega la condizione specifica allo scenario   
* il sistema valida la coerenza della regola in fase di creazione (es. prevenzione di loop infiniti o comandi contraddittori).

*RFS9:*

* Il sistema monitora continuamente le condizioni e attiva le regole quando soddisfatte  
* Se il sistema non rileva degli attuatori, funzionerà attivando quelli disponibili  
* Se il sistema non rileva il sensore alla base della condizione di attivazione, la regola viene sospesa automaticamente e non può essere attivata

*RFS10:*

* il sistema identifica e classifica le incompatibilità secondo 2 categorie: conflitti tollerabili (warning) e conflitti potenzialmente gravi (error)  
* Il sistema gestisce il conflitto manuale-scenario informando l'utente che l’impostazione manuale disattiva la regola per quel dispositivo   
* in situazioni pericolose il sistema agisce senza l’intervento dell’utente 

*RFS11:*

* Ogni dispositivo deve fornire il proprio consumo energetico su richiesta del sistema  
* Il sistema suggerisce un uso ottimizzato dei dispositivi presenti 

*RFS12:*

* Il sistema deve aggiornare le previsioni con una frequenza regolare.  
* Il sistema deve presentare le previsioni in modo chiaro tramite grafici e valori numerici.   
* Le previsioni devono adattarsi automaticamente se vengono attivati scenari che influenzano i parametri. 

*RFS13:*

* Il sistema deve salvare in un database locale tutte le misurazioni provenienti dai sensori e tutte le azioni rilevanti effettuate dall’utente o dal sistema.  
* Il sistema deve garantire conservazione sicura dei dati nel tempo evitando perdite.  
* Il sistema deve essere in grado di calcolare medie, massimi/minimi, trend e generare rappresentazioni grafiche o tabelle.

*RFS14:*

* Il sistema deve verificare periodicamente lo stato e le comunicazioni dei dispositivi in uso per agire in caso di comportamenti anomali registrati.  
* Il sistema dovrà notificare l’utente tempestivamente qualora si verificasse un comportamento anomalo.  
* Qualora il comportamento anomalo dovesse persistere il sistema dovrà rimuovere il dispositivo interessato dalla rete

*RFS15:*

* Il sistema deve assicurarsi che le comunicazioni vadano a buon fine e, qualora venissero riscontrati dei problemi il sistema deve intervenire ristabilendo la comunicazione.  
* Qualunque richiesta di dispositivi estranei, fittizi o malevoli deve essere eliminata.  
* Il sistema deve gestire le comunicazioni attraverso uno standard opportuno

### ***Non funzionali:***

* L’interfaccia grafica deve essere gestita in maniera tale da essere intuitiva e senza che ci siano rischi di blocco  
* Il sistema deve essere scritto in linguaggio Java  
* La base di dati dovrà essere gestita con MySQL  
* Le interrogazioni alla base di dati devono essere effettuate attraverso linguaggio SQL  
* Il sistema deve essere ultimato entro fine Gennaio 2026
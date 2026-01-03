
# Introduzione

Il progetto è quello di una “smart home”, un ambiente composto da diversi dispositivi che collaborano al fine di migliorare la qualità della vita quotidiana di ogni persona nella casa offrendo un’esperienza domestica confortevole, funzionale e automatizzabile.
La gestione tradizionale di un’abitazione richiede interventi manuali continui. La Smart Home rende la propria casa capace di adattarsi alle abitudini delle persone, riconoscendo situazioni particolari e agendo autonomamente, risparmiando tempo e attenzione e permettendo di monitorare i consumi ed evitare sprechi energetici. 
Questo può risultare un grande valore aggiunto sull’acquisto di un’abitazione che adotta tale sistema.

# Dispositivi


Al primo avviamento l'utente dovrà inserire il numero e la tipologia delle stanze della propria casa. In futuro, potrà comunque rimuoverle, aggiungerle o modificarle. Tuttavia, per garantire la coerenza del sistema, la rimozione di una stanza sarà possibile solo previa verifica dell'assenza di dispositivi attivi al suo interno. 
Tutte le procedure avvengono attraverso dei pannelli di gestione touch che sono collocati nelle singole stanze.
Il sistema prevede una gerarchia di accesso: al primo avvio sarà richiesta una password iniziale fornita dal produttore. Successivamente, l'utente definirà un PIN che potrà inserire negli hub per avere l’accesso completo al sistema. Sarà presente anche un accesso senza PIN che consentirà solo l’impostazione manuale di parametri e l’attivazione di scenari.
L'utente può configurare un qualsiasi nuovo dispositivo compatibile e connetterlo a tutta la rete di dispositivi già preesistente. La procedura avviene specificando la stanza in cui viene messo il dispositivo e la tipologia di dispositivo (es. illuminazione oppure temperatura).
I dispositivi possono essere di 2 categorie: sensori o attuatori.
I sensori rilevano parametri ambientali come temperatura, luminosità e umidità, mentre gli attuatori consentono di eseguire azioni specifiche come accendere luci, regolare il riscaldamento o attivare dispositivi di sicurezza.
Gli attuatori sono dispositivi in grado di cambiare lo stato delle stanze in base alle necessità degli utenti. Ne sono esempi la pompa di calore o l’umidificatore. 
La gestione dei dispositivi avviene tramite un’interfaccia grafica semplice che sia applicabile anche a piccoli palmari.

Se il nuovo dispositivo inserito è un sensore allora entrerà subito in funzione; nel caso di un attuatore, verrà tenuto spento fino all’attivazione manuale dell’utente o automatica.
Tutti i sensori inviano le loro misure ad un sistema centrale che le integrerà tra di loro e attiverà gli attuatori in base alle impostazioni presenti. Inoltre salverà i valori e li elaborerà per valutare eventuali anomalie.

L'utente è in grado di settare manualmente la temperatura o altri parametri, qualora possegga i dispositivi idonei.

Il sistema deve essere utilizzabile sia da utente domestico singolo sia da più utenti che vivono nella stessa casa (come le famiglie). 

# Automazioni

Un aspetto cardine del sistema è la creazione di scenari, cioè un’attivazione coordinata di vari dispositivi in base alle preferenze preimpostate dall’utente.
Una volta creato lo scenario deve essere reso operativo dall’utente. 

Le configurazioni possono valere per la singola stanza o per tutta la casa. 

Il sistema viene fornito con una serie di scenari predefiniti pronti all'uso, progettati sulla base delle migliori pratiche di comfort abitativo:
Scenario Notte: temperatura a 18°C, umidità al 45%, illuminazione minima
Scenario Giorno: temperatura a 21°C, umidità tra 40-50%, illuminazione naturale integrata
Scenario Assenza: temperatura ridotta a 16°C, luci spente, attivazione sensori di sicurezza

Tutti gli scenari predefiniti possono essere modificati o eliminati dall'utente secondo le proprie preferenze. Prima dell'attivazione, il sistema verifica automaticamente la disponibilità dei dispositivi necessari; se alcuni dispositivi sono assenti o non funzionanti, lo scenario si attiverà parzialmente con i dispositivi disponibili, notificando all'utente delle limitazioni.
Durante la creazione o modifica di uno scenario, il sistema esegue automaticamente controlli di coerenza per prevenire configurazioni illogiche. Nello specifico:
impedisce l’attivazione simultanea di dispositivi che lavorano in antagonismo 
verifica che i valori impostati siano nei range sicuri
Se vengono rilevati conflitti logici, l’utente viene informato e deve risolvere le incongruenze prima di poter salvare lo scenario. 

L'utente è anche in grado di attivare gli scenari in base a determinate condizioni dinamiche come la rilevazione di una temperatura eccessiva o il rilevamento di un movimento in casa. 

Gli utenti devono poter ricevere da parte del sistema delle previsioni (ad esempio sulla temperatura, sui tempi di riscaldamento del forno, sul tempo di riempimento della vasca, ecc.). Inoltre si può visualizzare lo stato della casa nelle prossime 24 ore. 


L'utente può leggere le regole esistenti con il relativo stato (attivo/disattivo) e inoltre può eliminarle o modificarle. 

Il controllo manuale e le automazioni dei singoli dispositivi possono entrare in conflitto.  Il sistema è in grado di classificare questi conflitti in due categorie: 'tollerabili' (dove l'utente viene avvisato ma può procedere, es. manuale vs automatico) e 'gravi' (dove il sistema interviene autonomamente per prevenire danni o situazioni pericolose).

É presente un meccanismo di risoluzione conflitti automatico. Quando si verifica un conflitto tra controllo manuale e automazione: 
L’impostazione manuale ha priorità e disattiva temporaneamente il pilotaggio automatico per quel specifico dispositivo 
 Il dispositivo entra in "modalità manuale" e rimane in tale stato fino a quando l'utente non ripristina esplicitamente la modalità automatica 
L'utente può ripristinare il controllo automatico per singoli dispositivi o per tutta la casa con un comando dedicato 
Il sistema mantiene traccia dello stato (manuale/automatico) di ogni dispositivo e lo visualizza nell'interfaccia

 
In ogni momento, l'utente deve essere in grado di visualizzare tutti i dispositivi al momento attivi e se stanno lavorando in modalità automatica o manuale. L'utente può vedere lo stato di tutte le stanze della casa in cui sono posizionati dei sensori e può vedere anche lo stato generale della casa. Quando l’utente lo desidera deve poter accedere a statistiche sulle misurazioni dei sensori, inclusa la possibilità di visualizzare una classifica dei dispositivi in base al loro consumo energetico, per identificare le fonti di maggiore spesa e storici delle azioni intraprese dai sensori in una modalità semplice da comprendere e intuitiva. Il sistema dovrà essere in grado di salvare i dati provenienti dai vari sensori in un database locale che permette una maggiore privacy.

# Manutenzione

Nei casi di malfunzionamenti del sistema, l'utente può rivolgersi ad un tecnico che interviene sui dispositivi potendo accedere a delle informazioni diagnostiche complete sui componenti, che tipicamente sono nascoste all'utente. 
Serve dunque un sistema di autenticazione per stabilire degli accessi in facoltà di utente o tecnico. 
In caso di guasti e malfunzionamenti da parte del dispositivo l'utente dovrà essere avvisato attraverso un messaggio che apparirà sul pannello di gestione e attraverso un messaggio sul telefono. E’ auspicabile che quest’ultimo notifichi problematiche legate a malfunzionamenti. 
Se il problema non si risolve in breve tempo, verrà mandato un secondo messaggio e avvisato il tecnico più vicino. 
Qualora il comportamento anomalo dovesse persistere, il software provvederà a isolare e rimuovere il dispositivo dalla rete. L'utente sarà guidato nella procedura di rimozione sicura e sostituzione in caso di guasto.

# Comunicazione e sicurezza

La comunicazione tra i dispositivi deve avvenire garantendo che ciascuno di questi possegga dei componenti software specifici e compatibili con la logica del sistema.
Ogni dispositivo dovrà avere una logica di comunicazione integrata e sviluppata separatamente dalle sue funzionalità, per le interazioni con l’event bus del sistema (componente software del sistema che gestisce le comunicazioni). Inoltre ogni attuatore dovrà avere un “traduttore” di messaggi del sistema in comandi, per garantire un’aggiunta semplificata di nuovi dispositivi con logica interna diversa da quella standard del sistema.
Per rendere il sistema più generico bisogna implementare un formato standard per le comunicazioni che ogni comunicatore deve utilizzare.
Bisogna inoltre assicurare una comunicazione affidabile e inequivocabile tra i componenti per evitare intrusioni esterne e per quanto possibile rilevare malfunzionamenti inattesi. 
Deve essere presente un controllo sulle comunicazioni, in maniera tale da evitare alterazioni dei messaggi inviati in partenza corretti.

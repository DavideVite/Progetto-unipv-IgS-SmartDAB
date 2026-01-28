package it.unipv.posfw.smartdab.infrastructure.messaging;

import java.util.ArrayList;
import java.util.Iterator;

import it.unipv.posfw.smartdab.adapter.facade.AttuatoreFacade;
import it.unipv.posfw.smartdab.core.domain.enums.Message;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.Dispositivo;
import it.unipv.posfw.smartdab.core.port.messaging.IEventBusClient;
import it.unipv.posfw.smartdab.infrastructure.messaging.request.Request;

public class EventBus implements DispositiviObserver, IEventBusClient {
	private ArrayList<Dispositivo> dispositivi = new ArrayList<>();
	private static EventBus instance = null;
	private Request request;
	
	/* Come chiamare l'event bus:
	 * 1) setRequest()
	 * 2) sendRequest() <- Richiede la conoscenza della richiesta da mandare, seguendo notazione
	 * 3) Restituisce un Messaggio che informa sulla buona riuscita dell'operazione
	 * 
	 * Oppure per i sensori:
	 * 
	 * 1) update()
	 * 2) L'event bus aggiorna tutti i subrscriber
	 */
	
	
	// Quando event bus riceve una request = topic (home/room/dispositivo/parameter) + type + val
	
	public void setRequest(Request request) {
		this.request = request;
	}
	
	public Dispositivo searchDispositivoByName(String name) {
		Iterator<Dispositivo> iter = dispositivi.iterator();
		Dispositivo d;
		
		while(iter.hasNext()) {
			d = iter.next();
			if(name.equals(d.getTopic().getId())) {
				return d;
			}
		}
		
		System.out.println("Dispositivo " + name + " non trovato");
		return null;
	}
	
	public ArrayList<Dispositivo> getSubscribers() {
		Iterator<Dispositivo> iter = dispositivi.iterator();
		ArrayList<Dispositivo> subs = new ArrayList<>();
		Dispositivo d;
		
		while(iter.hasNext()) {
			d = iter.next();
			if(request.getTopic().getParameter().equals(d.getTopic().getParameter()) &&
			   request.getTopic().getRoom().equals(d.getTopic().getRoom()) && 
			   request.getTopic().getId().equals(d.getTopic().getId())) {
				
				// Se il dispositivo è un attuatore lo inserisco, altrimenti passo avanti
				
				try {
					d = (AttuatoreFacade)d;
					subs.add(d);
				} catch(ClassCastException e) {
					continue;
				}

			}
		}
		return subs;
	}
	
	public Message sendRequest(Request request) {
		return searchDispositivoByName(request.getTopic().toString()
				).getCommunicator().processRequest(request);
	}
	
	private EventBus() {

	}
	
	// Singleton
	public static EventBus getInstance() {
		if(instance == null) {
			instance = new EventBus();
		}
		
		return instance;
	}

	
	// Metodo esclusivo dei sensori che misurano dei parametri
	
	// topic = "home/room/sensore/parameter"
	// type = "UPDATE.PARAMETER"
	// val = payload
	
	@Override
	public Message update(Request request) {
		
		// Verifico se il messaggio arrivato è pertinente alla funzionalità
		if(request.getType().equals(Message.UPDATE + "." + Message.PARAMETER)) {
			setRequest(request);

			// Verifica se il parametro esiste nella stanza...

			// Prendi tutti gli iscritti al topic
			ArrayList<Dispositivo> subs = getSubscribers();
			Iterator<Dispositivo> iterSubs = subs.iterator();

			while(iterSubs.hasNext()) {
				// Manda richiesta
				for(int i = 0; i < 10; i++) {
					if(sendRequest(request).equals(Message.ACK)) break;

					// Se il dispositivo non risponde a 10 chiamate allora vado al prossimo
					else if(i == 9) 
						System.out.println("Dispositivo " + request.getTopic().getId() + " non ha risposto");
				}
			}
		}
		
		return Message.ACK;
		
	}
}

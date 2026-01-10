package main.java.it.unipv.posfw.smartdab.infrastructure.messaging;

import java.util.ArrayList;
import java.util.Iterator;


import main.java.it.unipv.posfw.smartdab.core.domain.enums.Message;
import main.java.it.unipv.posfw.smartdab.core.domain.model.dispositivo.Dispositivo;
import main.java.it.unipv.posfw.smartdab.infrastructure.messaging.request.Request;

public class EventBus implements DispositiviObserver {
	private ArrayList<Dispositivo> dispositivi = new ArrayList<>();
	private EventBus instance = null;
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
	
	
	// Quando event bus riceve una misura (home/r1/d1/parameter/payload = topic + value)
	public void setRequest(Request request) {
		this.request = request;
	}
	
	public Dispositivo searchDispositivoByName(String name) {
		Iterator<Dispositivo> iter = dispositivi.iterator();
		Dispositivo d;
		
		while(iter.hasNext()) {
			d = iter.next();
			if(name.equals(d.getId())) {
				return d;
			}
		}
		
		System.out.println("Dispositivo " + name + " non trovato");
		return null;
	}
	
	public ArrayList<Dispositivo> getSubrscribers() {
		Iterator<Dispositivo> iter = dispositivi.iterator();
		ArrayList<Dispositivo> subs = new ArrayList<>();
		Dispositivo d;
		
		while(iter.hasNext()) {
			d = iter.next();
			if(request.getTopic().getParameterByTopic().equals(d.getTopic().getParameterByTopic()) &&
				request.getTopic().getRoomByTopic().equals(d.getTopic().getRoomByTopic()) && 
			   request.getTopic().getIdByTopic().equals(d.getId())) {
				
				subs.add(d);
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
	public EventBus getInstance() {
		if(instance == null) {
			instance = new EventBus();
		}
		
		return instance;
	}
	
	@Override
	public Message update(Request request) {
		
		setRequest(request);
		
		if(request.getTopic().length== 4 && request.getVal() != null) {
			// Verifica se il parametro esiste nella stanza...

			// Prendi tutti gli iscritti al topic
			ArrayList<Dispositivo> subs = getSubrscribers();
			Iterator<Dispositivo> iterSubs = subs.iterator();

			while(iterSubs.hasNext()) {
				// Manda richiesta
				for(int i = 0; i < 10; i++) {
					if(sendRequest(request).equals(Message.ACK)) break;

					// Se il dispositivo non risponde a 10 chiamate allora vado al prossimo
					else if(i == 9) 
						System.out.println("Dispositivo " + request.getTopic().getIdByTopic() + " non ha risposto");
				}
			}
		}
		
		return Message.ACK;
		
	}
}

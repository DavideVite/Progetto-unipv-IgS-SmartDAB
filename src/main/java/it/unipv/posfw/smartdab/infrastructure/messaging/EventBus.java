package it.unipv.posfw.smartdab.infrastructure.messaging;

import java.util.ArrayList;
import java.util.Iterator;


import it.unipv.posfw.smartdab.core.domain.enums.Message;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.Dispositivo;
import it.unipv.posfw.smartdab.infrastructure.messaging.request.Request;

public class EventBus implements DispositiviObserver {
	private ArrayList<Dispositivo> dispositivi = new ArrayList<>();
	private EventBus instance = null;
	private Request request;
	private String[] topicLayers;
	
	/* Notazioni: 
	 * topicLayers[0] = "home" 
	 * topicLayers[1] = "room" 
	 * topicLayers[2] = "device" 
	 * topicLayers[3] = "parameter" 
	 * Es. msgFormato = MAIN.SUB.val 
	 */
	
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
		setTopicLayers();
	}
	
	private void setTopicLayers() {
		topicLayers = request.getTopic().toString().split("/");
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
			if(topicLayers[3].equals(d.getTopic().getParameterByTopic()) &&
				topicLayers[1].equals(d.getTopic().getRoomByTopic()) && 
			   !topicLayers[2].equals(d.getId())) {
				
				subs.add(d);
			}
		}
		return subs;
	}
	
	public void removeRequest() {
		request.clear();
	}
	
	// Es. request = CONFIG.SETPOINT.value
	public Message sendRequest(Request request) {
		return searchDispositivoByName(request.getTopic().toString())
				.getCommunicator().processRequest(request.getType(), request.getVal());
	}
	
	private EventBus() {
		// TEMPORANEAMENTE COMMENTATO PER COMPILAZIONE - request è null
		// request.clear();
	}
	
	// Singleton
	public EventBus getInstance() {
		if(instance == null) {
			instance = new EventBus();
		}
		
		return instance;
	}
	
	@Override
	public Message update(String payload) {
		// TEMPORANEAMENTE STUB PER COMPILAZIONE
		// Implementazione originale commentata sotto - contiene metodi e variabili mancanti
		return Message.ACK;
	}

	/* IMPLEMENTAZIONE ORIGINALE COMMENTATA - Metodi e variabili mancanti
	 * Problemi:
	 * - setPayload(payload) non esiste
	 * - topicFormat non è dichiarata
	 * - longFormat non è dichiarata
	 *
	public Message update(String payload) {
		// update verrà usato per gestire carichi a 5 livelli

		setPayload(payload);

		if(topicLayers.length == 5) {
			if(topicLayers[3].equals("state")) {
				// Comunica con l'hub...
			}
			else {
				// Verifica se il parametro esiste nella stanza...

				// Prendi tutti gli iscritti al topic
				ArrayList<Dispositivo> subs = getSubrscribers();
				Iterator<Dispositivo> iterSubs = subs.iterator();

				while(iterSubs.hasNext()) {
					// Manda richiesta
					for(int i = 0; i < 10; i++) {
						if(sendRequest(topicFormat.formatted(topicLayers[1], iterSubs.next().getId()),
								       longFormat.formatted(Message.CONFIG, Message.STATE,
								       topicLayers[4])).equals(Message.ACK)) break;

						// Se il dispositivo non risponde a 10 chiamate allora vado al prossimo
						else if(i == 9) System.out.println("Dispositivo " + topicLayers[2] + " non ha risposto");
					}
				}
			}
		}

		return Message.ACK;

	}
	*/
}

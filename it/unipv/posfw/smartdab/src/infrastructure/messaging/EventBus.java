package it.unipv.posfw.smartdab.src.infrastructure.messaging;

import java.util.ArrayList;
import java.util.Iterator;

import it.unipv.posfw.smartdab.src.core.domain.enums.Message;
import it.unipv.posfw.smartdab.src.core.domain.model.dispositivo.Dispositivo;

public class EventBus implements DispositiviObserver {
	private ArrayList<Dispositivo> dispositivi = new ArrayList<>();
	private EventBus instance = null;
	private String payload;
	private String[] payloadLayers;
	
	/* Notazioni: 
	 * payloadLayers[0] = "home" 
	 * payloadLayers[1] = "room" 
	 * payloadLayers[2] = "device" 
	 * payloadLayers[3] = "parameter" 
	 * payloadLayers[4] = "value" 
	
	 * topic = payloadLayers[0:2]
	 * Es. msgFormato = MAIN.SUB.val 
	 */
	
	/* Come chiamare l'event bus:
	 * 1) setPayload()
	 * 2) sendRequest() <- Richiede la conoscenza della richiesta da mandare, seguendo notazione
	 * 3) Restituisce un Messaggio che informa sulla buona riuscita dell'operazione
	 * 
	 * Oppure per i sensori:
	 * 
	 * 1) update()
	 * 2) L'event bus aggiorna tutti i subrscriber
	 */
	
	public final String topicFormat = "home/%s/%s";
	public final String longFormat = "%s.%s.%s";
	public final String shortFormat = "%s.%s";
	
	// Quando event bus riceve una misura (home/r1/d1/parameter/payload = topic + value)
	public void setPayload(String payload) {
		this.payload = payload;
		setPayloadLayers();
	}
	
	private void setPayloadLayers() {
		payloadLayers = getTopicByPayload(payload).split("/");
	}
	
	private String getTopicByPayload(String payload) {
		String[] payloadLayers = payload.split("/");
		return topicFormat.formatted(payloadLayers[1], payloadLayers[2]);
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
			if(payloadLayers[3].equals(d.getParameterByTopic()) &&
				payloadLayers[1].equals(d.getRoomByTopic()) && 
			   !payloadLayers[2].equals(d.getId())) {
				
				subs.add(d);
			}
		}
		return subs;
	}
	
	public void removePayload() {
		payload = "";
	}
	
	// Es. request = CONFIG.SETPOINT.value
	public Message sendRequest(String topic, String request) {
		return searchDispositivoByName(topic).getCommunicator().processRequest(request);
	}
	
	private EventBus() {
		payload = "";
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
		// update verrà usato per gestire carichi a 5 livelli
		
		setPayload(payload);
		
		if(payloadLayers.length == 5) {
			if(payloadLayers[3].equals("state")) {
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
						if(sendRequest(topicFormat.formatted(payloadLayers[1], iterSubs.next().getId()),
								       longFormat.formatted(Message.CONFIG, Message.STATE, 
								       payloadLayers[4])).equals(Message.ACK)) break;
						
						// Se il dispositivo non risponde a 10 chiamate allora vado al prossimo
						else if(i == 9) System.out.println("Dispositivo " + payloadLayers[2] + " non ha risposto");
					}
				}
			}
		}
		
		return Message.ACK;
		
	}
}

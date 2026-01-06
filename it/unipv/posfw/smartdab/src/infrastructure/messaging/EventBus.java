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
	public final String topicFormat = "home/%s/%s";
	
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
			if(name == d.getId()) {
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
	public Message sendRequest(String topic, String request, Object args) {
		
		return Message.ACK;
	}
	
	private EventBus() {
		payload = "";
	}
	
	public EventBus getInstance() {
		if(instance == null) {
			instance = new EventBus();
		}
		
		return instance;
	}
	
	@Override
	public void update(String payload) {
		// update verrà usato per gestire carichi a 5 livelli
		
		setPayload(payload);
		if(payloadLayers.length == 5) {
			if(payloadLayers[3].equals("state")) {
				// Comunica con l'hub...
			}
			else {
				// Verifica se il parametro esiste nella stanza...
				ArrayList<Dispositivo> subs = getSubrscribers();
				Iterator<Dispositivo> iterSubs = subs.iterator();
				
				
				while(iterSubs.hasNext()) {
					// Manda richiesta
				}
			}
		}
		
	}
}

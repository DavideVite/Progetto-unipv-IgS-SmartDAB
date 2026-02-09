package it.unipv.posfw.smartdab.infrastructure.messaging.request;

import java.util.Arrays;
import java.util.List;

import it.unipv.posfw.smartdab.core.domain.enums.Message;
import it.unipv.posfw.smartdab.infrastructure.messaging.topic.Topic;

/**
 * Classe che definisce la struttura di una richiesta
 * @author Alessandro Ingenito
 * @version 1.0
 */

public class Request {
	private Topic topic;
	private String type;
	private Object val;

	private Request(Topic topic, String type, Object val) {
		this.topic = topic;	// Controllo integrato in topic
		this.type = type;
		this.val = val;
	}
	
	
	/**
	 * Questo metodo fa sì che una richiesta possa essere istanziata solo se i parametri inseriti sono validi
	 */
	
	public static Request createRequest(Topic topic, String type, Object val) throws IllegalArgumentException {
		if(verifyArguments(topic, type, val)) {
			return new Request(topic, type, val);
		}
		
		else {
			throw new IllegalArgumentException("Parametri inseriti non validi");
		}
	}
	
	private static boolean verifyArguments(Topic topic, String type, Object val) {
		
		// Prima verifica su parametri non nulli
		
		// Se type è null cortocircuito il .equals su "" che darebbe un'eccezione
		if(topic != null && type != null && !type.equals("") && val != null) {
			List<Message> msg = Arrays.asList(Message.values());
			
			// Seconda verifica sul type dato da composizione di Message
			for(String layer: type.split("\\.")) {
				if(!msg.contains(Message.valueOf(layer))) return false;
			}
			
			return true;
		}
		
		return false;
	}
	
	public void setRequest(Topic topic, String type, Object val) {
		if(verifyArguments(topic, type, val)) {
			this.topic = topic;
			this.type = type;
			this.val = val;
		}
		
		else System.out.println("Parametri inseriti non corretti");
	}
	
	public Topic getTopic() {
		return topic;
	}
	
	public String getType() {
		return type;
	}
	
	public Object getVal() {
		return val;
	}
}

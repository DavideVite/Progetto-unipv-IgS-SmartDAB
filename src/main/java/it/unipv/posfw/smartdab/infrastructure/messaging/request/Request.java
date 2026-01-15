package it.unipv.posfw.smartdab.infrastructure.messaging.request;

import it.unipv.posfw.smartdab.infrastructure.messaging.topic.Topic;

public class Request {
	private Topic topic;
	private String type;
	private Object val;

	private Request(Topic topic, String type, Object val) {
		this.topic = topic;	// Controllo integrato in topic
		this.type = type;
		this.val = val;
	}
	
	public static Request createRequest(Topic topic, String type, Object val) throws IllegalArgumentException {
		if(verifyArguments(topic, type, val)) {
			return new Request(topic, type, val);
		}
		
		else {
			throw new IllegalArgumentException("Parametri inseriti non validi");
		}
	}
	
	private static boolean verifyArguments(Topic topic, String type, Object val) {
		if(topic != null && !type.equals("") && val != null) return true;
		
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

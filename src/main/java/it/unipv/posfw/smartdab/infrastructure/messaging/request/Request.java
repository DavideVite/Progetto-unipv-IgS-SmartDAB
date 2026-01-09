package it.unipv.posfw.smartdab.infrastructure.messaging.request;

import it.unipv.posfw.smartdab.infrastructure.messaging.topic.Topic;

public class Request {
	private Topic topic;
	private String type;
	private Object val;

	public Request() {
		topic = new Topic();
		type = "";
		val = null;
	}
	
	public Request(Topic topic) {
		this.topic = topic;
		type = "";
		val = null;
	}
	
	public Request(Topic topic, String type) {
		this.topic = topic;
		this.type = type;
		val = null;
	}
	
	public Request(Topic topic, String type, Object val) {
		this.topic = topic;
		this.type = type;
		this.val = val;
	}
	
	public Topic getTopic() {
		return topic;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public Object getVal() {
		return val;
	}
	
	public void setVal(Object val) {
		this.val = val;
	}
	
	public void clear() {
		topic.clear();
		type = "";
		val = null;
	}
}

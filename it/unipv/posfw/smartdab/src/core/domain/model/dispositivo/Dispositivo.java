package it.unipv.posfw.smartdab.src.core.domain.model.dispositivo;

import java.util.ArrayList;
import java.util.List;

import it.unipv.posfw.smartdab.src.core.domain.enums.DispositivoStates;
import it.unipv.posfw.smartdab.src.core.port.communication.ICommunicator;
import it.unipv.posfw.smartdab.src.core.port.communication.observer.Observable;
import it.unipv.posfw.smartdab.src.core.port.communication.observer.Observer;

public class Dispositivo implements Observable {
	private String id;
	private String topic;	// home/room/dispositivo/parameter
	private boolean active;
	private DispositivoStates state;
	private ICommunicator c;
	private List<Observer> observers = new ArrayList<>();

	public Dispositivo(String id, ICommunicator c, boolean active) {
		this.id = id;
		this.c = c;
		this.active = active;
		topic = "";
		state = DispositivoStates.ALIVE;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
		
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		
		// conto i livelli del topic
		String[] result = topic.split("/");
		
		// topic_livelloDispositivo = home/room/id/parameter
		if(result.length == 4 && result[0].equals("home") && result[2].equals(id)) this.topic = topic;
		else System.out.println("Topic non valido");
	}

	public DispositivoStates getState() {
		return state;
	}

	public void setState(DispositivoStates state) {
		this.state = state;
	}
	
	public ICommunicator getCommunicator() {
		return c;
	}
	
	public void setCommunicator(ICommunicator c) {
		this.c = c;
	}
	
	public void switchDispositivo() {
		active = !active;
	}
	
	public String getParameterByTopic() {
		return topic.split("/")[3];
	}
	
	public String getRoomByTopic() {
		return topic.split("/")[1];
	}
	
	// Osservazione dell'event bus o altri eventuali osservatori
	public void addObserver(Observer observer) {
		observers.add(observer);
	}
	
	public void removeObserver(Observer observer) {
		observers.remove(observer);
	}
	
	public void notifyObservers(Object args) {
		c.sendPayload((String)args);
	}
}

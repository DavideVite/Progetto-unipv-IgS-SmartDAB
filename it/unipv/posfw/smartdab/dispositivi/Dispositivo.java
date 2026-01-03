package it.unipv.posfw.smartdab.dispositivi;

import java.util.ArrayList;
import java.util.List;

import it.unipv.posfw.smartdab.interfaces.Observable;
import it.unipv.posfw.smartdab.interfaces.Observer;

public class Dispositivo implements Observable {
	private String id;
	private String topic;
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
		this.topic = topic;
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
	
	public void switchDispositivo() {
		active = !active;
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

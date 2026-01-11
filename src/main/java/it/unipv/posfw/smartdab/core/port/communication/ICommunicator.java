package main.java.it.unipv.posfw.smartdab.core.port.communication;

import main.java.it.unipv.posfw.smartdab.core.domain.enums.Message;
import main.java.it.unipv.posfw.smartdab.core.port.communication.observer.Observable;
import main.java.it.unipv.posfw.smartdab.core.port.communication.observer.Observer;
import main.java.it.unipv.posfw.smartdab.infrastructure.messaging.request.Request;

public interface ICommunicator extends Observable{
	
	public Message processRequest(Request request);
	// Osservazione dell'event bus o altri eventuali osservatori
	
	public void addObserver(Observer observer);
	
	public void removeObserver(Observer observer);
	
	public void notifyObservers(Object args);
}

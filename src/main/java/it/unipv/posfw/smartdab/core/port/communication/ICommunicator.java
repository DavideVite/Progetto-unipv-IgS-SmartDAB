package it.unipv.posfw.smartdab.core.port.communication;

import it.unipv.posfw.smartdab.core.domain.enums.Message;
import it.unipv.posfw.smartdab.core.port.communication.observer.Observable;
import it.unipv.posfw.smartdab.core.port.communication.observer.Observer;
import it.unipv.posfw.smartdab.infrastructure.messaging.request.Request;

public interface ICommunicator extends Observable{
	
	public Message processRequest(Request request);
	// Osservazione dell'event bus o altri eventuali osservatori
	
	public void addObserver(Observer observer);
	
	public void removeObserver(Observer observer);
	
	public void notifyObservers(Object args);
}

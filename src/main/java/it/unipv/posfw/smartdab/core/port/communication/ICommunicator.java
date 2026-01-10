package it.unipv.posfw.smartdab.core.port.communication;

import it.unipv.posfw.smartdab.core.domain.enums.Message;
import it.unipv.posfw.smartdab.core.port.communication.observer.Observable;
import it.unipv.posfw.smartdab.core.port.communication.observer.Observer;

public interface ICommunicator extends Observable{
	
	// es. home/stanza1/disp1/param1/val
	final String FORMAT = "home/%s/%s/%s/%s";
	
	public Message processRequest(String request, Object val);
	// Osservazione dell'event bus o altri eventuali osservatori
	
	public void addObserver(Observer observer);
	
	public void removeObserver(Observer observer);
	
	public void notifyObservers(Object args);
}

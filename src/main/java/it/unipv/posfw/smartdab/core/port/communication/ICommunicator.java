package it.unipv.posfw.smartdab.core.port.communication;

import it.unipv.posfw.smartdab.core.domain.enums.Message;
import it.unipv.posfw.smartdab.infrastructure.messaging.DispositiviObserver;
import it.unipv.posfw.smartdab.infrastructure.messaging.request.Request;

public interface ICommunicator {
	
	public Message processRequest(Request request);
	// Osservazione dell'event bus o altri eventuali osservatori
	
	public void addObserver(DispositiviObserver observer);
	
	public void removeObserver(DispositiviObserver observer);
	
	public void notifyObservers(Object args);
}

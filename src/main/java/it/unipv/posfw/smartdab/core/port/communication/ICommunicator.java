package it.unipv.posfw.smartdab.core.port.communication;

import it.unipv.posfw.smartdab.core.domain.enums.Message;
import it.unipv.posfw.smartdab.infrastructure.messaging.DispositiviObserver;
import it.unipv.posfw.smartdab.infrastructure.messaging.request.Request;

/**
 * Interfaccia che ogni comunicatore deve implementare per essere compatibile col sistema SmartDAB
 * @see DispositiviObserver
 * @author Alessandro Ingenito
 * @version 1.0
 */

public interface ICommunicator {
	
	/**
	 * Metodo per ricevere le richieste e processarle
	 * @param request richiesta da processare
	 * @return restituisce un messaggio inerente alla buona riuscita del processing
	 */
	
	public Message processRequest(Request request);
	
	// Osservazione dell'event bus o altri eventuali osservatori
	
	public void addObserver(DispositiviObserver observer);
	
	public void removeObserver(DispositiviObserver observer);
	
	public void notifyObservers(Object args);
}

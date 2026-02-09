package it.unipv.posfw.smartdab.core.port.communication.observer;

/**
 * Pattern Observer: Observable
 * @author Alessandro Ingenito
 * @version 1.0
 */

public interface Observable {

	public void addObserver(Observer observer);
	public void removeObserver(Observer observer);
	public void notifyObservers(Object args);
}

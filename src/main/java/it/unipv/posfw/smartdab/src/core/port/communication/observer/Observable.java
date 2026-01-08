package it.unipv.posfw.smartdab.src.core.port.communication.observer;

public interface Observable {

	public void addObserver(Observer observer);
	public void removeObserver(Observer observer);
	public void notifyObservers(Object args);
}

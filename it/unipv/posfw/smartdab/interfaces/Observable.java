package it.unipv.posfw.smartdab.interfaces;

public interface Observable {

	public void addObserver(Observer observer);
	public void removeObserver(Observer observer);
	public void notifyObservers(Object args);
}

package it.unipv.posfw.smartdab.interfaces;

import java.util.EventObject;

public interface Observable {

	void addObserver(Observer observer);
	
	void removeObserver(Observer observer);
	
	void notifyObservers(Object args);
}

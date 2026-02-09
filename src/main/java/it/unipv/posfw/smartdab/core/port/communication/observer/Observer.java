package it.unipv.posfw.smartdab.core.port.communication.observer;

/**
 * Pattern Observer: Observer
 * @author Alessandro Ingenito
 * @version 1.0
 */

public interface Observer {
	public void update(Observable o, Object args);
}

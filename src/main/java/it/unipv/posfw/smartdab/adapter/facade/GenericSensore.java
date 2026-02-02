package it.unipv.posfw.smartdab.adapter.facade;

import it.unipv.posfw.smartdab.core.port.communication.ICommunicator;
import it.unipv.posfw.smartdab.core.port.communication.observer.Observable;
import it.unipv.posfw.smartdab.infrastructure.messaging.topic.Topic;

/**
 * Sensore generico utilizzato come fallback quando non esiste
 * un'implementazione specifica per il parametro richiesto.
 * Pubblica la misura ricevuta tramite il communicator.
 */
public class GenericSensore extends SensoreFacade {

	private double valore;

	public GenericSensore(Topic topic, ICommunicator c) {
		super(topic, c);
	}

	@Override
	public void update(Observable o, Object args) {
		try {
			valore = (double) args;
			publishMeasure();
		} catch (ClassCastException e) {
			System.out.println("Valore non valido per il sensore generico");
		}
	}

	@Override
	public void publishMeasure() {
		this.getCommunicator().notifyObservers(valore);
	}

	@Override
	public int action(Object args) {
		publishMeasure();
		return 0;
	}
}

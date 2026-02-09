package it.unipv.posfw.smartdab.core.domain.model.dispositivo.sensori.Termometro;

import it.unipv.posfw.smartdab.adapter.facade.SensoreFacade;
import it.unipv.posfw.smartdab.core.port.communication.StandardCommunicator;
import it.unipv.posfw.smartdab.core.port.communication.observer.Observable;
import it.unipv.posfw.smartdab.infrastructure.messaging.topic.Topic;

/**
 * Implementazione di un semplice sensore di temperatura
 * @author Alessandro Ingenito
 * @version 1.0
 */

public class Termometro extends SensoreFacade {

	private double temp;
	public final int noise_sensitivity = 2;
	public final double error = 0.05;
	
	public Termometro(Topic topic, StandardCommunicator c) {
		super(topic, c);
		
		c.setDispositivo(this);
	}
	
	/**
	 * La misura del termometro introduce in questo caso un errore gaussiano 
	 * di circa il 5% dello stato attuale
	 */
	
	@Override
	public void update(Observable o, Object args) {
		try {
			
			// Applico un rumore gaussiano sul errore% del valore reale
			double noise = (Math.random() * noise_sensitivity - noise_sensitivity / 2) * error * (double) args;
			temp = Math.floor(((double) args + noise) * 10) / 10;
			
			publishMeasure();
			
		} catch(ClassCastException e) {
			System.out.println("Il parametro da aggiornare non è conforme al tipo temperatura");
		}
	}

	@Override
	public void publishMeasure() {
		this.getCommunicator().notifyObservers(temp);
	}

	@Override
	public int action(Object args) {
		publishMeasure();
		return 0;
	}

}

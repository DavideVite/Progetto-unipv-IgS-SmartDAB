package it.unipv.posfw.smartdab.adapter.facade;

import it.unipv.posfw.smartdab.core.domain.model.dispositivo.Dispositivo;
import it.unipv.posfw.smartdab.core.port.communication.ICommunicator;
import it.unipv.posfw.smartdab.core.port.communication.observer.Observer;
import it.unipv.posfw.smartdab.infrastructure.messaging.topic.Topic;


/**
 * Questa classe astratta è una facade ai vari dispositivi di tipo sensore esistenti
 * @see Dispositivo
 * @see Observer
 * @see ICommunicator
 * @author Alessandro Ingenito
 * @version 1.1
 */

public abstract class SensoreFacade extends Dispositivo implements Observer {
	
	public SensoreFacade(Topic topic, ICommunicator c) {
		super(topic, c, true);
	}
	
	/**
	 * Questo metodo astratto deve essere implementato da ogni sensore per definire il modo 
	 * in cui effettua la misura
	 */
	
	public abstract void publishMeasure();
}

package it.unipv.posfw.smartdab.adapter.facade;

import it.unipv.posfw.smartdab.core.domain.model.dispositivo.Dispositivo;
import it.unipv.posfw.smartdab.core.port.communication.ICommunicator;
import it.unipv.posfw.smartdab.core.port.communication.observer.Observer;
import it.unipv.posfw.smartdab.infrastructure.messaging.topic.Topic;


public abstract class SensoreFacade extends Dispositivo implements Observer {
	
	public SensoreFacade(Topic topic, ICommunicator c) {
		super(topic, c, true);
	}
	
	// Modalità di pubblicazione della misura dipende dal particolare sensore
	public abstract void publishMeasure();
}

package it.unipv.posfw.smartdab.adapter.facade;

import it.unipv.posfw.smartdab.core.domain.model.dispositivo.Dispositivo;
import it.unipv.posfw.smartdab.core.port.communication.ICommunicator;
import it.unipv.posfw.smartdab.core.port.communication.observer.Observable;
import it.unipv.posfw.smartdab.core.port.communication.observer.Observer;


public abstract class SensoreFacade extends Dispositivo implements Observer {
	
	public SensoreFacade(String id, ICommunicator c) {
		super(id, c, true);
	}
	
	public abstract void publishMeasure();
}

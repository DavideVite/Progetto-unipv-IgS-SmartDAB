package it.unipv.posfw.smartdab.core.domain.model.dispositivo;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoStates;
import it.unipv.posfw.smartdab.core.port.communication.ICommunicator;
import it.unipv.posfw.smartdab.core.port.device.DevicePort;
import it.unipv.posfw.smartdab.infrastructure.messaging.topic.Topic;

public abstract class Dispositivo implements DevicePort {
	private Topic topic;	// home/room/dispositivo/parameter
	private boolean active;
	private DispositivoStates state;
	private ICommunicator c;

	public Dispositivo(Topic topic, ICommunicator c, boolean active) {
		this.topic = topic;
		this.c = c;
		this.active = active;
		state = DispositivoStates.ALIVE;
	}
	
	public Topic getTopic() {
		return topic;
	}

	public void setTopic(Topic topic) {
		this.topic = topic;
	}

	public DispositivoStates getState() {
		return state;
	}

	public void setState(DispositivoStates state) {
		this.state = state;
	}
	
	public ICommunicator getCommunicator() {
		return c;
	}
	
	public void setCommunicator(ICommunicator c) {
		if(c != null) this.c = c;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public void switchDispositivo() {
		active = !active;
	}
	
	// Il dispositivo fa qualcosa, ma dipende dalla specializzazione
	public abstract int action(Object args);
}

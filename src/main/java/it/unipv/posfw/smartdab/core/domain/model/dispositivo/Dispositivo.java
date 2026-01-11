package main.java.it.unipv.posfw.smartdab.core.domain.model.dispositivo;

import main.java.it.unipv.posfw.smartdab.core.domain.enums.DispositivoStates;
import main.java.it.unipv.posfw.smartdab.core.port.communication.ICommunicator;
import main.java.it.unipv.posfw.smartdab.core.port.device.DevicePort;
import main.java.it.unipv.posfw.smartdab.infrastructure.messaging.topic.Topic;

public class Dispositivo implements DevicePort {
	private String id;
	private Topic topic;	// home/room/dispositivo/parameter
	private boolean active;
	private DispositivoStates state;
	private ICommunicator c;

	public Dispositivo(String id, ICommunicator c, boolean active) {
		this.id = id;
		this.c = c;
		this.active = active;
		state = DispositivoStates.ALIVE;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		if(!id.equals("")) this.id = id;
	}

	public Topic getTopic() {
		return topic;
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
		this.c = c;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public void switchDispositivo() {
		active = !active;
	}
}

package it.unipv.posfw.smartdab.core.domain.model.dispositivo;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoStates;
import it.unipv.posfw.smartdab.core.port.communication.ICommunicator;
import it.unipv.posfw.smartdab.core.port.device.DevicePort;
import it.unipv.posfw.smartdab.infrastructure.messaging.topic.Topic;

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

	public boolean setId(String id) {
		if(checkId(id)) {
			this.id = id.toLowerCase();
			return true;
		}
		
		return false;
	}
	
	private boolean checkId(String id) {
		String idx = "[A-Za-z]{1,17}[0-9]{0,3}";
		return id.matches(idx);
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
		if(c != null) this.c = c;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public void switchDispositivo() {
		active = !active;
	}
}

package main.java.it.unipv.posfw.smartdab.src.core.domain.model.dispositivo;

import main.java.it.unipv.posfw.smartdab.src.core.domain.enums.DispositivoStates;
import main.java.it.unipv.posfw.smartdab.src.core.port.communication.ICommunicator;
import main.java.it.unipv.posfw.smartdab.src.core.port.device.DevicePort;

public class Dispositivo implements DevicePort {
	private String id;
	private String topic;	// home/room/dispositivo/parameter
	private boolean active;
	private DispositivoStates state;
	private ICommunicator c;

	public Dispositivo(String id, ICommunicator c, boolean active) {
		this.id = id;
		this.c = c;
		this.active = active;
		topic = "";
		state = DispositivoStates.ALIVE;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
		
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		
		// conto i livelli del topic
		String[] result = topic.split("/");
		
		// topic_livelloDispositivo = home/room/id/parameter
		if(result.length == 4 && result[0].equals("home") && result[2].equals(id)) this.topic = topic;
		else System.out.println("Topic non valido");
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
	
	public String getParameterByTopic() {
		return topic.split("/")[3];
	}
	
	public String getRoomByTopic() {
		return topic.split("/")[1];
	}
}

package it.unipv.posfw.smartdab.infrastructure.messaging.topic;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameters;
import it.unipv.posfw.smartdab.core.port.device.DevicePort;
import it.unipv.posfw.smartdab.core.port.room.RoomPort;

public class Topic {
	private String home;
	private String room;
	private String id;
	private DispositivoParameters parameter;
	public final int length = 4; // Numero di Layers
	public final String TOPIC_FORMAT = "%s/%s/%s/%s";
	
	private Topic(RoomPort room, DevicePort dispositivo, DispositivoParameters parameter) {
		home = "home";
		this.room = room.getID();
		id = dispositivo.getId();
		this.parameter = parameter;
	}
	
	public static Topic createTopic(RoomPort room, DevicePort dispositivo, DispositivoParameters parameter) throws IllegalArgumentException {
		if(verifyArguments(room, dispositivo, parameter)) {
			return new Topic(room, dispositivo, parameter);
		}
		
		else {
			throw new IllegalArgumentException("Parametri inseriti non validi");
		}
	}
	
	@Override
	public String toString() {
		return TOPIC_FORMAT.formatted(home, room, id, parameter);
	}
	
	private static boolean verifyArguments(RoomPort room, DevicePort dispositivo, DispositivoParameters parameter) {
		if(room != null && dispositivo != null && parameter != null)
			return true;
		
		return false;
	}

	public boolean setTopic(RoomPort room, DevicePort dispositivo, DispositivoParameters parameter) {
		if(verifyArguments(room, dispositivo, parameter)) {
			this.room = room.getID();
			id = dispositivo.getId();
			this.parameter = parameter;
			return true;
		}
		
		System.out.println("I parametri inseriti non sono corretti");
		return false;
	}

	public String getHome() {
		return home;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(RoomPort room) {
		if(room != null) this.room = room.getID();
	}

	public String getId() {
		return id;
	}

	public void setId(DevicePort dispositivo) {
		this.id = dispositivo.getId();
	}

	public DispositivoParameters getParameter() {
		return parameter;
	}

	public void setParameter(DispositivoParameters parameter) {
		if(parameter != null) this.parameter = parameter;
	}
	
	
}

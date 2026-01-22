package it.unipv.posfw.smartdab.infrastructure.messaging.topic;

<<<<<<< HEAD
import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameters;
import it.unipv.posfw.smartdab.core.port.device.DevicePort;
import it.unipv.posfw.smartdab.core.port.room.RoomPort;
=======
import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.port.device.DevicePort;
import it.unipv.posfw.smartdab.core.port.room.RoomPort;
import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.port.device.DevicePort;
import it.unipv.posfw.smartdab.core.port.room.RoomPort;

// Questa classe definisce un nuovo abstract type topic semi-strutturato

// Ogni controllo necessario per l'istanza è gestito qui
>>>>>>> main

public class Topic {
	private String home;
	private String room;
	private String id;
	private DispositivoParameter parameter;
	public final int length = 4; // Numero di Layers
	public final String TOPIC_FORMAT = "%s/%s/%s/%s";
	
	// Costruttore chiuso per garantire la costruzione di topic regolari
	private Topic(RoomPort room, DevicePort dispositivo, DispositivoParameter parameter) {
		home = "home";
		this.room = room.getId();
		id = dispositivo.getId();
		this.parameter = parameter;
	}
	
	// Usare questo metodo per istanziare il topic
	public static Topic createTopic(RoomPort room, DevicePort dispositivo, DispositivoParameter parameter) throws IllegalArgumentException {
		if(verifyArguments(room, dispositivo, parameter)) {
			return new Topic(room, dispositivo, parameter);
		}
		
		throw new IllegalArgumentException("Parametri inseriti non validi");
	}
	
	@Override
	public String toString() {
		return TOPIC_FORMAT.formatted(home, room, id, parameter);
	}
	
	private static boolean verifyArguments(RoomPort room, DevicePort dispositivo, DispositivoParameter parameter) {
		if(room != null && dispositivo != null && parameter != null)
			return true;
		
		return false;
	}
	
	// Metodo per reimpostare il topic post-istanza (non funziona senza istanza)
	public boolean setTopic(RoomPort room, DevicePort dispositivo, DispositivoParameter parameter) {
		if(verifyArguments(room, dispositivo, parameter)) {
			this.room = room.getId();
			id = dispositivo.getId();
			this.parameter = parameter;
			return true;
		}
		
		System.out.println("I parametri inseriti non sono corretti");
		return false;
	}

	// Gettes & Setters post-istanza
	public String getHome() {
		return home;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(RoomPort room) {
		if(room != null) this.room = room.getId();
	}

	public String getId() {
		return id;
	}

	public void setId(DevicePort dispositivo) {
		this.id = dispositivo.getId();
	}

	public DispositivoParameter getParameter() {
		return parameter;
	}

	public void setParameter(DispositivoParameter parameter) {
		if(parameter != null) this.parameter = parameter;
	}
	
	
}

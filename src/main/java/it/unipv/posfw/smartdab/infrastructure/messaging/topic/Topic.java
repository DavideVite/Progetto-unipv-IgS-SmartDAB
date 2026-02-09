package it.unipv.posfw.smartdab.infrastructure.messaging.topic;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.port.room.RoomPort;

/**
 * Questa classe definisce la struttura di un topic (home/room/id/parameter)
 * @author Alessandro Ingenito
 * @version 1.0
 */

public class Topic {
	private String home;
	private String room;
	private String id;
	private DispositivoParameter parameter;
	public final String TOPIC_FORMAT = "%s/%s/%s/%s";
	
	// Costruttore chiuso per garantire la costruzione di topic regolari
	private Topic(String id, RoomPort room, DispositivoParameter parameter) {
		home = "home";
		this.room = room.getId();
		this.id = id;
		this.parameter = parameter;
	}
	
	/**
	 * Questo metodo consente di istanziare un topic solo se i parametri inseriti sono validi
	 */
	public static Topic createTopic(String id, RoomPort room, DispositivoParameter parameter) throws IllegalArgumentException {
		if(verifyArguments(room, parameter) && checkId(id)) {
			return new Topic(id, room, parameter);
		}
		
		throw new IllegalArgumentException("Parametri inseriti non validi");
	}
	
	@Override
	public String toString() {
		return TOPIC_FORMAT.formatted(home, room, id, parameter);
	}
	
	private static boolean verifyArguments(RoomPort room, DispositivoParameter parameter) {
		if(room != null && parameter != null)
			return true;
		
		return false;
	}
	
	// Metodo per reimpostare il topic post-istanza (non funziona senza istanza)
	public boolean setTopic(String id, RoomPort room, DispositivoParameter parameter) {
		if(verifyArguments(room, parameter) && checkId(id)) {
			this.room = room.getId();
			this.id = id;
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

	public boolean setId(String id) {
		if(checkId(id)) {
			this.id = id.toLowerCase();
			return true;
		}
		
		return false;
	}
	
	private static boolean checkId(String id) {
		String idx = "[A-Za-z]{1,17}\\d{0,3}";
		if(id != null) return id.matches(idx);
		return false;
	}

	public DispositivoParameter getParameter() {
		return parameter;
	}

	public void setParameter(DispositivoParameter parameter) {
		if(parameter != null) this.parameter = parameter;
	}
	
	
}

package main.java.it.unipv.posfw.smartdab.infrastructure.messaging.topic;

import main.java.it.unipv.posfw.smartdab.core.port.device.DevicePort;
import main.java.it.unipv.posfw.smartdab.core.port.room.RoomPort;

public class Topic {
	private String topic;
	public final int length = 4; // Numero di Layers
	public final String TOPIC_FORMAT = "home/%s/%s/%s";
	
	private Topic(RoomPort room, DevicePort dispositivo, String parameter) {
		topic = TOPIC_FORMAT.formatted(room.getID(), dispositivo.getId(), parameter);
	}
	
	public static Topic createTopic(RoomPort room, DevicePort dispositivo, String parameter) throws IllegalArgumentException {
		if(verifyArguments(room, dispositivo, parameter)) {
			return new Topic(room, dispositivo, parameter);
		}
		
		else {
			throw new IllegalArgumentException("Parametri inseriti non validi");
		}
	}
	
	@Override
	public String toString() {
		return topic;
	}
	
	private static boolean verifyArguments(RoomPort room, DevicePort dispositivo, String parameter) {
		if(!room.equals(null) && !dispositivo.equals(null) && !parameter.equals(""))
			return true;
		
		return false;
	}

	public boolean setTopic(RoomPort room, DevicePort dispositivo, String parameter) {
		if(verifyArguments(room, dispositivo, parameter)) {
			topic = TOPIC_FORMAT.formatted(room.getID(), dispositivo.getId(), parameter);
			return true;
		}
		
		System.out.println("I parametri inseriti non sono corretti");
		return false;
	}
	
	public String getRoomByTopic() {
		return topic.split("/")[1];
	}
	
	public String getIdByTopic() {
		return topic.split("/")[2];
	}
	
	public String getParameterByTopic() {
		return topic.split("/")[3];
	}
}

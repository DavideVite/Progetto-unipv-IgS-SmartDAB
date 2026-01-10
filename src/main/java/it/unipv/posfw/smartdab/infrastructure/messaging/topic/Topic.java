package it.unipv.posfw.smartdab.infrastructure.messaging.topic;

import it.unipv.posfw.smartdab.core.port.device.DevicePort;
import it.unipv.posfw.smartdab.core.port.room.RoomPort;

public class Topic {
	private String topic;
	public final String TOPIC_FORMAT = "home/%s/%s/%s";
	
	public Topic() {
		topic = "";
	}
	
	@Override
	public String toString() {
		return topic;
	}

	public void setTopic(RoomPort room, DevicePort dispositivo, String parameter) {
		if(!parameter.equals(""))
		topic = TOPIC_FORMAT.formatted(room.getID(), dispositivo.getId(), parameter);
	}
	
	public String getRoomByTopic() {
		return topic.split("/")[1];
	}
	
	public String getDispositivoByTopic() {
		return topic.split("/")[2];
	}
	
	public String getParameterByTopic() {
		return topic.split("/")[3];
	}
	
	public void clear() {
		topic = "";
	}
}

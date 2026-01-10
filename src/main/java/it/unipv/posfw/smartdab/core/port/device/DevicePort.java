package main.java.it.unipv.posfw.smartdab.core.port.device;

import main.java.it.unipv.posfw.smartdab.core.domain.enums.DispositivoStates;
import main.java.it.unipv.posfw.smartdab.infrastructure.messaging.topic.Topic;

public interface DevicePort {
	Topic getTopic();
	DispositivoStates getState();
	void setState(DispositivoStates state);
	boolean isActive();
	String getId();
	void switchDispositivo();
}

package main.java.it.unipv.posfw.smartdab.src.core.port.device;

import main.java.it.unipv.posfw.smartdab.src.core.domain.enums.DispositivoStates;
import main.java.it.unipv.posfw.smartdab.src.infrastructure.messaging.topic.Topic;

public interface DevicePort {
	Topic getTopic();
	DispositivoStates getState();
	boolean isActive();
	String getId();
	void switchDispositivo();
}

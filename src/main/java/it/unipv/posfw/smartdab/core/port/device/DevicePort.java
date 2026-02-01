package it.unipv.posfw.smartdab.core.port.device;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoStates;
import it.unipv.posfw.smartdab.infrastructure.messaging.topic.Topic;

public interface DevicePort {
	Topic getTopic();
	DispositivoStates getState();
	void setState(DispositivoStates state);
	boolean isActive();
	void switchDispositivo();
	int action(Object args);
}

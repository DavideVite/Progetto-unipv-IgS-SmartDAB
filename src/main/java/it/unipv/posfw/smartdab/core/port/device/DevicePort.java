package it.unipv.posfw.smartdab.core.port.device;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoStates;
import it.unipv.posfw.smartdab.infrastructure.messaging.topic.Topic;

/**
 * Interfaccia ai dispositivi per i comandi
 * @author Alessandro Ingenito
 * @version 1.0
 */

public interface DevicePort {
	Topic getTopic();
	DispositivoStates getState();
	void setState(DispositivoStates state);
	boolean isActive();
	void switchDispositivo();
	int action(Object args);
}

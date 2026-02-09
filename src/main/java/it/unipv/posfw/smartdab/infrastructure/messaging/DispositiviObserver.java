package it.unipv.posfw.smartdab.infrastructure.messaging;

import it.unipv.posfw.smartdab.core.domain.enums.Message;
import it.unipv.posfw.smartdab.infrastructure.messaging.request.Request;

/**
 * Interfaccia per Observer dei dispositivi (Es. Event Bus)
 * @author Alessandro Ingenito
 * @version 1.0
 */

public interface DispositiviObserver {
	public Message update(Request request);
}

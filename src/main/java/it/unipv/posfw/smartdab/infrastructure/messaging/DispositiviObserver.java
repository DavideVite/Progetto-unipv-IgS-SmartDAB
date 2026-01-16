package it.unipv.posfw.smartdab.infrastructure.messaging;

import it.unipv.posfw.smartdab.core.domain.enums.Message;
import it.unipv.posfw.smartdab.infrastructure.messaging.request.Request;

public interface DispositiviObserver {
	public Message update(Request request);
}

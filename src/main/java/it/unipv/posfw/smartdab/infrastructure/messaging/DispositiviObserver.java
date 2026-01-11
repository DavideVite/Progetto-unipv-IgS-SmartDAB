package main.java.it.unipv.posfw.smartdab.infrastructure.messaging;

import main.java.it.unipv.posfw.smartdab.core.domain.enums.Message;
import main.java.it.unipv.posfw.smartdab.infrastructure.messaging.request.Request;

public interface DispositiviObserver {
	public Message update(Request request);
}

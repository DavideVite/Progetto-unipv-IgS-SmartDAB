package it.unipv.posfw.smartdab.infrastructure.messaging;

import it.unipv.posfw.smartdab.core.domain.enums.Message;

public interface DispositiviObserver {
	public Message update(String payload);
}

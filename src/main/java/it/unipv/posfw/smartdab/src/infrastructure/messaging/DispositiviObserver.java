package it.unipv.posfw.smartdab.src.infrastructure.messaging;

import it.unipv.posfw.smartdab.src.core.domain.enums.Message;

public interface DispositiviObserver {
	public Message update(String payload);
}

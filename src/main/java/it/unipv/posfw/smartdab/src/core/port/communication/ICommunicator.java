package it.unipv.posfw.smartdab.src.core.port.communication;

import it.unipv.posfw.smartdab.src.core.domain.enums.DispositivoStates;
import it.unipv.posfw.smartdab.src.core.domain.enums.Message;

public interface ICommunicator {
	
	// es. home/stanza1/disp1/param1/val
	final String format = "home/%s/%s/%s/%s";
	
	public boolean sendPayload(String payload);
	public Message processRequest(String request);
	public boolean changeState(DispositivoStates state);
}

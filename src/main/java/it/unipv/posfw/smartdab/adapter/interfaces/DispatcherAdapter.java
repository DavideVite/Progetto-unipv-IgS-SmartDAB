package it.unipv.posfw.smartdab.adapter.interfaces;

import it.unipv.posfw.smartdab.core.port.communication.ICommunicator;

public interface DispatcherAdapter extends ICommunicator{
	public boolean dispatchCommand(String command);
}

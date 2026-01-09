package main.java.it.unipv.posfw.smartdab.src.adapter.interfaces;

import main.java.it.unipv.posfw.smartdab.src.core.port.communication.ICommunicator;

public interface DispatcherAdapter extends ICommunicator{
	public boolean dispatchCommand(String command);
}

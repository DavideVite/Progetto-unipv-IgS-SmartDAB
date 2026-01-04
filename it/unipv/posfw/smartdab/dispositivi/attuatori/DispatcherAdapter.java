package it.unipv.posfw.smartdab.dispositivi.attuatori;

import it.unipv.posfw.smartdab.dispositivi.ICommunicator;

public interface DispatcherAdapter extends ICommunicator{
	public boolean dispatchCommand(String command);
}

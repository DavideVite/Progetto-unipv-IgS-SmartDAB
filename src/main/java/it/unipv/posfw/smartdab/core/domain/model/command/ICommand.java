package it.unipv.posfw.smartdab.core.domain.model.command;

import it.unipv.posfw.smartdab.core.port.device.DevicePort;
import it.unipv.posfw.smartdab.infrastructure.messaging.request.Request;

/**
 * Questa classe è un'interfaccia che ogni comando associato ai dispositivi deve implementare
 * @see DevicePort
 * @see Request
 * @author Alessandro Ingenito
 * @version 1.0
 */

public interface ICommand {
	
	/**
	 * @param dispositivo dispositivo sul quale il comando deve agire
	 * @param request richiesta che ha portato all'individuazione del comando
	 */
	
	public void execute(DevicePort dispositivo, Request request);
}

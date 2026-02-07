package it.unipv.posfw.smartdab.core.domain.model.command;

import it.unipv.posfw.smartdab.core.port.device.DevicePort;
import it.unipv.posfw.smartdab.infrastructure.messaging.request.Request;

/**
 * Interfaccia per il pattern Command.
 * Rappresenta un comando eseguibile su un dispositivo.
 */
public interface ICommand {

	/**
	 * Esegue il comando sul dispositivo specificato.
	 * @param dispositivo il dispositivo target
	 * @param request la richiesta contenente i parametri del comando
	 */
	void execute(DevicePort dispositivo, Request request);
}

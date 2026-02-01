package it.unipv.posfw.smartdab.core.domain.model.dispositivo.standard_commands;

import it.unipv.posfw.smartdab.core.domain.model.command.ICommand;
import it.unipv.posfw.smartdab.core.port.device.DevicePort;
import it.unipv.posfw.smartdab.infrastructure.messaging.request.Request;

public class ActionCommand implements ICommand {

	@Override
	public void execute(DevicePort dispositivo, Request request) {
		if(dispositivo.action(request.getVal()) == 1) 
			System.out.println("Azione eseguita correttamente");
		else 
			System.out.println("Errore: l'azione richiesta non è andata a buon fine");
	}

}

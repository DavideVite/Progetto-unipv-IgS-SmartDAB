package main.java.it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.lampadaON_OFF.commands;

import main.java.it.unipv.posfw.smartdab.core.domain.enums.DispositivoStates;
import main.java.it.unipv.posfw.smartdab.core.domain.model.command.ICommand;
import main.java.it.unipv.posfw.smartdab.core.port.device.DevicePort;
import main.java.it.unipv.posfw.smartdab.infrastructure.messaging.request.Request;

public class StateUpdateCommand implements ICommand {

	@Override
	public void execute(DevicePort dispositivo, Request request) {
		try {
			dispositivo.setState((DispositivoStates)request.getVal());
		} catch(ClassCastException e) {
			System.out.println("Il valore presente nella richiesta non è uno stato valido");
		}
	}

}

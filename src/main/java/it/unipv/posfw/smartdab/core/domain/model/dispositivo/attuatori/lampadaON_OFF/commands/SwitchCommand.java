package main.java.it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.lampadaON_OFF.commands;

import main.java.it.unipv.posfw.smartdab.core.domain.model.command.ICommand;
import main.java.it.unipv.posfw.smartdab.core.port.device.DevicePort;
import main.java.it.unipv.posfw.smartdab.infrastructure.messaging.request.Request;

public class SwitchCommand implements ICommand {

	@Override
	public void execute(DevicePort dispositivo, Request request) {
		dispositivo.switchDispositivo();
	}

}

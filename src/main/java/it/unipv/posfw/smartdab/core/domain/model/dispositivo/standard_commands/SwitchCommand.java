package it.unipv.posfw.smartdab.core.domain.model.dispositivo.standard_commands;

import it.unipv.posfw.smartdab.core.domain.model.command.ICommand;
import it.unipv.posfw.smartdab.core.port.device.DevicePort;
import it.unipv.posfw.smartdab.infrastructure.messaging.request.Request;

public class SwitchCommand implements ICommand {

	@Override
	public void execute(DevicePort dispositivo, Request request) {
		dispositivo.switchDispositivo();
	}

}

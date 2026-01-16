package it.unipv.posfw.smartdab.core.domain.model.command;

import it.unipv.posfw.smartdab.core.port.device.DevicePort;
import it.unipv.posfw.smartdab.infrastructure.messaging.request.Request;

// Ogni comando valido deve implementare questa interfaccia

public interface ICommand {
	public void execute(DevicePort dispositivo, Request request);
}

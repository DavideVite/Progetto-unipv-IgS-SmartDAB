package main.java.it.unipv.posfw.smartdab.core.domain.model.command;

import main.java.it.unipv.posfw.smartdab.core.port.device.DevicePort;
import main.java.it.unipv.posfw.smartdab.infrastructure.messaging.request.Request;

// Ogni comando valido deve implementare questa interfaccia

public interface ICommand {
	public void execute(DevicePort dispositivo, Request request);
}

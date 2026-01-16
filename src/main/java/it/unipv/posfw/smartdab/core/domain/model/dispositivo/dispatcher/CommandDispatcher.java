package it.unipv.posfw.smartdab.core.domain.model.dispositivo.dispatcher;

import java.util.HashMap;
import java.util.Map;

import it.unipv.posfw.smartdab.core.domain.model.command.ICommand;
import it.unipv.posfw.smartdab.core.port.device.DevicePort;
import it.unipv.posfw.smartdab.infrastructure.messaging.request.Request;

public class CommandDispatcher {
	private Map<String, ICommand> commands = new HashMap<>();
	
	public CommandDispatcher() {
		
	}
	
	public void register(String type, ICommand command) {
		if(!type.equals("") && command  != null) commands.put(type, command);
		else System.out.println("I parametri inseriti non sono validi");
	}
	
	public void dispatch(Request request, DevicePort dispositivo) throws Exception {
		ICommand cmd = commands.get(request.getType());
		if(cmd == null) {
			// Da inserire eccezione personalizzata
			throw new Exception();
		}

		cmd.execute(dispositivo, request);
	}
}

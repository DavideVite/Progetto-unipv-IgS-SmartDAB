package it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.pompa_di_calore.commands;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.model.command.ICommand;
import it.unipv.posfw.smartdab.core.port.device.DevicePort;
import it.unipv.posfw.smartdab.infrastructure.messaging.request.Request;

public class UpdateSetpointCommand implements ICommand {

	@Override
	public void execute(DevicePort dispositivo, Request request) {
		try {
			if((double)request.getVal() <= DispositivoParameter.TEMPERATURA.getMax() &&
			   (double)request.getVal() >= DispositivoParameter.TEMPERATURA.getMin()) {
				
				dispositivo.action((double)request.getVal());
			}
			
			else System.out.println("Il valore inserito è potenzialmente pericoloso");
			
		} catch(ClassCastException e) {
			System.out.println("Il valore nella richiesta non è del tipo corretto");
		}

	}

}

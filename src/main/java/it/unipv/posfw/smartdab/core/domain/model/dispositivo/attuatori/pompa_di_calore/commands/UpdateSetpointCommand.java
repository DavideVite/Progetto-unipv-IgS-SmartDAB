package it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.pompa_di_calore.commands;

import it.unipv.posfw.smartdab.adapter.facade.AttuatoreFacade;
import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.model.command.ICommand;
import it.unipv.posfw.smartdab.core.port.device.DevicePort;
import it.unipv.posfw.smartdab.core.port.device.AttuatorePort;
import it.unipv.posfw.smartdab.infrastructure.messaging.request.Request;

/**
 * Comando specifico per dispositivi di tipo attuatore di temperatura dotati di setpoint
 * @see Dispositivo
 * @see AttuatoreFacade
 * @see ICommand
 * @see DevicePort
 * @see Request
 * @author Alessandro Ingenito
 * @version 1.0
 */

public class UpdateSetpointCommand implements ICommand {

	/**
	 * Imposta il nuovo setpoint dell'attuatore se rientra nel range di sistema
	 */
	
	@Override
	public void execute(DevicePort dispositivo, Request request) {
		try {
			if((double)request.getVal() <= DispositivoParameter.TEMPERATURA.getMax() &&
			   (double)request.getVal() >= DispositivoParameter.TEMPERATURA.getMin()) {
				
				((AttuatorePort) dispositivo).changeSetpoint((double)request.getVal());
			}
			
			else System.out.println("Il valore inserito è potenzialmente pericoloso");
			
		} catch(ClassCastException e) {
			System.out.println("Il valore nella richiesta non è del tipo corretto");
		}

	}

}

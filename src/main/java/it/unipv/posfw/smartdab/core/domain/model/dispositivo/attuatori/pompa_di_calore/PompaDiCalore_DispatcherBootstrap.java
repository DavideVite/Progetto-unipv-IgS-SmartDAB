package it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.pompa_di_calore;

import it.unipv.posfw.smartdab.core.domain.enums.Message;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.pompa_di_calore.commands.UpdateSetpointCommand;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.dispatcher.CommandDispatcher;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.dispatcher.IDispatcherBootstrap;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.standard_commands.ActionCommand;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.standard_commands.SwitchCommand;

public class PompaDiCalore_DispatcherBootstrap implements IDispatcherBootstrap {
	
	public PompaDiCalore_DispatcherBootstrap() {
		
	}
	
	@Override
	public CommandDispatcher createDispatcher() {
		CommandDispatcher cd = new CommandDispatcher();
		cd.register(Message.CONFIG + "." + Message.SWITCH, new SwitchCommand());
		cd.register(Message.UPDATE + "." + Message.SETPOINT, new UpdateSetpointCommand());
		cd.register(Message.ACT.toString(), new ActionCommand());
		return cd;
	}
}

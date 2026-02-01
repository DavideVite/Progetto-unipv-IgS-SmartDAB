package it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.lampadaON_OFF;

import it.unipv.posfw.smartdab.core.domain.enums.Message;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.standard_commands.ActionCommand;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.standard_commands.SwitchCommand;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.dispatcher.CommandDispatcher;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.dispatcher.IDispatcherBootstrap;

public class Lampada_DispatcherBootstrap implements IDispatcherBootstrap {

	public Lampada_DispatcherBootstrap() {
		
	}
	
	@Override
	public CommandDispatcher createDispatcher() {
		CommandDispatcher cd = new CommandDispatcher();
		cd.register(Message.CONFIG + "." + Message.SWITCH, new SwitchCommand());
		cd.register(Message.ACT.toString(), new ActionCommand());
		return cd;
	}

}

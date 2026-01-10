package main.java.it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.lampadaON_OFF;

import main.java.it.unipv.posfw.smartdab.core.domain.enums.Message;
import main.java.it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.lampadaON_OFF.commands.SwitchCommand;
import main.java.it.unipv.posfw.smartdab.core.domain.model.dispositivo.dispatcher.CommandDispatcher;
import main.java.it.unipv.posfw.smartdab.core.domain.model.dispositivo.dispatcher.IDispatcherBootstrap;

public class Lampada_DispatcherBootstrap implements IDispatcherBootstrap {

	public Lampada_DispatcherBootstrap() {
		
	}
	
	@Override
	public CommandDispatcher createDispatcher() {
		CommandDispatcher cd = new CommandDispatcher();
		cd.register(Message.CONFIG + "." + Message.SWITCH, new SwitchCommand());
		cd.register(Message.UPDATE + "." + Message.STATE, null);
		return cd;
	}

}

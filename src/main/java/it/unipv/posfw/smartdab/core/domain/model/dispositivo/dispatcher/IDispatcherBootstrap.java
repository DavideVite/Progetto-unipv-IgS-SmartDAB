package it.unipv.posfw.smartdab.core.domain.model.dispositivo.dispatcher;

/**
 * Questa interfaccia deve essere utilizzata dai produttori o tecnici per definire tutti
 * i comandi da inserire nel command dispatcher del proprio dispositivo
 * @author Alessandro Ingenito
 * @version 1.0
 */

public interface IDispatcherBootstrap {
	public CommandDispatcher createDispatcher();
}

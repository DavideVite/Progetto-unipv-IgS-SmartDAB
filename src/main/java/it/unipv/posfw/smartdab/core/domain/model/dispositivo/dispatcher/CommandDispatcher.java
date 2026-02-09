package it.unipv.posfw.smartdab.core.domain.model.dispositivo.dispatcher;

import java.util.HashMap;
import java.util.Map;

import it.unipv.posfw.smartdab.core.domain.model.command.ICommand;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.dispatcher.exceptions.ComandoNonEsistente;
import it.unipv.posfw.smartdab.core.port.device.DevicePort;
import it.unipv.posfw.smartdab.infrastructure.messaging.request.Request;

/**
 * Questa classe è utilizzata per implementare il pattern command per risolvere la problematica
 * dell'individuazione del comando data una richiesta
 * @see ICommand
 * @see ComandoNonEsistente
 * @author Alessandro Ingenito
 * @version 1.0
 */

public class CommandDispatcher {
	private Map<String, ICommand> commands = new HashMap<>();
	
	public CommandDispatcher() {
		
	}
	
	/**
	 * Questo metodo serve per registrare dinamicamente i comandi accettati da un command dispatcher
	 * 
	 * @param type rappresenta il comando che deve essere ricevuto per eseguire il comando
	 * @param command implementazione del comando da eseguire
	 */
	
	public void register(String type, ICommand command) {
		if(!type.equals("") && type != null && command  != null) commands.put(type, command);
		else System.out.println("I parametri inseriti non sono validi");
	}
	
	/**
	 * Questo metodo ricerca un comando attraverso il type e lo esegue qualora fosse presente 
	 * in lista
	 * @param request richiesta ricevuta dal dispatcher
	 * @param dispositivo dispositivo sul quale eventualmente il comando agisce
	 * @throws ComandoNonEsistente
	 */
	
	public void dispatch(Request request, DevicePort dispositivo) throws ComandoNonEsistente {
		ICommand cmd = commands.get(request.getType());
		if(cmd == null) {
			throw new ComandoNonEsistente(request.getType());
		}

		cmd.execute(dispositivo, request);
	}
}

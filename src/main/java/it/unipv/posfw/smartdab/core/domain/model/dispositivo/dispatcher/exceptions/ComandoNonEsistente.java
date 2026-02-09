package it.unipv.posfw.smartdab.core.domain.model.dispositivo.dispatcher.exceptions;

/**
 * Eccezione nella ricerca dei comandi
 * @author Alessandro Ingenito
 * @version 1.0
 */


public class ComandoNonEsistente extends Exception {
	private final static String msg = "Il comando %s inserito non è ammesso da questo dispositivo";
	
	public ComandoNonEsistente(String command) {
		super(String.format(msg, command));
	}
}

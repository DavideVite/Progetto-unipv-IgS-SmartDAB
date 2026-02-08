package it.unipv.posfw.smartdab.core.domain.exception;

/**
 * Eccezione lanciata quando si tenta di modificare o eliminare uno scenario predefinito.
 */
public class ScenarioNonModificabileException extends RuntimeException {

	public ScenarioNonModificabileException(String message) {
		super(message);
	}
}

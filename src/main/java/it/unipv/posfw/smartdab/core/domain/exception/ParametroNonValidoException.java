package it.unipv.posfw.smartdab.core.domain.exception;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;

/**
 * Eccezione lanciata quando un valore di parametro non e' valido.
 */
public class ParametroNonValidoException extends RuntimeException {

    private final DispositivoParameter tipoParametro;

    public ParametroNonValidoException(DispositivoParameter tipoParametro, String message) {
        super("Parametro " + tipoParametro.name() + " non valido: " + message);
        this.tipoParametro = tipoParametro;
    }

    public ParametroNonValidoException(String message) {
        super(message);
        this.tipoParametro = null;
    }

    public DispositivoParameter getTipoParametro() {
        return tipoParametro;
    }
}

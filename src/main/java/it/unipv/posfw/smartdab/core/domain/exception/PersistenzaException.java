package it.unipv.posfw.smartdab.core.domain.exception;

/**
 * Eccezione unchecked per errori di persistenza (database, I/O).
 *
 * SCELTA UNCHECKED (RuntimeException):
 * - Gli errori di DB sono tipicamente non recuperabili dal chiamante
 * - Evita la propagazione di try/catch in tutta la call stack
 * - Il chiamante puo' comunque catturarla se vuole gestirla
 */
public class PersistenzaException extends RuntimeException {

    public PersistenzaException(String message) {
        super(message);
    }

    public PersistenzaException(String message, Throwable cause) {
        super(message, cause);
    }

    public PersistenzaException(Throwable cause) {
        super(cause);
    }
}

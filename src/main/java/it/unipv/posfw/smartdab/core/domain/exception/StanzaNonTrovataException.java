package it.unipv.posfw.smartdab.core.domain.exception;

/**
 * Eccezione lanciata quando una stanza richiesta non esiste.
 */
public class StanzaNonTrovataException extends RuntimeException {

    private final String stanzaId;

    public StanzaNonTrovataException(String stanzaId) {
        super("Stanza non trovata con ID: " + stanzaId);
        this.stanzaId = stanzaId;
    }

    public String getStanzaId() {
        return stanzaId;
    }
}

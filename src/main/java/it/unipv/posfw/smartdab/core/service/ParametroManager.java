package it.unipv.posfw.smartdab.core.service;

import it.unipv.posfw.smartdab.adapter.facade.AttuatoreFacade;
import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.model.casa.Stanza;
import it.unipv.posfw.smartdab.core.domain.model.parametro.IParametroValue;
import it.unipv.posfw.smartdab.core.domain.model.scenario.StanzaConfig;
import it.unipv.posfw.smartdab.core.port.communication.ICommandSender;

/**
 * Servizio per la gestione dei parametri.
 *
 * REFACTORING: Inversione delle Dipendenze (DIP)
 * - Prima: Dipendeva da IEventBusClient e Request (infrastructure)
 *          Conteneva "SETPOINT" hardcoded
 * - Dopo: Dipende da ICommandSender (core.port) - Output Port
 *
 * REFACTORING: Eliminazione instanceof (OCP/LSP)
 * - Prima: Usava instanceof per filtrare AttuatoreFacade e per estrarre valori
 * - Dopo: Usa Stanza.getAttuatori() e IParametroValue.toNumericValue()
 *         La responsabilita' del filtraggio e' nell'entita' che possiede i dati,
 *         la conversione e' delegata all'oggetto stesso (polimorfismo).
 */
public class ParametroManager {
    private final GestoreStanze gestoreStanze;
    private final ICommandSender commandSender;

    public ParametroManager(GestoreStanze gestoreStanze, ICommandSender commandSender) {
        this.gestoreStanze = gestoreStanze;
        this.commandSender = commandSender;
    }

    /**
     * Trova il primo attuatore idoneo nella stanza che supporta il parametro richiesto.
     * Usa Stanza.getAttuatori() per evitare instanceof nel codice chiamante.
     */
    public AttuatoreFacade getAttuatoreIdoneo(String stanzaId, DispositivoParameter tipoParametro) {
        Stanza stanza = gestoreStanze.cercaStanzaPerId(stanzaId);
        if (stanza == null) return null;

        // Itera solo sugli attuatori - il filtraggio e' gia' fatto da Stanza
        for (AttuatoreFacade attuatore : stanza.getAttuatori()) {
            if (attuatore.isActive() && attuatore.supportaParametro(tipoParametro)) {
                return attuatore;
            }
        }
        return null;
    }

    // Caso d'uso 1: Impostazione manuale
    public boolean impostaParametro(String stanzaId, DispositivoParameter tipoParametro, IParametroValue valore) {
        if (valore == null || !valore.isValid()) return false;

        Stanza stanza = gestoreStanze.cercaStanzaPerId(stanzaId);
        if (stanza == null) return false;

        // Tenta invio via CommandSender se esiste un attuatore idoneo
        AttuatoreFacade attuatore = getAttuatoreIdoneo(stanzaId, tipoParametro);
        if (attuatore != null) {
            // Delega all'Output Port - il core non conosce il protocollo
            commandSender.inviaComando(attuatore, tipoParametro, valore);
        }

        // Aggiorna sempre il target della stanza.
        // Usa toNumericValue() invece di instanceof + switch (polimorfismo).
        stanza.updateTarget(tipoParametro.name(), valore.toNumericValue());

        return true;
    }

    // Caso d'uso 2: Applicazione configurazione (sia manuale che da scenario)
    public boolean applicaStanzaConfig(StanzaConfig config) {
        return impostaParametro(
            config.getStanzaId(),
            config.getTipo_parametro(),
            config.getParametro()
        );
    }
}

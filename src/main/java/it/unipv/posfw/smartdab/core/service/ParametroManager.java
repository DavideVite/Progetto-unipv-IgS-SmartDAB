package it.unipv.posfw.smartdab.core.service;

import it.unipv.posfw.smartdab.adapter.facade.AttuatoreFacade;
import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.exception.ParametroNonValidoException;
import it.unipv.posfw.smartdab.core.domain.exception.StanzaNonTrovataException;
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
     *
     * @param stanzaId ID della stanza
     * @param tipoParametro Tipo di parametro richiesto
     * @return L'attuatore idoneo, o null se non trovato
     * @throws StanzaNonTrovataException se la stanza non esiste
     */
    public AttuatoreFacade getAttuatoreIdoneo(String stanzaId, DispositivoParameter tipoParametro) {
        Stanza stanza = gestoreStanze.cercaStanzaPerId(stanzaId);
        if (stanza == null) {
            throw new StanzaNonTrovataException(stanzaId);
        }

        // Itera solo sugli attuatori - il filtraggio e' gia' fatto da Stanza
        for (AttuatoreFacade attuatore : stanza.getAttuatori()) {
            if (attuatore.isActive() && attuatore.supportaParametro(tipoParametro)) {
                return attuatore;
            }
        }
        return null;
    }

    /**
     * Imposta un parametro su una stanza.
     * Se esiste un attuatore idoneo, invia il comando via EventBus.
     * Aggiorna sempre il target della stanza.
     *
     * @param stanzaId ID della stanza
     * @param tipoParametro Tipo di parametro da impostare
     * @param valore Valore da impostare
     * @throws StanzaNonTrovataException se la stanza non esiste
     * @throws ParametroNonValidoException se il valore e' nullo o non valido
     */
    public void impostaParametro(String stanzaId, DispositivoParameter tipoParametro, IParametroValue valore) {
        // Validazione input
        if (valore == null) {
            throw new ParametroNonValidoException(tipoParametro, "il valore non puo' essere nullo");
        }
        if (!valore.isValid()) {
            throw new ParametroNonValidoException(tipoParametro, "il valore non e' valido: " + valore.getDisplayString());
        }

        Stanza stanza = gestoreStanze.cercaStanzaPerId(stanzaId);
        if (stanza == null) {
            throw new StanzaNonTrovataException(stanzaId);
        }

        // Tenta invio via CommandSender se esiste un attuatore idoneo
        AttuatoreFacade attuatore = findAttuatoreIdoneoInStanza(stanza, tipoParametro);
        if (attuatore != null) {
            // Delega all'Output Port - il core non conosce il protocollo
            commandSender.inviaComando(attuatore, tipoParametro, valore);
        }

        // Aggiorna sempre il target della stanza.
        // Usa toNumericValue() invece di instanceof + switch (polimorfismo).
        stanza.updateTarget(tipoParametro.name(), valore.toNumericValue());
    }

    /**
     * Applica una configurazione stanza (sia manuale che da scenario).
     *
     * @param config La configurazione da applicare
     * @throws StanzaNonTrovataException se la stanza non esiste
     * @throws ParametroNonValidoException se il valore non e' valido
     */
    public void applicaStanzaConfig(StanzaConfig config) {
        impostaParametro(
            config.getStanzaId(),
            config.getTipo_parametro(),
            config.getParametro()
        );
    }

    /**
     * Trova un attuatore idoneo all'interno di una stanza gia' recuperata.
     * Metodo privato per evitare doppia ricerca della stanza.
     */
    private AttuatoreFacade findAttuatoreIdoneoInStanza(Stanza stanza, DispositivoParameter tipoParametro) {
        for (AttuatoreFacade attuatore : stanza.getAttuatori()) {
            if (attuatore.isActive() && attuatore.supportaParametro(tipoParametro)) {
                return attuatore;
            }
        }
        return null;
    }
}

package it.unipv.posfw.smartdab.core.service;

import java.util.ArrayList;
import java.util.List;

import it.unipv.posfw.smartdab.adapter.facade.AttuatoreFacade;
import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.exception.ParametroNonValidoException;
import it.unipv.posfw.smartdab.core.domain.exception.StanzaNonTrovataException;
import it.unipv.posfw.smartdab.core.domain.model.casa.Stanza;
import it.unipv.posfw.smartdab.core.domain.model.parametro.IParametroValue;
import it.unipv.posfw.smartdab.core.domain.model.scenario.StanzaConfig;
import it.unipv.posfw.smartdab.core.port.communication.ICommandSender;
import it.unipv.posfw.smartdab.core.port.communication.observer.Observable;
import it.unipv.posfw.smartdab.core.port.communication.observer.Observer;
import it.unipv.posfw.smartdab.core.service.strategy.ActuationStrategy;
import it.unipv.posfw.smartdab.core.service.strategy.ActuationStrategy.ActuationResult;
import it.unipv.posfw.smartdab.core.service.strategy.DirectMatchStrategy;

/**
 *
 * PATTERN OBSERVER:
 * - Implementa Observable per notificare quando un parametro viene impostato
 * - I controller possono registrarsi come observer per reagire ai cambiamenti
 */
public class ParametroManager implements Observable {
    private final GestoreStanze gestoreStanze;
    private final ICommandSender commandSender;
    private ActuationStrategy actuationStrategy;
    private final List<Observer> observers = new ArrayList<>();

    public ParametroManager(GestoreStanze gestoreStanze, ICommandSender commandSender) {
        this.gestoreStanze = gestoreStanze;
        this.commandSender = commandSender;
        this.actuationStrategy = new DirectMatchStrategy(); // Default
    }

    // ==================== Observer Pattern ====================

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(Object args) {
        for (Observer o : observers) {
            o.update(this, args);
        }
    }

    /**
     * Imposta la strategia di distribuzione dei comandi.
     * Permette di cambiare comportamento a runtime.
     *
     * @param strategy La nuova strategia da utilizzare
     */
    public void setActuationStrategy(ActuationStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("La strategia non puo' essere null");
        }
        this.actuationStrategy = strategy;
    }

    /**
     * Restituisce la strategia corrente.
     */
    public ActuationStrategy getActuationStrategy() {
        return actuationStrategy;
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
     * Usa ActuationStrategy per determinare quali attuatori riceveranno il comando.
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

        // Raccogli tutti gli attuatori candidati che supportano il parametro
        List<AttuatoreFacade> attuatoriCandidati = new ArrayList<>();
        for (AttuatoreFacade attuatore : stanza.getAttuatori()) {
            if (attuatore.supportaParametro(tipoParametro)) {
                attuatoriCandidati.add(attuatore);
            }
        }

        // Delega alla strategia la selezione e distribuzione
        List<ActuationResult> risultati = actuationStrategy.distribuisci(
            attuatoriCandidati, tipoParametro, valore
        );

        // Invia comandi a tutti gli attuatori selezionati dalla strategia
        for (ActuationResult risultato : risultati) {
            // L'aggiornamento del target nella stanza avviene tramite il flusso Observer:
            // Attuatore.action() -> ObservableParameter.notifyObservers() -> Stanza.update()
            commandSender.inviaComando(
                risultato.getAttuatore(),
                tipoParametro,
                risultato.getValore()
            );
        }

        // Notifica gli observer che un parametro e' stato impostato
        notifyObservers("PARAMETRO_IMPOSTATO");
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

}

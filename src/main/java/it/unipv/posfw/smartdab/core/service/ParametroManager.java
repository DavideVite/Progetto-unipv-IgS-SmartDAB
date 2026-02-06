package it.unipv.posfw.smartdab.core.service;

import java.util.List;

import it.unipv.posfw.smartdab.adapter.facade.AttuatoreFacade;
import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.model.casa.Stanza;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.Dispositivo;
import it.unipv.posfw.smartdab.core.domain.model.parametro.IParametroValue;
import it.unipv.posfw.smartdab.core.domain.model.parametro.ParametroValue;
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
 * Vantaggi:
 * 1. Il core non conosce piu' il protocollo di comunicazione
 * 2. "SETPOINT" e il formato Request sono in CommandSenderAdapter
 * 3. Possiamo cambiare protocollo senza modificare questo service
 * 4. Architettura Esagonale: dipendenze corrette (Infrastructure -> Core)
 */
public class ParametroManager {
    private final GestoreStanze gestoreStanze;
    private final ICommandSender commandSender;

    public ParametroManager(GestoreStanze gestoreStanze, ICommandSender commandSender) {
        this.gestoreStanze = gestoreStanze;
        this.commandSender = commandSender;
    }

    // Trova primo attuatore idoneo nella stanza che supporta il parametro richiesto
    public Dispositivo getDispositivoIdoneo(String stanzaId, DispositivoParameter tipoParametro) {
        List<Dispositivo> dispositivi = gestoreStanze.getDispositiviPerStanza(stanzaId);
        if (dispositivi == null) return null;

        for (Dispositivo d : dispositivi) {
            // Verifica se e' un attuatore tramite instanceof (risolve LSP punto 4.1)
            if (!(d instanceof AttuatoreFacade attuatore)) {
                continue;
            }

            // Deve essere attivo
            if (!attuatore.isActive()) {
                continue;
            }

            // Usa supportaParametro (risolve Demeter punto 3.3)
            if (attuatore.supportaParametro(tipoParametro)) {
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

        // Tenta invio via CommandSender se esiste un dispositivo di dominio
        Dispositivo dispositivo = getDispositivoIdoneo(stanzaId, tipoParametro);
        if (dispositivo != null) {
            // Delega all'Output Port - il core non conosce il protocollo
            commandSender.inviaComando(dispositivo, tipoParametro, valore);
        }

        // Aggiorna sempre il target della stanza direttamente
        double valoreNumerico = estraiValoreNumerico(tipoParametro, valore);
        stanza.updateTarget(tipoParametro.name(), valoreNumerico);

        return true;
    }

    /**
     * Estrae il valore numerico per aggiornare il target della stanza.
     * Nota: questa logica resta qui perche' serve per Stanza.updateTarget(),
     * non per il protocollo di comunicazione.
     */
    private double estraiValoreNumerico(DispositivoParameter tipo, IParametroValue valore) {
        if (valore instanceof ParametroValue pv) {
            switch (tipo.getType()) {
                case NUMERIC:
                    return pv.getAsDouble();
                case BOOLEAN:
                    return pv.getAsBoolean() ? 1.0 : 0.0;
                case ENUM:
                    return tipo.getAllowedValues().indexOf(pv.getAsString());
            }
        }
        return 0.0;
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

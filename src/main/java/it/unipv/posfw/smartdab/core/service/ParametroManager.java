package it.unipv.posfw.smartdab.core.service;

import java.util.List;

import it.unipv.posfw.smartdab.adapter.facade.AttuatoreFacade;
import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.enums.Message;
import it.unipv.posfw.smartdab.core.domain.model.casa.Stanza;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.Dispositivo;
import it.unipv.posfw.smartdab.core.domain.model.parametro.IParametroValue;
import it.unipv.posfw.smartdab.core.domain.model.parametro.ParametroValue;
import it.unipv.posfw.smartdab.core.domain.model.scenario.StanzaConfig;
import it.unipv.posfw.smartdab.core.port.messaging.IEventBusClient;
import it.unipv.posfw.smartdab.infrastructure.messaging.request.Request;

public class ParametroManager {
    private final GestoreStanze gestoreStanze;
    private final IEventBusClient eventBusClient;

    public ParametroManager(GestoreStanze gestoreStanze, IEventBusClient eventBusClient) {
        this.gestoreStanze = gestoreStanze;
        this.eventBusClient = eventBusClient;
    }

    // Trova primo attuatore idoneo nella stanza che supporta il parametro richiesto
    public Dispositivo getDispositivoIdoneo(String stanzaId, DispositivoParameter tipoParametro) {
        List<Dispositivo> dispositivi = gestoreStanze.getDispositiviPerStanza(stanzaId);
        if (dispositivi == null) return null;

        for (Dispositivo d : dispositivi) {
            // 1. Verifica se è un attuatore tramite cast
            try {
                AttuatoreFacade attuatore = (AttuatoreFacade) d;

                // 2. Deve essere attivo
                if (!attuatore.isActive()) {
                    continue;
                }

                // 3. Deve supportare il parametro richiesto
                if (attuatore.getTopic() != null && attuatore.getTopic().getParameter() == tipoParametro) {
                    return attuatore;
                }
            } catch (ClassCastException e) {
                // Non è un attuatore, passa al prossimo dispositivo
                continue;
            }
        }
        return null;
    }

    // Caso d'uso 1: Impostazione manuale
    public boolean impostaParametro(String stanzaId, DispositivoParameter tipoParametro, IParametroValue valore) {
        if (!valore.isValid()) return false;

        Dispositivo dispositivo = getDispositivoIdoneo(stanzaId, tipoParametro);
        if (dispositivo == null) return false;

        boolean successo = inviaComando(dispositivo, tipoParametro, valore);

        if (successo) {
            Stanza stanza = gestoreStanze.cercaStanzaPerId(stanzaId);
            if (stanza != null) {
                double valoreNumerico = estraiValoreNumerico(tipoParametro, valore);
                stanza.updateTarget(tipoParametro.name(), valoreNumerico);
            }
        }

        return successo;
    }

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

    // Invio all'EventBus tramite IEventBusClient
    private boolean inviaComando(Dispositivo dispositivo, DispositivoParameter tipo, IParametroValue valore) {
        Request request = Request.createRequest(dispositivo.getTopic(), "SETPOINT", valore);

        // Flusso: setRequest -> sendRequest
        eventBusClient.setRequest(request);
        Message result = eventBusClient.sendRequest(request);

        return result == Message.ACK;
    }
}

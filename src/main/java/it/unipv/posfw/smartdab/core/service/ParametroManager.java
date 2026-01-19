package it.unipv.posfw.smartdab.core.service;

import java.util.List;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameters;
import it.unipv.posfw.smartdab.core.domain.enums.Message;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.Dispositivo;
import it.unipv.posfw.smartdab.core.domain.model.parametro.IParametroValue;
import it.unipv.posfw.smartdab.core.domain.model.scenario.ScenarioStanzaConfig;
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
    public Dispositivo getDispositivoIdoneo(String stanzaId, DispositivoParameters tipoParametro) {
        List<Dispositivo> dispositivi = gestoreStanze.getDispositiviPerStanza(stanzaId);
        if (dispositivi == null) return null;

        for (Dispositivo d : dispositivi) {
            // 1. Deve essere un attuatore
            if (!d.isAttuatore()) {
                continue;
            }

            // 2. Deve essere attivo
            if (!d.isActive()) {
                continue;
            }

            // 3. Deve supportare il parametro richiesto
            if (d.getTopic() != null && d.getTopic().getParameter() == tipoParametro) {
                return d;
            }
        }
        return null;
    }

    // Caso d'uso 1: Impostazione manuale
    public boolean impostaParametro(String stanzaId, DispositivoParameters tipoParametro, IParametroValue valore) {
        if (!valore.isValid()) return false;

        Dispositivo dispositivo = getDispositivoIdoneo(stanzaId, tipoParametro);
        if (dispositivo == null) return false;

        return inviaComando(dispositivo, tipoParametro, valore);

          }

    // Caso d'uso 2: Applicazione scenario
    public boolean applicaScenarioConfig(ScenarioStanzaConfig config) {
        return impostaParametro(
            config.getStanzaId(),
            config.getTipo_parametro(),
            config.getParametro()
        );
    }

    // Invio all'EventBus tramite IEventBusClient

    private boolean inviaComando(Dispositivo dispositivo, DispositivoParameters tipo, IParametroValue valore) {
        Request request = Request.createRequest(dispositivo.getTopic(), "SETPOINT", valore);

        // Flusso: setRequest -> sendRequest
        eventBusClient.setRequest(request);
        Message result = eventBusClient.sendRequest(request);

        return result == Message.ACK;
    }
}
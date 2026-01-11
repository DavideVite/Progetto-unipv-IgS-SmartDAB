package it.unipv.posfw.smartdab.core.service;

import java.util.List;

import it.unipv.posfw.smartdab.core.domain.enums.EnumTipoParametro;
import it.unipv.posfw.smartdab.core.domain.enums.Message;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.Dispositivo;
import it.unipv.posfw.smartdab.core.domain.model.parametro.IParametroValue;
import it.unipv.posfw.smartdab.core.domain.model.scenario.ScenarioStanzaConfig;
import it.unipv.posfw.smartdab.infrastructure.messaging.EventBus;
import it.unipv.posfw.smartdab.infrastructure.messaging.request.Request;

public class ParametroManager {
    private final GestoreStanze gestoreStanze;
    private final EventBus eventBus;

    public ParametroManager(GestoreStanze gestoreStanze, EventBus eventBus) {
        this.gestoreStanze = gestoreStanze;
        this.eventBus = eventBus;
    }

    // Trova primo dispositivo idoneo nella stanza
    // TODO: Decommentare quando supportaParametro() viene abilitato in Dispositivo
    public Dispositivo getDispositivoIdoneo(String stanzaId, EnumTipoParametro tipoParametro) {
        List<Dispositivo> dispositivi = gestoreStanze.getDispositiviPerStanza(stanzaId);
        if (dispositivi == null) return null;

        for (Dispositivo d : dispositivi) {
            // if (d.isActive() && d.supportaParametro(tipoParametro)) {
            if (d.isActive()) {  // Temporaneo: ritorna il primo dispositivo attivo
                return d;
            }
        }
        return null;
    }

    // Caso d'uso 1: Impostazione manuale
    public boolean impostaParametro(String stanzaId, EnumTipoParametro tipoParametro, IParametroValue valore) {
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

    // Invio all'EventBus
    private boolean inviaComando(Dispositivo dispositivo, EnumTipoParametro tipo, IParametroValue valore) {
        // TODO: Da definire con il collega - formato Request
        Request request = new Request(dispositivo.getTopic(), "SETPOINT", valore);
        Message result = eventBus.sendRequest(request);
        return result == Message.ACK;
    }
}

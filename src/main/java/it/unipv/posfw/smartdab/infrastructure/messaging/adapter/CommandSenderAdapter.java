package it.unipv.posfw.smartdab.infrastructure.messaging.adapter;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.enums.Message;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.Dispositivo;
import it.unipv.posfw.smartdab.core.domain.model.parametro.IParametroValue;
import it.unipv.posfw.smartdab.core.port.communication.ICommandSender;
import it.unipv.posfw.smartdab.core.port.messaging.IEventBusClient;
import it.unipv.posfw.smartdab.infrastructure.messaging.request.Request;

/**
 * Adapter che implementa l'Output Port ICommandSender.
 *
 * RUOLO NELL'ARCHITETTURA:
 * - Traduce le chiamate dal dominio (ICommandSender) al protocollo di messaging (EventBus)
 * - Contiene i dettagli del protocollo: formato Request, chiave "SETPOINT", etc.
 * - Il core (ParametroManager) non conosce questi dettagli
 *
 * PERCHE' "SETPOINT" E' QUI E NON NEL CORE:
 * - "SETPOINT" e' parte del protocollo di comunicazione con i dispositivi
 * - Se il protocollo cambia (es. "SET_VALUE" invece di "SETPOINT"), modifichiamo solo qui
 * - Il core resta invariato
 *
 * ESTRAZIONE VALORE NUMERICO:
 * - La Request richiede un valore numerico (double)
 * - Questa logica di conversione e' specifica del protocollo
 * - Diversi protocolli potrebbero richiedere formati diversi
 */
public class CommandSenderAdapter implements ICommandSender {

    private final IEventBusClient eventBusClient;

    // Costante per il tipo di comando - dettaglio del protocollo
    private static final String COMMAND_TYPE = Message.SETPOINT.name();

    public CommandSenderAdapter(IEventBusClient eventBusClient) {
        this.eventBusClient = eventBusClient;
    }

    /**
     * Invia un comando a un dispositivo per impostare un parametro.
     *
     * @param dispositivo Il dispositivo target (non null)
     * @param tipo Il tipo di parametro da impostare (non null)
     * @param valore Il valore da impostare (non null)
     * @return true se il comando e' stato ricevuto con ACK
     * @throws IllegalArgumentException se uno dei parametri e' null
     */
    @Override
    public boolean inviaComando(Dispositivo dispositivo, DispositivoParameter tipo, IParametroValue valore) {
        // Fail-fast con messaggi espliciti
        if (dispositivo == null) {
            throw new IllegalArgumentException("Dispositivo non puo' essere null");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("Tipo parametro non puo' essere null");
        }
        if (valore == null) {
            throw new IllegalArgumentException("Valore parametro non puo' essere null");
        }

        // Usa toNumericValue() invece di instanceof + switch.
        // La conversione e' delegata all'oggetto stesso (polimorfismo).
        Request request = Request.createRequest(
            dispositivo.getTopic(),
            COMMAND_TYPE,
            valore.toNumericValue()
        );

        // Flusso EventBus: setRequest -> sendRequest
        eventBusClient.setRequest(request);
        Message result = eventBusClient.sendRequest(request);

        return result == Message.ACK;
    }
}

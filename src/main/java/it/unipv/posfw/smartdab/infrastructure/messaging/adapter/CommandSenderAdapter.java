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

    @Override
    public boolean inviaComando(Dispositivo dispositivo, DispositivoParameter tipo, IParametroValue valore) {
        if (dispositivo == null || tipo == null || valore == null) {
            return false;
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

package it.unipv.posfw.smartdab.infrastructure.messaging.adapter;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.enums.Message;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.Dispositivo;
import it.unipv.posfw.smartdab.core.domain.model.parametro.IParametroValue;
import it.unipv.posfw.smartdab.core.domain.model.parametro.ParametroValue;
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

        // Estrai il valore numerico dal parametro (logica specifica del protocollo)
        double valoreNumerico = estraiValoreNumerico(tipo, valore);

        // Crea la Request nel formato richiesto dal protocollo
        Request request = Request.createRequest(
            dispositivo.getTopic(),
            COMMAND_TYPE,
            valoreNumerico
        );

        // Flusso EventBus: setRequest -> sendRequest
        eventBusClient.setRequest(request);
        Message result = eventBusClient.sendRequest(request);

        return result == Message.ACK;
    }

    /**
     * Estrae il valore numerico da un IParametroValue.
     * Questa logica e' specifica del protocollo: la Request richiede un double.
     *
     * @param tipo Il tipo di parametro
     * @param valore Il valore da convertire
     * @return Il valore come double
     */
    private double estraiValoreNumerico(DispositivoParameter tipo, IParametroValue valore) {
        if (valore instanceof ParametroValue pv) {
            switch (tipo.getType()) {
                case NUMERIC:
                    return pv.getAsDouble();
                case BOOLEAN:
                    return pv.getAsBoolean() ? 1.0 : 0.0;
                case ENUM:
                    // Restituisce l'indice del valore nell'enum
                    return tipo.getAllowedValues().indexOf(pv.getAsString());
            }
        }
        return 0.0;
    }
}

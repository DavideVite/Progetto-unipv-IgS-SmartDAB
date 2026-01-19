package it.unipv.posfw.smartdab.core.port.messaging;

import it.unipv.posfw.smartdab.core.domain.enums.Message;
import it.unipv.posfw.smartdab.infrastructure.messaging.request.Request;

/**
 * Interfaccia per la comunicazione con l'EventBus da parte di ParametroManager
 * Espone solo i metodi necessari per settare e inviare richieste ai dispositivi.
 */
public interface IEventBusClient {

    
    //Prepara la richiesta da inviare.
    void setRequest(Request request);

    /**
     * Invia la richiesta al dispositivo target.
     * @param request La richiesta da inviare
     * @return Message indicante l'esito (ACK, ERROR, etc.)
     */
    Message sendRequest(Request request);
}

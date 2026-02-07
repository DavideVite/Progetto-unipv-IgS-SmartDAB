package it.unipv.posfw.smartdab.core.port.communication;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.Dispositivo;
import it.unipv.posfw.smartdab.core.domain.model.parametro.IParametroValue;

/**
 * Output Port per l'invio di comandi ai dispositivi.
 *
 * 1. RIMUOVE "SETPOINT" HARDCODED DA ParametroManager:
 *    - Prima: ParametroManager conteneva Request.createRequest(..., "SETPOINT", ...)
 *    - Dopo: ParametroManager chiama solo inviaComando(), l'implementazione decide il formato
 *
 * 2. IL CORE NON CONOSCE IL PROTOCOLLO:
 *    - Il formato del messaggio (Request, Topic, "SETPOINT") e' un dettaglio infrastrutturale
 *    - Il core sa solo che vuole "impostare un parametro su un dispositivo"
 *    - L'implementazione (in infrastructure) sa COME farlo
 *
 * 3. INVERSIONE DELLE DIPENDENZE:
 *    - ParametroManager (core) dipende da questa interfaccia (core.port)
 *    - CommandSenderAdapter (infrastructure) implementa questa interfaccia
 *    - Il flusso delle dipendenze e' corretto: Infrastructure -> Core
 *
 * 4. FLESSIBILITA':
 *    - Possiamo cambiare il protocollo (MQTT, HTTP, WebSocket) senza toccare il core
 *    - Possiamo cambiare il formato dei messaggi senza toccare ParametroManager
 *
 *                    ┌─────────────────────────────────┐
 *                    │            CORE                 │
 *                    │  ParametroManager               │
 *                    │        │                        │
 *                    │        ▼                        │
 *                    │  ICommandSender                 │
 *                    │  (Output Port)                  │
 *                    └─────────────────────────────────┘
 *                                 △
 *                                 │ implementa
 *                    ┌────────────┴────────────────────┐
 *                    │       INFRASTRUCTURE            │
 *                    │  CommandSenderAdapter           │
 *                    │        │                        │
 *                    │        ▼ usa                    │
 *                    │  EventBus, Request, Topic       │
 *                    │  (dettagli protocollo)          │
 *                    └─────────────────────────────────┘
 */
public interface ICommandSender {

    /**
     * Invia un comando a un dispositivo per impostare un parametro.
     * L'implementazione si occupa di:
     * - Creare il messaggio nel formato corretto (Request)
     * - Usare il protocollo appropriato (EventBus)
     * - Gestire la risposta (ACK/ERROR)
     *
     * @param dispositivo Il dispositivo target
     * @param tipo Il tipo di parametro da impostare
     * @param valore Il valore da impostare
     * @return true se il comando e' stato inviato e ricevuto con successo
     */
    boolean inviaComando(Dispositivo dispositivo, DispositivoParameter tipo, IParametroValue valore);
}

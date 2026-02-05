
// TODO: forse posso semplificare l'interfaccia rimuovendo DispositivoParameter 
// un dipositivo gestisce solo un parametro, quindi potrei passare solo Dispositivo e IParametroValue

//Ho deciso di fare questa interfaccia in quanto ParametroManager è un elemento del dominio 
// dipendeva da un dettaglio dell'infrastruttura (ICommandSender) e questo rompeva il principio di separazione 

// Inoltre, la stringa "SETPOINT" è hardcoded nel servizio core. 
// Il formato del messaggio (come comporre Request) è un dettaglio infrastrutturale che potrebbe variare
//Bisogna delegare la creazione del comando a un'interfaccia nel port layer. Il core non deve conoscere il protocollo di comunicazione.

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.Dispositivo;
import it.unipv.posfw.smartdab.core.domain.model.parametro.IParametroValue;

    boolean inviaComando(Dispositivo dispositivo, DispositivoParameter tipo, IParametroValue valore);
}
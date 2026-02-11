package it.unipv.posfw.smartdab.strategy;

import it.unipv.posfw.smartdab.core.beans.DispositivoPOJO;

/**
 * Ogni implementazione di questa interfaccia definisce una logica specifica 
 * per stabilire quando un dispositivo deve essere disabilitato dal sistema.
 * *Questo approccio permette di differenziare il comportamento del gestore in base 
 * alla criticità del dispositivo 
 * * @author Beatrice Bertone
 * @version 1.0
 */

public interface MalfunzionamentoStrategy {
	
	boolean deveDisabilitare(DispositivoPOJO d, Object value, int conteggioErrori);

}

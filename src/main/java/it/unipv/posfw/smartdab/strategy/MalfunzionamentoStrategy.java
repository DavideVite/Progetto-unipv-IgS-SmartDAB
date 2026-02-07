package it.unipv.posfw.smartdab.strategy;

import it.unipv.posfw.smartdab.core.beans.DispositivoPOJO;

public interface MalfunzionamentoStrategy {
	
	boolean deveDisabilitare(DispositivoPOJO d, Object value, int conteggioErrori);

}

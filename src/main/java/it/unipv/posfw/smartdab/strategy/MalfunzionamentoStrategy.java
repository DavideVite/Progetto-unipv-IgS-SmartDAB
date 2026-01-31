package it.unipv.posfw.smartdab.strategy;

import it.unipv.posfw.smartdab.core.domain.model.dispositivo.Dispositivo;

public interface MalfunzionamentoStrategy {
	
	boolean deveDisabilitare(Dispositivo d, Object value, int conteggioErrori);

}

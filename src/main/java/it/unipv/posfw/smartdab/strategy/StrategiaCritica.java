package it.unipv.posfw.smartdab.strategy;

import it.unipv.posfw.smartdab.core.domain.model.dispositivo.Dispositivo;

public class StrategiaCritica implements MalfunzionamentoStrategy {
	@Override
	public boolean deveDisabilitare(Dispositivo d, Object value, int conteggio) {
		return value == null;
	}

}

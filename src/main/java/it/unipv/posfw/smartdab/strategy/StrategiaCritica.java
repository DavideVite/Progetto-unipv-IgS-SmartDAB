package it.unipv.posfw.smartdab.strategy;

import it.unipv.posfw.smartdab.core.beans.DispositivoPOJO;

public class StrategiaCritica implements MalfunzionamentoStrategy {
	@Override
	public boolean deveDisabilitare(DispositivoPOJO d, Object value, int conteggio) {
		return value == null;
	}

}

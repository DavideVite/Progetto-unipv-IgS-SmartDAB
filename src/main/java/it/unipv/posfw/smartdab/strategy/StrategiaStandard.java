package it.unipv.posfw.smartdab.strategy;

import it.unipv.posfw.smartdab.core.beans.DispositivoPOJO;

public class StrategiaStandard implements MalfunzionamentoStrategy{
	private final int SOGLIA = 3;
	
	@Override
	public boolean deveDisabilitare(DispositivoPOJO d, Object value, int conteggio) {
		return value == null && conteggio >= SOGLIA;
	    }
	  }

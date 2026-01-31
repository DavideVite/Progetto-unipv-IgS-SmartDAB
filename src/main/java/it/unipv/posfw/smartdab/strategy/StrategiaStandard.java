package it.unipv.posfw.smartdab.strategy;

import it.unipv.posfw.smartdab.core.domain.model.dispositivo.Dispositivo;

public class StrategiaStandard implements MalfunzionamentoStrategy{
	private final int SOGLIA = 3;
	
	@Override
	public boolean deveDisabilitare(Dispositivo d, Object value, int conteggio) {
		return value == null && conteggio >= SOGLIA;
	    }
	  }

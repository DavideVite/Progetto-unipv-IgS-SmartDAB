package it.unipv.posfw.smartdab.strategy;

import it.unipv.posfw.smartdab.core.beans.DispositivoPOJO;

/**
 * Implementazione standard della strategia di gestione malfunzionamenti.
 * * @author Beatrice Bertone
 * @version 1.0
 */

public class StrategiaStandard implements MalfunzionamentoStrategy{
	private final int SOGLIA = 3;
	
	@Override
	public boolean deveDisabilitare(DispositivoPOJO d, Object value, int conteggio) {
		return value == null && conteggio >= SOGLIA;
	    }
	  }

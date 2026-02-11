package it.unipv.posfw.smartdab.strategy;

import it.unipv.posfw.smartdab.core.beans.DispositivoPOJO;

/**
 * Implementazione della strategia critica di gestione malfunzionamenti.
 * Applica una logica di tolleranza zero: il dispositivo viene disabilitato
 * immediatamente al primo segnale di mancata comunicazione.
 * * @author Beatrice Bertone
 * @version 1.0
 */

public class StrategiaCritica implements MalfunzionamentoStrategy {
	@Override
	public boolean deveDisabilitare(DispositivoPOJO d, Object value, int conteggio) {
		return value == null;
	}

}

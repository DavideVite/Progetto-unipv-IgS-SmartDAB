package it.unipv.posfw.smartdab.core.service.strategy;

import it.unipv.posfw.smartdab.core.domain.model.scenario.Scenario;
import it.unipv.posfw.smartdab.core.service.ParametroManager;

/**
 * Strategy per l'attivazione di uno scenario.
 * Permette di variare l'algoritmo di attivazione (immediata, prioritizzata, ecc.)
 * senza modificare ScenarioManager.
 * Applicazione del Design Pattern Strategy e del principio di Open/Closed e Protected Variations.
 * @param scenario Lo scenario da attivare
 * @param parmetroManager applica ogni configurazione
 * @return true se tutte le configurazioni sono state applicate con successo, false altrimenti
 */
public interface ScenarioActivationStrategy {
	boolean attiva(Scenario scenario, ParametroManager parametroManager);
}

package it.unipv.posfw.smartdab.core.service.strategy;

import it.unipv.posfw.smartdab.core.domain.model.scenario.Scenario;
import it.unipv.posfw.smartdab.core.domain.model.scenario.StanzaConfig;
import it.unipv.posfw.smartdab.core.service.ParametroManager;

/**
 * Strategia di attivazione immediata: applica tutte le configurazioni subito.
 * Corrisponde al comportamento originale di ScenarioManager.attivaScenario().
 */
public class ImmediateActivationStrategy implements ScenarioActivationStrategy {

	@Override
	public boolean attiva(Scenario scenario, ParametroManager parametroManager) {
		boolean tuttiSuccesso = true;
		for (StanzaConfig config : scenario.getConfigurazioni()) {
			if (!parametroManager.applicaStanzaConfig(config)) {
				tuttiSuccesso = false;
			}
		}
		return tuttiSuccesso;
	}
}

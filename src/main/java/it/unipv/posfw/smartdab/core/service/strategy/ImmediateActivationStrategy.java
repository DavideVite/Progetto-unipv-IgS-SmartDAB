package it.unipv.posfw.smartdab.core.service.strategy;

import it.unipv.posfw.smartdab.core.domain.model.scenario.Scenario;
import it.unipv.posfw.smartdab.core.domain.model.scenario.StanzaConfig;
import it.unipv.posfw.smartdab.core.service.ParametroManager;

/**
 * Strategia di attivazione immediata: applica tutte le configurazioni subito.
 * Corrisponde al comportamento originale di ScenarioManager.attivaScenario().
 *
 * Le eccezioni (StanzaNonTrovataException, ParametroNonValidoException) vengono
 * catturate e logggate, ma non interrompono l'esecuzione delle altre configurazioni.
 */
public class ImmediateActivationStrategy implements ScenarioActivationStrategy {

	@Override
	public boolean attiva(Scenario scenario, ParametroManager parametroManager) {
		boolean tuttiSuccesso = true;
		for (StanzaConfig config : scenario) {
			try {
				parametroManager.applicaStanzaConfig(config);
			} catch (RuntimeException e) {
				System.err.println("Errore applicazione config stanza " + config.getStanzaId()
					+ ": " + e.getMessage());
				tuttiSuccesso = false;
			}
		}
		return tuttiSuccesso;
	}
}

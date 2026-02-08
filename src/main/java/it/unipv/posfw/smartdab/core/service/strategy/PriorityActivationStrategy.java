package it.unipv.posfw.smartdab.core.service.strategy;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.model.scenario.Scenario;
import it.unipv.posfw.smartdab.core.domain.model.scenario.StanzaConfig;
import it.unipv.posfw.smartdab.core.service.ParametroManager;

/**
 * Strategia di attivazione prioritizzata: applica le configurazioni
 * in ordine di priorita' (sicurezza -> comfort -> estetica).
 */
public class PriorityActivationStrategy implements ScenarioActivationStrategy {

	/**
	 * Applica le configurazioni in ordine di priorita'.
	 * Le eccezioni vengono catturate e loggate, ma non interrompono l'esecuzione.
	 */
	@Override
	public boolean attiva(Scenario scenario, ParametroManager parametroManager) {
		List<StanzaConfig> sorted = scenario.getConfigurazioni().stream()
				.sorted(Comparator.comparingInt(c -> getPriority(c.getTipo_parametro())))
				.collect(Collectors.toList());

		boolean tuttiSuccesso = true;
		for (StanzaConfig config : sorted) {
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

	/**
	 * Restituisce la priorita' di un parametro (valore piu' basso = priorita' piu' alta).
	 * 0 = Sicurezza, 1 = Comfort, 2 = Estetica.
	 */
	private int getPriority(DispositivoParameter param) {
		switch (param) {
			// Sicurezza
			case SENSORE_PRESENZA:
			case SENSORE_MOVIMENTO:
			case CONTATTO_PORTA:
			case FUMO:
			case GAS:
			case MANOMISSIONE:
				return 0;

			// Comfort
			case TEMPERATURA:
			case UMIDITA:
			case PRESSIONE:
			case VENTILAZIONE:
			case TEMPERATURA_FLUIDO:
				return 1;

			// Estetica
			case LUMINOSITA:
			case LUCE_RGB:
			case RUMORE:
			default:
				return 2;
		}
	}
}

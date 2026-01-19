package it.unipv.posfw.smartdab.core.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.enums.EnumScenarioType;
import it.unipv.posfw.smartdab.core.domain.model.parametro.BooleanValue;
import it.unipv.posfw.smartdab.core.domain.model.parametro.EnumValue;
import it.unipv.posfw.smartdab.core.domain.model.parametro.NumericValue;
import it.unipv.posfw.smartdab.core.domain.model.scenario.Scenario;
import it.unipv.posfw.smartdab.core.domain.model.scenario.ScenarioStanzaConfig;

public class ScenarioManager {

	private Map<String, Scenario> scenari;

	// Costruttore
	public ScenarioManager() {
		this.scenari = new HashMap<>();
	}

	// CRUD Scenari

	public Scenario creaScenario(String nome, EnumScenarioType tipo) {
		if (esisteScenario(nome)){
			throw new IllegalArgumentException("Scenario con nome '" + nome + "' esiste già");
		}
		else {
			Scenario scenario = new Scenario(nome, tipo);
			scenari.put(nome, scenario);
			return scenario;
		}

	}

	// secondo modo di chiamare creaScenario
	public Scenario creaScenario(String nome) {
		Scenario scenario = new Scenario(nome);
		scenari.put(nome, scenario);
		return scenario;
	}


	public Scenario getScenario(String nomeScenario) {
		return scenari.get(nomeScenario);
	}


	public boolean eliminaScenario(String nomeScenario) {
		return scenari.remove(nomeScenario) != null;
	}



	// Attivazione e Disattivazione

	/**
	 * Attiva uno scenario ed esegue automaticamente le sue configurazioni.
	 * Cicla tutte le ScenarioStanzaConfig dello scenario e le passa al ParametroManager
	 * che le tratta come impostazioni di parametri manuali.
	 *
	 * @param nomeScenario Nome dello scenario da attivare
	 * @param parametroManager Il ParametroManager per applicare le configurazioni
	 * @return true se tutte le configurazioni sono state applicate con successo,
	 *         false se lo scenario non esiste o se almeno una configurazione è fallita
	 */
	public boolean attivaScenario(String nomeScenario, ParametroManager parametroManager) {
		Scenario scenario = scenari.get(nomeScenario);
		if (scenario == null) {
			return false;
		}

		// Attiva lo scenario (setta il flag)
		scenario.attivaScenario();

		// Esegui le configurazioni
		boolean tuttiSuccesso = true;
		for (ScenarioStanzaConfig config : scenario.getConfigurazioni()) {
			if (!parametroManager.applicaScenarioConfig(config)) {
				tuttiSuccesso = false;
				// Continua comunque con le altre configurazioni
			}
		}
		return tuttiSuccesso;
	}


	public boolean disattivaScenario(String nomeScenario) {
		Scenario scenario = scenari.get(nomeScenario);
		if (scenario != null) {
			scenario.disattivaScenario();
			return true;
		}
		return false;
	}


	public boolean esisteScenario(String nomeScenario) {
		return scenari.containsKey(nomeScenario);
	}

	// Getters

	public Collection<Scenario> getTuttiScenari() {
		return scenari.values();
	}


	public Map<String, Scenario> getScenari() {
		return scenari;
	}


	public int getNumeroScenari() {
		return scenari.size();
	}

	
    // Esecuzione Scenario (senza attivazione). Adesso si trova in attivaScenario.
	// public boolean eseguiScenario(String nomeScenario, ParametroManager parametroManager) {
	// 	Scenario scenario = getScenario(nomeScenario);
	// 	if (scenario == null) return false;

	// 	boolean tuttiSuccesso = true;
	// 	for (ScenarioStanzaConfig config : scenario.getConfigurazioni()) {
	// 		if (!parametroManager.applicaScenarioConfig(config)) {
	// 			tuttiSuccesso = false;
	// 			// Continua comunque con le altre configurazioni
	// 		}
	// 	}
	// 	return tuttiSuccesso;
	// }


	/**
	 * Crea una configurazione con valore numerico (es. temperatura, luminosità).
	 */
	public ScenarioStanzaConfig creaConfigNumerico(
			String stanzaId,
			DispositivoParameter tipoParametro,
			double valore,
			Double min,
			Double max,
			String unit) {

		NumericValue numericValue = new NumericValue(valore, min, max, unit);
		if (!numericValue.isValid()) {
			throw new IllegalArgumentException("Valore non valido: " + valore +
					" (min: " + min + ", max: " + max + ")");
		}
		return new ScenarioStanzaConfig(stanzaId, numericValue, tipoParametro);
	}

	/**
	 * Crea una configurazione con valore booleano (es. acceso/spento).
	 */
	public ScenarioStanzaConfig creaConfigBooleano(
			String stanzaId,
			DispositivoParameter tipoParametro,
			boolean valore,
			String labelTrue,
			String labelFalse) {

		BooleanValue booleanValue = new BooleanValue(valore, labelTrue, labelFalse);
		return new ScenarioStanzaConfig(stanzaId, booleanValue, tipoParametro);
	}

	/**
	 * Crea una configurazione con valore enum (es. modalità di funzionamento).
	 */
	public ScenarioStanzaConfig creaConfigEnum(
			String stanzaId,
			DispositivoParameter tipoParametro,
			String valoreSelezionato,
			List<String> valoriAmmessi) {

		EnumValue enumValue = new EnumValue(valoreSelezionato, valoriAmmessi);
		if (!enumValue.isValid()) {
			throw new IllegalArgumentException("Valore non ammesso: " + valoreSelezionato +
					". Valori ammessi: " + valoriAmmessi);
		}
		return new ScenarioStanzaConfig(stanzaId, enumValue, tipoParametro);
	}

	// ===== GESTIONE CONFIGURAZIONI =====

	/**
	 * Aggiunge una configurazione a uno scenario esistente.
	 */
	public boolean aggiungiConfigurazione(String nomeScenario, ScenarioStanzaConfig config) {
		Scenario scenario = scenari.get(nomeScenario);
		if (scenario == null) return false;
		scenario.aggiungiConfigurazione(config);
		return true;
	}

	/**
	 * Rimuove una configurazione da uno scenario esistente.
	 */
	public boolean rimuoviConfigurazione(String nomeScenario, ScenarioStanzaConfig config) {
		Scenario scenario = scenari.get(nomeScenario);
		if (scenario == null) return false;
		return scenario.rimuoviConfigurazione(config);
	}
}

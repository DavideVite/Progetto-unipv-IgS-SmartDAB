package it.unipv.posfw.smartdab.core.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.enums.EnumScenarioType;
import it.unipv.posfw.smartdab.core.domain.model.scenario.Scenario;
import it.unipv.posfw.smartdab.core.domain.model.scenario.StanzaConfig;

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
	 * Cicla tutte le StanzaConfig dello scenario e le passa al ParametroManager
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
		for (StanzaConfig config : scenario.getConfigurazioni()) {
			if (!parametroManager.applicaStanzaConfig(config)) {
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

	// ===== GESTIONE CONFIGURAZIONI =====

	/**
	 * Aggiunge una configurazione a uno scenario esistente.
	 */
	public boolean aggiungiConfigurazione(String nomeScenario, StanzaConfig config) {
		Scenario scenario = scenari.get(nomeScenario);
		if (scenario == null) return false;
		scenario.aggiungiConfigurazione(config);
		return true;
	}

	/**
	 * Rimuove una configurazione da uno scenario esistente.
	 */
	public boolean rimuoviConfigurazione(String nomeScenario, StanzaConfig config) {
		Scenario scenario = scenari.get(nomeScenario);
		if (scenario == null) return false;
		return scenario.rimuoviConfigurazione(config);
	}

	/**
	 * Rimuove una configurazione identificata da stanzaId e tipoParametro.
	 */
	public boolean rimuoviConfigurazione(String nomeScenario, String stanzaId, DispositivoParameter tipoParametro) {
		Scenario scenario = scenari.get(nomeScenario);
		if (scenario == null) return false;

		List<StanzaConfig> configurazioni = scenario.getConfigurazioni();
		for (StanzaConfig config : configurazioni) {
			if (config.getStanzaId().equals(stanzaId) && config.getTipo_parametro() == tipoParametro) {
				return scenario.rimuoviConfigurazione(config);
			}
		}
		return false;
	}

	/**
	 * Ritorna la lista delle configurazioni di uno scenario.
	 */
	public List<StanzaConfig> getConfigurazioniScenario(String nomeScenario) {
		Scenario scenario = scenari.get(nomeScenario);
		if (scenario == null) return null;
		return scenario.getConfigurazioni();
	}
}

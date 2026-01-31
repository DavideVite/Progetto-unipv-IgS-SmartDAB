package it.unipv.posfw.smartdab.core.controller;

import java.util.List;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.enums.ParameterType;
import it.unipv.posfw.smartdab.core.domain.model.scenario.Scenario;
import it.unipv.posfw.smartdab.core.domain.model.scenario.StanzaConfig;
import it.unipv.posfw.smartdab.core.service.ParametroManager;
import it.unipv.posfw.smartdab.core.service.ScenarioManager;
import it.unipv.posfw.smartdab.factory.StanzaConfigFactory;

/**
 * Controller per la gestione degli Scenari.
 * Coordina le operazioni tra Factory e ScenarioManager.
 *
 * TODO: Da completare con altri metodi per gestione completa scenari
 */
public class ScenarioController {

	private ScenarioManager scenarioManager;

	public ScenarioController(ScenarioManager scenarioManager) {
		this.scenarioManager = scenarioManager;
	}

	/**
	 * Crea uno scenario vuoto.
	 */
	public Scenario creaScenarioVuoto(String nome) {
		return scenarioManager.creaScenario(nome);
	}

	/**
	 * Crea una StanzaConfig usando la Factory.
	 * Determina automaticamente il tipo di parametro e chiama il metodo factory corretto.
	 */
	public StanzaConfig creaStanzaConfig(String stanzaId, DispositivoParameter tipoParametro, Object valore) {
		ParameterType type = tipoParametro.getType();

		switch (type) {
			case NUMERIC:
				if (!(valore instanceof Number)) {
					throw new IllegalArgumentException("Valore deve essere numerico per " + tipoParametro);
				}
				double val = ((Number) valore).doubleValue();
				if (tipoParametro.getMin() != null && val < tipoParametro.getMin()) {
					throw new IllegalArgumentException("Valore " + val + " sotto il minimo consentito (" + tipoParametro.getMin() + " " + tipoParametro.getUnit() + ") per " + tipoParametro);
				}
				if (tipoParametro.getMax() != null && val > tipoParametro.getMax()) {
					throw new IllegalArgumentException("Valore " + val + " sopra il massimo consentito (" + tipoParametro.getMax() + " " + tipoParametro.getUnit() + ") per " + tipoParametro);
				}
				return StanzaConfigFactory.creaConfigNumerico(stanzaId, tipoParametro, val);

			case BOOLEAN:
				if (!(valore instanceof Boolean)) {
					throw new IllegalArgumentException("Valore deve essere booleano per " + tipoParametro);
				}
				return StanzaConfigFactory.creaConfigBooleano(stanzaId, tipoParametro, (Boolean) valore);

			case ENUM:
				if (!(valore instanceof String)) {
					throw new IllegalArgumentException("Valore deve essere stringa per " + tipoParametro);
				}
				String valEnum = (String) valore;
				if (tipoParametro.getAllowedValues() != null && !tipoParametro.getAllowedValues().contains(valEnum)) {
					throw new IllegalArgumentException("Valore \"" + valEnum + "\" non ammesso per " + tipoParametro + ". Valori consentiti: " + tipoParametro.getAllowedValues());
				}
				return StanzaConfigFactory.creaConfigEnum(stanzaId, tipoParametro, valEnum);

			default:
				throw new IllegalArgumentException("Tipo parametro non supportato: " + type);
		}
	}

	/**
	 * Aggiunge una StanzaConfig a uno scenario esistente.
	 */
	public boolean aggiungiConfigAScenario(String nomeScenario, StanzaConfig config) {
		return scenarioManager.aggiungiConfigurazione(nomeScenario, config);
	}

	/**
	 * Rimuove una configurazione dallo scenario identificata da stanzaId e tipoParametro.
	 */
	public boolean rimuoviConfigDaScenario(String nomeScenario, String stanzaId, DispositivoParameter tipoParametro) {
		return scenarioManager.rimuoviConfigurazione(nomeScenario, stanzaId, tipoParametro);
	}

	/**
	 * Ritorna la lista delle configurazioni di uno scenario.
	 */
	public List<StanzaConfig> getConfigurazioniScenario(String nomeScenario) {
		return scenarioManager.getConfigurazioniScenario(nomeScenario);
	}

	/**
	 * Attiva uno scenario applicando tutte le sue configurazioni tramite ParametroManager.
	 */
	public boolean attivaScenario(String nomeScenario, ParametroManager parametroManager) {
		return scenarioManager.attivaScenario(nomeScenario, parametroManager);
	}

	/**
	 * Disattiva uno scenario.
	 */
	public boolean disattivaScenario(String nomeScenario) {
		return scenarioManager.disattivaScenario(nomeScenario);
	}

	/**
	 * Elimina uno scenario.
	 */
	public boolean eliminaScenario(String nomeScenario) {
		return scenarioManager.eliminaScenario(nomeScenario);
	}
}

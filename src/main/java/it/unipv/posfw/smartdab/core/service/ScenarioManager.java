package it.unipv.posfw.smartdab.core.service;

import it.unipv.posfw.smartdab.core.domain.enums.EnumScenarioType;
import it.unipv.posfw.smartdab.core.domain.model.scenario.Scenario;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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

	public boolean attivaScenario(String nomeScenario) {
		Scenario scenario = scenari.get(nomeScenario);
		if (scenario != null) {
			scenario.attivaScenario();
			return true;
		}
		return false;
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
}

package it.unipv.posfw.smartdab.core.domain.model.scenario;

import it.unipv.posfw.smartdab.core.domain.enums.EnumScenarioType;
import java.util.Iterator;
import java.util.LinkedHashSet;

import java.time.LocalDateTime;
import java.util.Set;


public class Scenario implements Iterable <StanzaConfig> , Comparable<Scenario> {

//	LocalDateTime() è un classe che rappresenta data e ora senza fuso orario
//	- non tiene traccia del timezone
//	- formato : "2025-01-05T14:30:00"
//	E' uno standard per gestire il tempo e permette vari modi di creazione e operazioni comuni.

	private String id;
	private String nome;
	private Set<StanzaConfig> configurazioni;
	private EnumScenarioType tipo_scenario;
	private boolean isActive;
	private LocalDateTime data_creazione;
	private	LocalDateTime data_ultima_modifica;


	public Scenario(String nome) {
		this.id = null;
		this.nome = nome;
		this.configurazioni = new LinkedHashSet<>();
		this.tipo_scenario = EnumScenarioType.PERSONALIZZATO;
		this.isActive = false;
		this.data_creazione = LocalDateTime.now();
		this.data_ultima_modifica = LocalDateTime.now();
	}

	public Scenario(String nome, EnumScenarioType tipo_scenario) {
		this.id = null;
		this.nome = nome;
		this.configurazioni = new LinkedHashSet<>();
		this.tipo_scenario = tipo_scenario;
		this.isActive = false;
		this.data_creazione = LocalDateTime.now();
		this.data_ultima_modifica = LocalDateTime.now();
	}

	// Costruttore completo per la ricostruzione dal database
	public Scenario(String id, String nome, EnumScenarioType tipo_scenario, boolean isActive,
			LocalDateTime data_creazione, LocalDateTime data_ultima_modifica) {
		this.id = id;
		this.nome = nome;
		this.configurazioni = new LinkedHashSet<>();
		this.tipo_scenario = tipo_scenario;
		this.isActive = isActive;
		this.data_creazione = data_creazione;
		this.data_ultima_modifica = data_ultima_modifica;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Scenario scenario = (Scenario) o;

		return id != null ? id.equals(scenario.id) : scenario.id == null;
	}

	@Override
	public int hashCode(){
		return id != null ? id.hashCode() : 0;
	}

	@Override
	public Iterator<StanzaConfig> iterator(){
		return configurazioni.iterator();
	}

	@Override
	public int compareTo(Scenario other) {
		return this.nome.compareTo(other.nome);
	}
	// I senguenti 2 metodi sono public per permettere a ScenarioManager di usarli.
	/*  Questa funzione aggiunge allo Scenario la nuova configurazione.
	/*Lo Scenario è sostanzialmente formato da un insime di configurazioni,
	/* cioè di oggetti StanzaConfig
	*/
	public void aggiungiConfigurazione(StanzaConfig config) {
	       configurazioni.add(config);
	       this.data_ultima_modifica = LocalDateTime.now();
	 }



	 public boolean rimuoviConfigurazione(StanzaConfig config) {
		   boolean removed = configurazioni.remove(config);
		   if (removed) {
		       this.data_ultima_modifica = LocalDateTime.now();
		   }
		   return removed;
	 }

	 //TO-DO: Rimane aperto come trattare le modifiche allo Scenario

	 // Attiva e Disattiva Scenario
	 public void attivaScenario() {
		 this.isActive = true;
	 }

	 public void disattivaScenario() {
		 this.isActive = false;

	 }


	 // Getters e Setters
	 public String getId() {
		 return id;
	 }

	 public void setId(String id) {
		 this.id = id;
	 }

	 public String getNome() {
		 return nome;
	 }

	 public Set<StanzaConfig> getConfigurazioni() {
	        return configurazioni;
	 }

	 public void setConfigurazioni(Set<StanzaConfig> configurazioni) {
		 this.configurazioni = configurazioni;
		 this.data_ultima_modifica = LocalDateTime.now();
	 }

	 public EnumScenarioType getTipo_scenario() {
		return tipo_scenario;
	}

	 public boolean isActive() {
		 return isActive;
	 }

	 public LocalDateTime getData_creazione() {
		return data_creazione;
	}


	 public LocalDateTime getData_ultima_modifica() {
		 return data_ultima_modifica;
	 }



	 // to String

	 @Override
	 public String toString() {
		return "Scenario [nome=" + nome + ", " + "\nconfigurazioni=" + configurazioni + "]";
	 }




}

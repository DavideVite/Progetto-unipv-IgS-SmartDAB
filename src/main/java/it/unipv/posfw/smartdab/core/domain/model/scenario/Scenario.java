package it.unipv.posfw.smartdab.core.domain.model.scenario;
<<<<<<< HEAD
import it.unipv.posfw.smartdab.core.domain.enums.EnumScenarioType; 

=======
>>>>>>> main
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import it.unipv.posfw.smartdab.core.domain.enums.EnumScenarioType;


public class Scenario {

//	LocalDateTime() è un classe che rappresenta data e ora senza fuso orario
//	- non tiene traccia del timezone
//	- formato : "2025-01-05T14:30:00"
//	E' uno standard per gestire il tempo e permette vari modi di creazione e operazioni comuni.


	private String nome;
	private List<ScenarioStanzaConfig> configurazioni;
	private EnumScenarioType tipo_scenario;
	private boolean isActive;
	private LocalDateTime data_creazione;
	private	LocalDateTime data_ultima_modifica;


	public Scenario(String nome) {
		this.nome = nome;
		this.configurazioni = new ArrayList<>();
		this.tipo_scenario = EnumScenarioType.PERSONALIZZATO;
		this.isActive = false;
		this.data_creazione = LocalDateTime.now();
		this.data_ultima_modifica = LocalDateTime.now();
	}

	public Scenario(String nome, EnumScenarioType tipo_scenario) {
		this.nome = nome;
		this.configurazioni = new ArrayList<>();
		this.tipo_scenario = tipo_scenario;
		this.isActive = false;
		this.data_creazione = LocalDateTime.now();
		this.data_ultima_modifica = LocalDateTime.now();

	}

	// I senguenti 2 metodi sono public per permettere a ScenarioManager di usarli.
	/*  Questa funzione aggiunge allo Scenario la nuova configurazione. 
	/*Lo Scenario è sostanzialmente formato da un insime di configurazioni,
	/* cioè di oggetti ScenarioStanzaConfig
	*/
	public void aggiungiConfigurazione(ScenarioStanzaConfig config) {
	       configurazioni.add(config);
	       this.data_ultima_modifica = LocalDateTime.now();
	 }



	 public boolean rimuoviConfigurazione(ScenarioStanzaConfig config) {
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


	 // Getters
	 public String getNome() {
		 return nome;
	 }

	 public List<ScenarioStanzaConfig> getConfigurazioni() {
	        return configurazioni;
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

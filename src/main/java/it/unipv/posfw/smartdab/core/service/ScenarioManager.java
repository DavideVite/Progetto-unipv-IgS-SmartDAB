package it.unipv.posfw.smartdab.core.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.unipv.posfw.smartdab.core.port.communication.observer.Observable;
import it.unipv.posfw.smartdab.core.port.communication.observer.Observer;
import it.unipv.posfw.smartdab.core.service.strategy.ImmediateActivationStrategy;
import it.unipv.posfw.smartdab.core.service.strategy.ScenarioActivationStrategy;
import it.unipv.posfw.smartdab.core.service.ScenarioImporter.ScenarioImportException;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.enums.EnumScenarioType;
import it.unipv.posfw.smartdab.core.domain.model.scenario.Scenario;
import it.unipv.posfw.smartdab.core.domain.model.scenario.StanzaConfig;
import it.unipv.posfw.smartdab.core.port.persistence.IScenarioRepository;
import it.unipv.posfw.smartdab.core.domain.exception.ScenarioNonModificabileException;

/**
 * Servizio per la gestione degli scenari.
 *
 * REFACTORING: Inversione delle Dipendenze (DIP)
 * - Prima: Dipendeva direttamente da ScenarioDAO (infrastructure)
 * - Dopo: Dipende da IScenarioRepository (core.port) - Output Port
 *
 * Vantaggi:
 * 1. Il core non conosce piu' MySQL o altri dettagli di persistenza
 * 2. Testabilita: possiamo iniettare un mock di IScenarioRepository
 * 3. Flessibilita: cambiare database senza modificare questo service
 * 4. Architettura Esagonale: il flusso delle dipendenze e' corretto
 *    (Infrastructure -> Core, mai viceversa)
 */
public class ScenarioManager implements Observable {

	private Map<String, Scenario> scenari;
	private final IScenarioRepository scenarioRepository;
	private final List<Observer> observers = new ArrayList<>();
	private ScenarioActivationStrategy activationStrategy = new ImmediateActivationStrategy();

	public void setActivationStrategy(ScenarioActivationStrategy strategy) {
		this.activationStrategy = strategy;
	}

	@Override
	public void addObserver(Observer observer) { observers.add(observer); }

	@Override
	public void removeObserver(Observer observer) { observers.remove(observer); }

	@Override
	public void notifyObservers(Object args) {
		for (Observer o : observers) o.update(this, args);
	}

	/**
	 * Costruttore con Dependency Injection esplicita.
	 * Le dipendenze vengono iniettate dal MainController (Composition Root).
	 * Carica automaticamente gli scenari dal repository all'avvio.
	 *
	 * @param scenarioRepository Il repository per la persistenza degli scenari (Output Port)
	 */
	public ScenarioManager(IScenarioRepository scenarioRepository) {
		this.scenarioRepository = scenarioRepository;
		this.scenari = new HashMap<>();
		caricaDalRepository();
	}

	/**
	 * Carica tutti gli scenari dal repository e li inserisce nella mappa in memoria.
	 * Chiamato automaticamente nel costruttore.
	 */
	private void caricaDalRepository() {
		try {
			List<Scenario> scenariDalDb = scenarioRepository.findAll();
			for (Scenario scenario : scenariDalDb) {
				scenari.put(scenario.getNome(), scenario);
			}
			System.out.println("Caricati " + scenariDalDb.size() + " scenari dal repository");
		} catch (Exception e) {
			System.err.println("Errore durante il caricamento degli scenari dal repository: " + e.getMessage());
			e.printStackTrace();
		}
	}

	// CRUD Scenari

	/**
	 * Crea uno scenario con il tipo specificato.
	 * Solo il sistema puo' creare scenari PREDEFINITI (tramite inizializzaScenariPredefiniti).
	 */
	public Scenario creaScenario(String nome, EnumScenarioType tipo) {
		if (esisteScenario(nome)){
			throw new IllegalArgumentException("Scenario con nome '" + nome + "' esiste gia'");
		}
		else {
			Scenario scenario = new Scenario(nome, tipo);
			scenari.put(nome, scenario);

			// Persistenza nel repository
			scenarioRepository.save(scenario);

			notifyObservers("SCENARIO_CREATO");
			return scenario;
		}

	}

	// secondo modo di chiamare creaScenario
	public Scenario creaScenario(String nome) {
		if (esisteScenario(nome)){
			throw new IllegalArgumentException("Scenario con nome '" + nome + "' esiste gia'");
		}
		Scenario scenario = new Scenario(nome);
		scenari.put(nome, scenario);

		// Persistenza nel repository
		scenarioRepository.save(scenario);

		notifyObservers("SCENARIO_CREATO");
		return scenario;
	}


	public Scenario getScenario(String nomeScenario) {
		return scenari.get(nomeScenario);
	}


	public boolean eliminaScenario(String nomeScenario) {
		Scenario scenario = scenari.get(nomeScenario);
		if (scenario == null) {
			return false;
		}

		// Gli scenari predefiniti non possono essere eliminati
		if (scenario.getTipo_scenario() == EnumScenarioType.PREDEFINITO) {
			throw new ScenarioNonModificabileException("Lo scenario predefinito '" + nomeScenario + "' non puo' essere eliminato");
		}

		// Elimina dal repository (elimina anche le StanzaConfig associate)
		boolean eliminatoDalDb = scenarioRepository.delete(scenario.getId());

		// Rimuovi dalla mappa in memoria
		scenari.remove(nomeScenario);

		notifyObservers("SCENARIO_ELIMINATO");
		return eliminatoDalDb;
	}



	// Attivazione e Disattivazione

	/**
	 * Attiva uno scenario ed esegue automaticamente le sue configurazioni.
	 * Cicla tutte le StanzaConfig dello scenario e le passa al ParametroManager
	 * che le tratta come impostazioni di parametri manuali.
	 * Lo stato di attivazione viene persistito nel repository.
	 *
	 * @param nomeScenario Nome dello scenario da attivare
	 * @param parametroManager Il ParametroManager per applicare le configurazioni
	 * @return true se tutte le configurazioni sono state applicate con successo,
	 *         false se lo scenario non esiste o se almeno una configurazione e' fallita
	 */
	public boolean attivaScenario(String nomeScenario, ParametroManager parametroManager) {
		Scenario scenario = scenari.get(nomeScenario);
		if (scenario == null) {
			return false;
		}

		// Attiva lo scenario (setta il flag)
		scenario.attivaScenario();

		// Persiste lo stato di attivazione nel repository
		scenarioRepository.update(scenario);

		// Delega l'esecuzione delle configurazioni alla strategy
		boolean result = activationStrategy.attiva(scenario, parametroManager);

		notifyObservers("SCENARIO_ATTIVATO");
		return result;
	}


	public boolean disattivaScenario(String nomeScenario) {
		Scenario scenario = scenari.get(nomeScenario);
		if (scenario != null) {
			scenario.disattivaScenario();

			// Persiste lo stato di disattivazione nel repository
			scenarioRepository.update(scenario);

			notifyObservers("SCENARIO_DISATTIVATO");
			return true;
		}
		return false;
	}


	public boolean esisteScenario(String nomeScenario) {
		return scenari.containsKey(nomeScenario);
	}

	// Getters

	/**
	 * Restituisce una vista non modificabile di tutti gli scenari.
	 */
	public Collection<Scenario> getTuttiScenari() {
		return Collections.unmodifiableCollection(scenari.values());
	}

	/**
	 * Restituisce una vista non modificabile della mappa degli scenari.
	 * Il chiamante non puo' modificare la mappa direttamente.
	 */
	public Map<String, Scenario> getScenari() {
		return Collections.unmodifiableMap(scenari);
	}


	public int getNumeroScenari() {
		return scenari.size();
	}

	// ===== GESTIONE CONFIGURAZIONI =====

	/**
	 * Aggiunge una configurazione a uno scenario esistente.
	 * La modifica viene persistita nel repository.
	 */
	public boolean aggiungiConfigurazione(String nomeScenario, StanzaConfig config) {
		Scenario scenario = scenari.get(nomeScenario);
		if (scenario == null) return false;
		scenario.aggiungiConfigurazione(config);

		// Persiste la modifica nel repository
		scenarioRepository.update(scenario);

		return true;
	}

	/**
	 * Rimuove una configurazione da uno scenario esistente.
	 * La modifica viene persistita nel repository.
	 */
	public boolean rimuoviConfigurazione(String nomeScenario, StanzaConfig config) {
		Scenario scenario = scenari.get(nomeScenario);
		if (scenario == null) return false;
		boolean removed = scenario.rimuoviConfigurazione(config);

		if (removed) {
			// Persiste la modifica nel repository
			scenarioRepository.update(scenario);
		}

		return removed;
	}

	/**
	 * Rimuove una configurazione identificata da stanzaId e tipoParametro.
	 * La modifica viene persistita nel repository.
	 */
	public boolean rimuoviConfigurazione(String nomeScenario, String stanzaId, DispositivoParameter tipoParametro) {
		Scenario scenario = scenari.get(nomeScenario);
		if (scenario == null) return false;

		// Uso di Iterator per rimozione sicura durante l'iterazione
		Iterator<StanzaConfig> iterator = scenario.iterator();
		while (iterator.hasNext()) {
			StanzaConfig config = iterator.next();
			if (config.getStanzaId().equals(stanzaId) && config.getTipo_parametro() == tipoParametro) {
				iterator.remove();
				// Persiste la modifica nel repository
				scenarioRepository.update(scenario);
				return true;
			}
		}
		return false;
	}

	/**
	 * Ritorna la lista delle configurazioni di uno scenario.
	 */
	public Set<StanzaConfig> getConfigurazioniScenario(String nomeScenario) {
		Scenario scenario = scenari.get(nomeScenario);
		if (scenario == null) return null;
		return scenario.getConfigurazioni();
	}

	// ===== EXPORT / IMPORT SCENARI =====

	/**
	 * Esporta uno scenario su file.
	 * Utilizza ScenarioExporter con pattern Decorator (PrintWriter -> BufferedWriter -> FileWriter).
	 *
	 * @param nomeScenario Nome dello scenario da esportare
	 * @param file File di destinazione
	 * @return true se l'export e' riuscito, false se lo scenario non esiste
	 * @throws IOException Se si verifica un errore durante la scrittura
	 */
	public boolean esportaScenario(String nomeScenario, File file) throws IOException {
		Scenario scenario = scenari.get(nomeScenario);
		if (scenario == null) {
			return false;
		}

		ScenarioExporter exporter = new ScenarioExporter();
		exporter.esportaScenario(scenario, file);
		return true;
	}

	/**
	 * Importa uno scenario da file e lo aggiunge al sistema.
	 * Utilizza ScenarioImporter con pattern Decorator (BufferedReader -> FileReader)
	 * e StringTokenizer per il parsing.
	 *
	 * Se uno scenario con lo stesso nome esiste gia', viene generata un'eccezione.
	 *
	 * @param file File da cui importare
	 * @return Lo scenario importato
	 * @throws IOException Se si verifica un errore durante la lettura
	 * @throws ScenarioImportException Se il formato del file non e' valido
	 * @throws IllegalArgumentException Se uno scenario con lo stesso nome esiste gia'
	 */
	public Scenario importaScenario(File file) throws IOException, ScenarioImportException {
		ScenarioImporter importer = new ScenarioImporter();
		Scenario scenario = importer.importaScenario(file);

		// Verifica che non esista gia' uno scenario con lo stesso nome
		if (esisteScenario(scenario.getNome())) {
			throw new IllegalArgumentException(
				"Scenario con nome '" + scenario.getNome() + "' esiste gia'. " +
				"Eliminare lo scenario esistente prima di importare.");
		}

		// Aggiungi alla mappa in memoria
		scenari.put(scenario.getNome(), scenario);

		// Persisti nel repository
		scenarioRepository.save(scenario);

		notifyObservers("SCENARIO_IMPORTATO");
		return scenario;
	}

	/**
	 * Importa uno scenario da file, sovrascrivendo quello esistente se presente.
	 *
	 * @param file File da cui importare
	 * @param sovrascrivi Se true, sovrascrive lo scenario esistente
	 * @return Lo scenario importato
	 * @throws IOException Se si verifica un errore durante la lettura
	 * @throws ScenarioImportException Se il formato del file non e' valido
	 */
	public Scenario importaScenario(File file, boolean sovrascrivi)
			throws IOException, ScenarioImportException {

		ScenarioImporter importer = new ScenarioImporter();
		Scenario scenario = importer.importaScenario(file);

		// Se esiste gia' e sovrascrivi e' true, elimina quello esistente
		if (esisteScenario(scenario.getNome())) {
			if (sovrascrivi) {
				Scenario esistente = scenari.get(scenario.getNome());
				// Non permettere sovrascrittura di scenari predefiniti
				if (esistente.getTipo_scenario() == EnumScenarioType.PREDEFINITO) {
					throw new IllegalArgumentException(
						"Non e' possibile sovrascrivere lo scenario predefinito '" + scenario.getNome() + "'");
				}
				// Elimina quello esistente
				scenarioRepository.delete(esistente.getId());
				scenari.remove(scenario.getNome());
			} else {
				throw new IllegalArgumentException(
					"Scenario con nome '" + scenario.getNome() + "' esiste gia'.");
			}
		}

		// Aggiungi alla mappa in memoria
		scenari.put(scenario.getNome(), scenario);

		// Persisti nel repository
		scenarioRepository.save(scenario);

		notifyObservers("SCENARIO_IMPORTATO");
		return scenario;
	}

	/**
	 * Genera un nome file suggerito per l'export di uno scenario.
	 *
	 * @param nomeScenario Nome dello scenario
	 * @return Nome file suggerito o null se lo scenario non esiste
	 */
	public String suggerisciNomeFileExport(String nomeScenario) {
		Scenario scenario = scenari.get(nomeScenario);
		if (scenario == null) {
			return null;
		}
		ScenarioExporter exporter = new ScenarioExporter();
		return exporter.suggerisciNomeFile(scenario);
	}

	/**
	 * Verifica se un file ha il formato corretto per l'importazione.
	 *
	 * @param file File da verificare
	 * @return true se il formato sembra valido
	 */
	public boolean verificaFormatoFileImport(File file) {
		ScenarioImporter importer = new ScenarioImporter();
		return importer.verificaFormatoFile(file);
	}
}

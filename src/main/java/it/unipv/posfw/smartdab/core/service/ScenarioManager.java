package it.unipv.posfw.smartdab.core.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.enums.EnumScenarioType;
import it.unipv.posfw.smartdab.core.domain.model.casa.Stanza;
import it.unipv.posfw.smartdab.core.domain.model.scenario.Scenario;
import it.unipv.posfw.smartdab.core.domain.model.scenario.StanzaConfig;
import it.unipv.posfw.smartdab.factory.StanzaConfigFactory;
import it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao.ScenarioDAO;
import it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao.ScenarioDAOImpl;
import it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao.StanzaDAO;
import it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao.StanzaDAOImpl;

/**
 * Servizio per la gestione degli scenari.
 *
 * REFACTORING: Dependency Injection del DAO e persistenza nel database.
 * - Prima: Gli scenari venivano gestiti solo in memoria (HashMap)
 * - Dopo: Gli scenari vengono persistiti nel database tramite ScenarioDAO
 *
 * Vantaggi:
 * 1. Persistenza: gli scenari sopravvivono al riavvio dell'applicazione
 * 2. Testabilita: possibilita di iniettare mock DAO nei test
 * 3. Coerenza: stesso pattern di GestoreStanze
 */
public class ScenarioManager {

	private Map<String, Scenario> scenari;
	private final ScenarioDAO scenarioDAO;

	/**
	 * Costruttore con Dependency Injection.
	 * Usa l'implementazione di default ScenarioDAOImpl.
	 * Carica automaticamente gli scenari dal database all'avvio.
	 */
	public ScenarioManager() {
		this(new ScenarioDAOImpl());
	}

	/**
	 * Costruttore con Dependency Injection esplicita del DAO.
	 * Permette di iniettare mock DAO per i test.
	 * Carica automaticamente gli scenari dal database all'avvio.
	 *
	 * @param scenarioDAO Il DAO per la persistenza degli scenari
	 */
	public ScenarioManager(ScenarioDAO scenarioDAO) {
		this.scenarioDAO = scenarioDAO;
		this.scenari = new HashMap<>();
		caricaDalDatabase();
		inizializzaScenariPredefiniti();
	}

	/**
	 * Crea gli scenari predefiniti (Notte, Giorno, Assenza) se non esistono già.
	 * Ogni scenario viene configurato per tutte le stanze presenti nel database.
	 */
	private void inizializzaScenariPredefiniti() {
		try {
			StanzaDAO stanzaDAO = new StanzaDAOImpl();
			Set<Stanza> stanze = stanzaDAO.readAllStanze();

			if (stanze.isEmpty()) {
				System.out.println("Nessuna stanza trovata: scenari predefiniti non creati");
				return;
			}

			creaScenarioNotte(stanze);
			creaScenarioGiorno(stanze);
			creaScenarioAssenza(stanze);

		} catch (Exception e) {
			System.err.println("Errore durante l'inizializzazione degli scenari predefiniti: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void creaScenarioNotte(Set<Stanza> stanze) {
		if (esisteScenario("Notte")) return;
		Scenario s = creaScenario("Notte", EnumScenarioType.PREDEFINITO);
		for (Stanza stanza : stanze) {
			aggiungiConfigurazione("Notte", StanzaConfigFactory.creaConfigNumerico(stanza.getId(), DispositivoParameter.TEMPERATURA, 18.0));
			aggiungiConfigurazione("Notte", StanzaConfigFactory.creaConfigNumerico(stanza.getId(), DispositivoParameter.UMIDITA, 45.0));
			aggiungiConfigurazione("Notte", StanzaConfigFactory.creaConfigNumerico(stanza.getId(), DispositivoParameter.LUMINOSITA, 50.0));
		}
		System.out.println("Scenario predefinito 'Notte' creato con " + s.getConfigurazioni().size() + " configurazioni");
	}

	private void creaScenarioGiorno(Set<Stanza> stanze) {
		if (esisteScenario("Giorno")) return;
		Scenario s = creaScenario("Giorno", EnumScenarioType.PREDEFINITO);
		for (Stanza stanza : stanze) {
			aggiungiConfigurazione("Giorno", StanzaConfigFactory.creaConfigNumerico(stanza.getId(), DispositivoParameter.TEMPERATURA, 21.0));
			aggiungiConfigurazione("Giorno", StanzaConfigFactory.creaConfigNumerico(stanza.getId(), DispositivoParameter.UMIDITA, 45.0));
			aggiungiConfigurazione("Giorno", StanzaConfigFactory.creaConfigNumerico(stanza.getId(), DispositivoParameter.LUMINOSITA, 500.0));
		}
		System.out.println("Scenario predefinito 'Giorno' creato con " + s.getConfigurazioni().size() + " configurazioni");
	}

	private void creaScenarioAssenza(Set<Stanza> stanze) {
		if (esisteScenario("Assenza")) return;
		Scenario s = creaScenario("Assenza", EnumScenarioType.PREDEFINITO);
		for (Stanza stanza : stanze) {
			aggiungiConfigurazione("Assenza", StanzaConfigFactory.creaConfigNumerico(stanza.getId(), DispositivoParameter.TEMPERATURA, 16.0));
			aggiungiConfigurazione("Assenza", StanzaConfigFactory.creaConfigNumerico(stanza.getId(), DispositivoParameter.LUMINOSITA, 0.0));
			aggiungiConfigurazione("Assenza", StanzaConfigFactory.creaConfigBooleano(stanza.getId(), DispositivoParameter.SENSORE_PRESENZA, true));
			aggiungiConfigurazione("Assenza", StanzaConfigFactory.creaConfigBooleano(stanza.getId(), DispositivoParameter.SENSORE_MOVIMENTO, true));
		}
		System.out.println("Scenario predefinito 'Assenza' creato con " + s.getConfigurazioni().size() + " configurazioni");
	}

	/**
	 * Carica tutti gli scenari dal database e li inserisce nella mappa in memoria.
	 * Chiamato automaticamente nel costruttore.
	 */
	private void caricaDalDatabase() {
		try {
			List<Scenario> scenariDalDb = scenarioDAO.readAllScenari();
			for (Scenario scenario : scenariDalDb) {
				scenari.put(scenario.getNome(), scenario);
			}
			System.out.println("Caricati " + scenariDalDb.size() + " scenari dal database");
		} catch (Exception e) {
			System.err.println("Errore durante il caricamento degli scenari dal database: " + e.getMessage());
			e.printStackTrace();
		}
	}

	// CRUD Scenari

	/**
	 * Crea uno scenario con il tipo specificato.
	 * Solo il sistema può creare scenari PREDEFINITI (tramite inizializzaScenariPredefiniti).
	 */
	Scenario creaScenario(String nome, EnumScenarioType tipo) {
		if (esisteScenario(nome)){
			throw new IllegalArgumentException("Scenario con nome '" + nome + "' esiste già");
		}
		else {
			Scenario scenario = new Scenario(nome, tipo);
			scenari.put(nome, scenario);

			// Persistenza nel database
			scenarioDAO.insertScenario(scenario);

			return scenario;
		}

	}

	// secondo modo di chiamare creaScenario
	public Scenario creaScenario(String nome) {
		if (esisteScenario(nome)){
			throw new IllegalArgumentException("Scenario con nome '" + nome + "' esiste già");
		}
		Scenario scenario = new Scenario(nome);
		scenari.put(nome, scenario);

		// Persistenza nel database
		scenarioDAO.insertScenario(scenario);

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
			throw new IllegalArgumentException("Lo scenario predefinito '" + nomeScenario + "' non può essere eliminato");
		}

		// Elimina dal database (elimina anche le StanzaConfig associate)
		boolean eliminatoDalDb = scenarioDAO.deleteScenario(scenario.getId());

		// Rimuovi dalla mappa in memoria
		scenari.remove(nomeScenario);

		return eliminatoDalDb;
	}



	// Attivazione e Disattivazione

	/**
	 * Attiva uno scenario ed esegue automaticamente le sue configurazioni.
	 * Cicla tutte le StanzaConfig dello scenario e le passa al ParametroManager
	 * che le tratta come impostazioni di parametri manuali.
	 * Lo stato di attivazione viene persistito nel database.
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

		// Persiste lo stato di attivazione nel database
		scenarioDAO.updateScenario(scenario);

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

			// Persiste lo stato di disattivazione nel database
			scenarioDAO.updateScenario(scenario);

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
	 * La modifica viene persistita nel database.
	 */
	public boolean aggiungiConfigurazione(String nomeScenario, StanzaConfig config) {
		Scenario scenario = scenari.get(nomeScenario);
		if (scenario == null) return false;
		scenario.aggiungiConfigurazione(config);

		// Persiste la modifica nel database
		scenarioDAO.updateScenario(scenario);

		return true;
	}

	/**
	 * Rimuove una configurazione da uno scenario esistente.
	 * La modifica viene persistita nel database.
	 */
	public boolean rimuoviConfigurazione(String nomeScenario, StanzaConfig config) {
		Scenario scenario = scenari.get(nomeScenario);
		if (scenario == null) return false;
		boolean removed = scenario.rimuoviConfigurazione(config);

		if (removed) {
			// Persiste la modifica nel database
			scenarioDAO.updateScenario(scenario);
		}

		return removed;
	}

	/**
	 * Rimuove una configurazione identificata da stanzaId e tipoParametro.
	 * La modifica viene persistita nel database.
	 */
	public boolean rimuoviConfigurazione(String nomeScenario, String stanzaId, DispositivoParameter tipoParametro) {
		Scenario scenario = scenari.get(nomeScenario);
		if (scenario == null) return false;

		List<StanzaConfig> configurazioni = scenario.getConfigurazioni();
		for (StanzaConfig config : configurazioni) {
			if (config.getStanzaId().equals(stanzaId) && config.getTipo_parametro() == tipoParametro) {
				boolean removed = scenario.rimuoviConfigurazione(config);
				if (removed) {
					// Persiste la modifica nel database
					scenarioDAO.updateScenario(scenario);
				}
				return removed;
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

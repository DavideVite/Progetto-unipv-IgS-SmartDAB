package it.unipv.posfw.smartdab.infrastructure.persistence.adapter;

import java.util.List;
import java.util.Optional;

import it.unipv.posfw.smartdab.core.domain.enums.EnumScenarioType;
import it.unipv.posfw.smartdab.core.domain.model.scenario.Scenario;
import it.unipv.posfw.smartdab.core.domain.model.scenario.StanzaConfig;
import it.unipv.posfw.smartdab.core.port.persistence.IScenarioRepository;
import it.unipv.posfw.smartdab.core.service.ParametroValidator;
import it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao.ScenarioDAO;

/**
 * Adapter che implementa l'Output Port IScenarioRepository.
 *
 * - Traduce le chiamate dal dominio (IScenarioRepository) al DAO (ScenarioDAO)
 * - Il pattern DAO resta intatto e indipendente
 * - Aggiunge validazione multilivello prima della persistenza
 *
 * VALIDAZIONE MULTILIVELLO - PERCHE' E' UNA BUONA COSA:
 *
 * 1. DEFENSE IN DEPTH:
 *    - Livello 1 (UI): ParametroValidator in StanzeController/ScenarioFormPanel
 *    - Livello 2 (Service): Validazione in ParametroManager/ScenarioManager
 *    - Livello 3 (Adapter/Persistence): Validazione qui, ultima linea di difesa
 *
 * 2. PROTEZIONE DA BYPASS:
 *    - Se qualcuno chiama direttamente il repository (es. da test o altro service)
 *    - I dati vengono comunque validati prima di arrivare al database
 *
 * 3. RIUTILIZZO CODICE:
 *    - Usiamo lo stesso ParametroValidator del core (no duplicazione)
 *    - Infrastructure puo' dipendere da core (direzione corretta delle dipendenze)
 *
 * 4. FAIL FAST:
 *    - Meglio fallire con un messaggio chiaro prima di toccare il DB
 *    - Evita stati inconsistenti nel database
 */
public class ScenarioRepositoryAdapter implements IScenarioRepository {
    private final ScenarioDAO scenarioDAO;

    public ScenarioRepositoryAdapter(ScenarioDAO scenarioDAO) {
        this.scenarioDAO = scenarioDAO;
    }

    @Override
    public void save(Scenario scenario) {
        validaScenario(scenario);
        scenarioDAO.insertScenario(scenario);
    }

    @Override
    public void update(Scenario scenario) {
        validaScenario(scenario);
        scenarioDAO.updateScenario(scenario);
    }

    @Override
    public boolean delete(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("ID scenario non puo' essere nullo o vuoto");
        }
        return scenarioDAO.deleteScenario(id);
    }

    @Override
    public Optional<Scenario> findById(String id) {
        return scenarioDAO.readScenario(id);
    }

    @Override
    public Optional<Scenario> findByNome(String nome) {
        return scenarioDAO.readScenarioByNome(nome);
    }

    @Override
    public List<Scenario> findAll() {
        return scenarioDAO.readAllScenari();
    }

    @Override
    public List<Scenario> findByTipo(EnumScenarioType tipo) {
        return scenarioDAO.readScenariByTipo(tipo);
    }

    @Override
    public List<Scenario> findByActive(boolean active) {
        return scenarioDAO.readScenariByActive(active);
    }

    @Override
    public boolean existsByNome(String nome) {
        return scenarioDAO.existsByNome(nome);
    }

    @Override
    public int count() {
        return scenarioDAO.count();
    }

    // ==================== VALIDAZIONE MULTILIVELLO ====================

    /**
     * Valida lo scenario e tutte le sue configurazioni prima della persistenza.
     * Riutilizza ParametroValidator del core (no duplicazione di logica).
     *
     * @param scenario Lo scenario da validare
     * @throws IllegalArgumentException se la validazione fallisce
     */
    private void validaScenario(Scenario scenario) {
        if (scenario == null) {
            throw new IllegalArgumentException("Scenario non puo' essere nullo");
        }

        if (scenario.getNome() == null || scenario.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome scenario non puo' essere vuoto");
        }

        if (scenario.getTipo_scenario() == null) {
            throw new IllegalArgumentException("Tipo scenario non puo' essere nullo");
        }

        // Valida ogni StanzaConfig usando ParametroValidator del core
        for (StanzaConfig config : scenario) {
            validaStanzaConfig(config);
        }
    }

    /**
     * Valida una singola configurazione stanza.
     * Riutilizza la logica di validazione centralizzata in ParametroValidator.
     *
     * @param config La configurazione da validare
     * @throws IllegalArgumentException se la validazione fallisce
     */
    private void validaStanzaConfig(StanzaConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("StanzaConfig non puo' essere nulla");
        }

        if (config.getStanzaId() == null || config.getStanzaId().trim().isEmpty()) {
            throw new IllegalArgumentException("StanzaId non puo' essere vuoto in StanzaConfig");
        }

        if (config.getTipo_parametro() == null) {
            throw new IllegalArgumentException("Tipo parametro non puo' essere nullo in StanzaConfig");
        }

        if (config.getParametro() == null) {
            throw new IllegalArgumentException("Valore parametro non puo' essere nullo in StanzaConfig");
        }

        // Riutilizza ParametroValidator per validare il valore
        // Questo garantisce coerenza con la validazione UI
        String valoreStr = config.getParametro().getAsString();
        ParametroValidator.ValidazioneResult result =
            ParametroValidator.valida(config.getTipo_parametro(), valoreStr);

        if (!result.isValido()) {
            throw new IllegalArgumentException(
                "Validazione fallita per parametro " + config.getTipo_parametro().name() +
                " nella stanza " + config.getStanzaId() + ": " + result.getMessaggio()
            );
        }
    }
}

package it.unipv.posfw.smartdab.core.port.persistence;

import java.util.List;
import java.util.Optional;

import it.unipv.posfw.smartdab.core.domain.enums.EnumScenarioType;
import it.unipv.posfw.smartdab.core.domain.model.scenario.Scenario;

/**
 * Output Port per la persistenza degli Scenari (Architettura Esagonale).
 *
 * PERCHE' QUESTA INTERFACCIA E' NEL CORE:
 *
 * 1. INVERSIONE DELLE DIPENDENZE (DIP):
 *    - Il core (ScenarioManager) definisce COSA vuole fare (questo contratto)
 *    - L'infrastructure decide COME farlo (implementazione)
 *    - Il flusso delle dipendenze va: Infrastructure -> Core (non viceversa)
 *
 * 2. SEPARAZIONE OUTPUT PORT vs DAO:
 *    - IScenarioRepository (qui): Output Port del dominio, linguaggio del dominio
 *    - ScenarioDAO (infrastructure): Pattern DAO puro, gestisce SQL/JDBC
 *    - ScenarioRepositoryAdapter: Adapter che traduce tra i due
 *
 * 3. NESSUNA DIPENDENZA CIRCOLARE:
 *    - ScenarioDAO NON estende questa interfaccia
 *    - L'Adapter in infrastructure implementa IScenarioRepository
 *    - L'Adapter USA internamente ScenarioDAO
 *
 * 4. TESTABILITA':
 *    - Nei test possiamo creare un mock di IScenarioRepository
 *    - Non serve toccare il database reale
 *
 *                    ┌─────────────────────────────────┐
 *                    │            CORE                 │
 *                    │  ScenarioManager                │
 *                    │        │                        │
 *                    │        ▼                        │
 *                    │  IScenarioRepository            │
 *                    │  (Output Port)                  │
 *                    └─────────────────────────────────┘
 *                                 △
 *                                 │ implementa
 *                    ┌────────────┴────────────────────┐
 *                    │       INFRASTRUCTURE            │
 *                    │  ScenarioRepositoryAdapter      │
 *                    │        │                        │
 *                    │        ▼ usa                    │
 *                    │  ScenarioDAO / ScenarioDAOImpl  │
 *                    │  (Pattern DAO puro)             │
 *                    └─────────────────────────────────┘
 */
public interface IScenarioRepository {

    void save(Scenario scenario);

    void update(Scenario scenario);

    boolean delete(String id);

    Optional<Scenario> findById(String id);

    Optional<Scenario> findByNome(String nome);

    List<Scenario> findAll();

    List<Scenario> findByTipo(EnumScenarioType tipo);

    List<Scenario> findByActive(boolean active);

    boolean existsByNome(String nome);

    int count();
}

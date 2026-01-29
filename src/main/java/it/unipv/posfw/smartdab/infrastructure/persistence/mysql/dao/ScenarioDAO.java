package it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao;

import java.util.List;
import java.util.Optional;

import it.unipv.posfw.smartdab.core.domain.enums.EnumScenarioType;
import it.unipv.posfw.smartdab.core.domain.model.scenario.Scenario;

public interface ScenarioDAO {

	public void insertScenario(Scenario scenario);

	public void updateScenario(Scenario scenario);

	public boolean deleteScenario(String id);

	public Optional<Scenario> readScenario(String id);

	public Optional<Scenario> readScenarioByNome(String nome);

	public List<Scenario> readAllScenari();

	public List<Scenario> readScenariByTipo(EnumScenarioType tipo);

	public List<Scenario> readScenariByActive(boolean active);

	public boolean existsByNome(String nome);

	public int count();

}

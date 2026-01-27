package it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao;

import java.sql.Connection;
import java.util.List;

import it.unipv.posfw.smartdab.core.domain.model.scenario.ScenarioStanzaConfig;

public interface ScenarioStanzaConfigDAO {

	public void insertConfig(Connection conn, String scenarioId, ScenarioStanzaConfig config);

	public List<ScenarioStanzaConfig> readConfigsByScenario(Connection conn, String scenarioId);

	public void deleteByScenario(Connection conn, String scenarioId);

}

package it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao;

import java.sql.Connection;
import java.util.List;

import it.unipv.posfw.smartdab.core.domain.model.scenario.StanzaConfig;

public interface ScenarioStanzaConfigDAO {

	public void insertConfig(Connection conn, String scenarioId, StanzaConfig config);

	public List<StanzaConfig> readConfigsByScenario(Connection conn, String scenarioId);

	public void deleteByScenario(Connection conn, String scenarioId);

}

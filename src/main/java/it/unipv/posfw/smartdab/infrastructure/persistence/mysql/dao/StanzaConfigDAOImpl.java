package it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.model.parametro.IParametroValue;
import it.unipv.posfw.smartdab.core.domain.model.parametro.ParametroValue;
import it.unipv.posfw.smartdab.core.domain.model.scenario.StanzaConfig;

public class StanzaConfigDAOImpl implements StanzaConfigDAO {

	private static final String INSERT = "INSERT INTO ScenarioStanzaConfig (id, stanza, scenario, tipo_parametro, tipo_valore) VALUES (?, ?, ?, ?, ?)";
	private static final String SELECT_BY_SCENARIO = "SELECT * FROM ScenarioStanzaConfig WHERE scenario = ?";
	private static final String DELETE_BY_SCENARIO = "DELETE FROM ScenarioStanzaConfig WHERE scenario = ?";

	@Override
	public void insertConfig(Connection conn, String scenarioId, StanzaConfig config) {
		PreparedStatement pstmt = null;

		try {
			pstmt = conn.prepareStatement(INSERT);

			String configId = UUID.randomUUID().toString();
			pstmt.setString(1, configId);
			pstmt.setString(2, config.getStanzaId());
			pstmt.setString(3, scenarioId);
			pstmt.setString(4, config.getTipo_parametro().name());
			pstmt.setString(5, serializeParametroValue(config.getParametro()));

			pstmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null) pstmt.close();
				// Non chiudere conn: e' passata dall'esterno
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public List<StanzaConfig> readConfigsByScenario(Connection conn, String scenarioId) {
		List<StanzaConfig> configs = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			pstmt = conn.prepareStatement(SELECT_BY_SCENARIO);
			pstmt.setString(1, scenarioId);
			rs = pstmt.executeQuery();

			while (rs.next()) {
				String stanzaId = rs.getString("stanza");
				String tipoParametroStr = rs.getString("tipo_parametro");
				String tipoValoreStr = rs.getString("tipo_valore");

				DispositivoParameter tipoParametro = DispositivoParameter.valueOf(tipoParametroStr);
				IParametroValue valore = deserializeParametroValue(tipoValoreStr, tipoParametro);

				StanzaConfig config = new StanzaConfig(stanzaId, valore, tipoParametro);
				configs.add(config);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) rs.close();
				if (pstmt != null) pstmt.close();
				// Non chiudere conn: e' passata dall'esterno
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return configs;
	}

	@Override
	public void deleteByScenario(Connection conn, String scenarioId) {
		PreparedStatement pstmt = null;

		try {
			pstmt = conn.prepareStatement(DELETE_BY_SCENARIO);
			pstmt.setString(1, scenarioId);
			pstmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null) pstmt.close();
				// Non chiudere conn: e' passata dall'esterno
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	private String serializeParametroValue(IParametroValue value) {
		if (value instanceof ParametroValue) {
			ParametroValue pv = (ParametroValue) value;
			return pv.getRawValue();
		}
		return value.getDisplayString();
	}

	private IParametroValue deserializeParametroValue(String serialized, DispositivoParameter tipoParametro) {
		if (serialized == null || serialized.isEmpty()) {
			return null;
		}
		return new ParametroValue(serialized, tipoParametro);
	}

}

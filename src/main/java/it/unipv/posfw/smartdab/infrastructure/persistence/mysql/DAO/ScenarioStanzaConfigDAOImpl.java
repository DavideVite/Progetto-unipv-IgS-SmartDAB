package it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.model.parametro.BooleanValue;
import it.unipv.posfw.smartdab.core.domain.model.parametro.IParametroValue;
import it.unipv.posfw.smartdab.core.domain.model.parametro.NumericValue;
import it.unipv.posfw.smartdab.core.domain.model.scenario.ScenarioStanzaConfig;

public class ScenarioStanzaConfigDAOImpl implements ScenarioStanzaConfigDAO {

	private static final String INSERT = "INSERT INTO ScenarioStanzaConfig (id, stanza, scenario, tipo_parametro, tipo_valore) VALUES (?, ?, ?, ?, ?)";
	private static final String SELECT_BY_SCENARIO = "SELECT * FROM ScenarioStanzaConfig WHERE scenario = ?";
	private static final String DELETE_BY_SCENARIO = "DELETE FROM ScenarioStanzaConfig WHERE scenario = ?";

	@Override
	public void insertConfig(Connection conn, String scenarioId, ScenarioStanzaConfig config) {
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
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public List<ScenarioStanzaConfig> readConfigsByScenario(Connection conn, String scenarioId) {
		List<ScenarioStanzaConfig> configs = new ArrayList<>();
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
				IParametroValue valore = deserializeParametroValue(tipoValoreStr);

				ScenarioStanzaConfig config = new ScenarioStanzaConfig(stanzaId, valore, tipoParametro);
				configs.add(config);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) rs.close();
				if (pstmt != null) pstmt.close();
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
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Serializza un IParametroValue in una stringa per il database.
	 * Formato: TIPO:valore (es. "NUMERIC:22.5" o "BOOLEAN:true")
	 */
	private String serializeParametroValue(IParametroValue value) {
		if (value instanceof NumericValue) {
			NumericValue nv = (NumericValue) value;
			return "NUMERIC:" + nv.getValue();
		} else if (value instanceof BooleanValue) {
			BooleanValue bv = (BooleanValue) value;
			return "BOOLEAN:" + bv.getValue();
		} else {
			// Fallback: usa getDisplayString()
			return "STRING:" + value.getDisplayString();
		}
	}

	/**
	 * Deserializza una stringa dal database in un IParametroValue.
	 */
	private IParametroValue deserializeParametroValue(String serialized) {
		if (serialized == null || serialized.isEmpty()) {
			return null;
		}

		String[] parts = serialized.split(":", 2);
		if (parts.length < 2) {
			return null;
		}

		String tipo = parts[0];
		String valore = parts[1];

		switch (tipo) {
			case "NUMERIC":
				Double numValue = Double.parseDouble(valore);
				return new NumericValue(numValue, null, null, null);
			case "BOOLEAN":
				boolean boolValue = Boolean.parseBoolean(valore);
				return new BooleanValue(boolValue, "ON", "OFF");
			case "STRING":
			default:
				// Crea un NumericValue di fallback
				try {
					Double fallbackValue = Double.parseDouble(valore);
					return new NumericValue(fallbackValue, null, null, null);
				} catch (NumberFormatException e) {
					// Se non è un numero, crea un BooleanValue
					return new BooleanValue(true, valore, "OFF");
				}
		}
	}

}

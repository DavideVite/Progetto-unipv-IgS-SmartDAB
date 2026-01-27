package it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import it.unipv.posfw.smartdab.core.domain.enums.EnumScenarioType;
import it.unipv.posfw.smartdab.core.domain.model.scenario.Scenario;
import it.unipv.posfw.smartdab.core.domain.model.scenario.ScenarioStanzaConfig;
import it.unipv.posfw.smartdab.infrastructure.persistence.mysql.DatabaseConnection;

public class ScenarioDAOImpl implements ScenarioDAO {

	private ScenarioStanzaConfigDAOImpl configDAO;

	private static final String INSERT = "INSERT INTO Scenario (id, nome, tipo, created_at, updated_at, attivo) VALUES (?, ?, ?, ?, ?, ?)";
	private static final String UPDATE = "UPDATE Scenario SET tipo = ?, attivo = ?, updated_at = ? WHERE id = ?";
	private static final String DELETE = "DELETE FROM Scenario WHERE id = ?";
	private static final String SELECT_BY_ID = "SELECT * FROM Scenario WHERE id = ?";
	private static final String SELECT_BY_NAME = "SELECT * FROM Scenario WHERE nome = ?";
	private static final String SELECT_ALL = "SELECT * FROM Scenario ORDER BY created_at DESC";
	private static final String SELECT_BY_TYPE = "SELECT * FROM Scenario WHERE tipo = ? ORDER BY created_at DESC";
	private static final String SELECT_BY_ACTIVE = "SELECT * FROM Scenario WHERE attivo = ? ORDER BY created_at DESC";
	private static final String EXISTS_BY_NAME = "SELECT COUNT(*) FROM Scenario WHERE nome = ?";
	private static final String COUNT = "SELECT COUNT(*) FROM Scenario";

	public ScenarioDAOImpl() {
		this.configDAO = new ScenarioStanzaConfigDAOImpl();
	}

	@Override
	public void insertScenario(Scenario scenario) {
		if (existsByNome(scenario.getNome())) {
			System.out.println("Scenario già esistente: " + scenario.getNome());
			return;
		}

		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			conn = DatabaseConnection.getConnection();

			if (conn != null) {
				pstmt = conn.prepareStatement(INSERT);

				// Genera un ID se non presente
				String id = scenario.getId();
				if (id == null || id.isEmpty()) {
					id = UUID.randomUUID().toString();
					scenario.setId(id);
				}

				pstmt.setString(1, id);
				pstmt.setString(2, scenario.getNome());
				pstmt.setString(3, scenario.getTipo_scenario().name());
				pstmt.setTimestamp(4, Timestamp.valueOf(scenario.getData_creazione()));
				pstmt.setTimestamp(5, Timestamp.valueOf(scenario.getData_ultima_modifica()));
				pstmt.setBoolean(6, scenario.isActive());

				pstmt.executeUpdate();

				// Salva le configurazioni associate
				for (ScenarioStanzaConfig config : scenario.getConfigurazioni()) {
					configDAO.insertConfig(conn, id, config);
				}

				System.out.println("Scenario salvato con successo: " + scenario.getNome());
			}

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
	public void updateScenario(Scenario scenario) {
		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			conn = DatabaseConnection.getConnection();

			if (conn != null) {
				pstmt = conn.prepareStatement(UPDATE);
				pstmt.setString(1, scenario.getTipo_scenario().name());
				pstmt.setBoolean(2, scenario.isActive());
				pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
				pstmt.setString(4, scenario.getId());

				pstmt.executeUpdate();

				// Aggiorna le configurazioni: cancella le vecchie e inserisce le nuove
				configDAO.deleteByScenario(conn, scenario.getId());
				for (ScenarioStanzaConfig config : scenario.getConfigurazioni()) {
					configDAO.insertConfig(conn, scenario.getId(), config);
				}

				System.out.println("Scenario aggiornato con successo: " + scenario.getNome());
			}

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
	public boolean deleteScenario(String id) {
		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			conn = DatabaseConnection.getConnection();

			if (conn != null) {
				// Prima elimina le configurazioni associate
				configDAO.deleteByScenario(conn, id);

				// Poi elimina lo scenario
				pstmt = conn.prepareStatement(DELETE);
				pstmt.setString(1, id);
				int rowsAffected = pstmt.executeUpdate();
				return rowsAffected > 0;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (pstmt != null) pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public Optional<Scenario> readScenario(String id) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			conn = DatabaseConnection.getConnection();

			if (conn != null) {
				pstmt = conn.prepareStatement(SELECT_BY_ID);
				pstmt.setString(1, id);
				rs = pstmt.executeQuery();

				if (rs.next()) {
					Scenario scenario = creaScenarioDaResultSet(rs);
					// Carica le configurazioni
					List<ScenarioStanzaConfig> configs = configDAO.readConfigsByScenario(conn, id);
					scenario.setConfigurazioni(configs);
					return Optional.of(scenario);
				}
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
		return Optional.empty();
	}

	@Override
	public Optional<Scenario> readScenarioByNome(String nome) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			conn = DatabaseConnection.getConnection();

			if (conn != null) {
				pstmt = conn.prepareStatement(SELECT_BY_NAME);
				pstmt.setString(1, nome);
				rs = pstmt.executeQuery();

				if (rs.next()) {
					Scenario scenario = creaScenarioDaResultSet(rs);
					List<ScenarioStanzaConfig> configs = configDAO.readConfigsByScenario(conn, scenario.getId());
					scenario.setConfigurazioni(configs);
					return Optional.of(scenario);
				}
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
		return Optional.empty();
	}

	@Override
	public List<Scenario> readAllScenari() {
		List<Scenario> scenari = new ArrayList<>();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = DatabaseConnection.getConnection();

			if (conn != null) {
				stmt = conn.createStatement();
				rs = stmt.executeQuery(SELECT_ALL);

				while (rs.next()) {
					Scenario scenario = creaScenarioDaResultSet(rs);
					List<ScenarioStanzaConfig> configs = configDAO.readConfigsByScenario(conn, scenario.getId());
					scenario.setConfigurazioni(configs);
					scenari.add(scenario);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) rs.close();
				if (stmt != null) stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return scenari;
	}

	@Override
	public List<Scenario> readScenariByTipo(EnumScenarioType tipo) {
		List<Scenario> scenari = new ArrayList<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			conn = DatabaseConnection.getConnection();

			if (conn != null) {
				pstmt = conn.prepareStatement(SELECT_BY_TYPE);
				pstmt.setString(1, tipo.name());
				rs = pstmt.executeQuery();

				while (rs.next()) {
					Scenario scenario = creaScenarioDaResultSet(rs);
					List<ScenarioStanzaConfig> configs = configDAO.readConfigsByScenario(conn, scenario.getId());
					scenario.setConfigurazioni(configs);
					scenari.add(scenario);
				}
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
		return scenari;
	}

	@Override
	public List<Scenario> readScenariByActive(boolean active) {
		List<Scenario> scenari = new ArrayList<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			conn = DatabaseConnection.getConnection();

			if (conn != null) {
				pstmt = conn.prepareStatement(SELECT_BY_ACTIVE);
				pstmt.setBoolean(1, active);
				rs = pstmt.executeQuery();

				while (rs.next()) {
					Scenario scenario = creaScenarioDaResultSet(rs);
					List<ScenarioStanzaConfig> configs = configDAO.readConfigsByScenario(conn, scenario.getId());
					scenario.setConfigurazioni(configs);
					scenari.add(scenario);
				}
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
		return scenari;
	}

	@Override
	public boolean existsByNome(String nome) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			conn = DatabaseConnection.getConnection();

			if (conn != null) {
				pstmt = conn.prepareStatement(EXISTS_BY_NAME);
				pstmt.setString(1, nome);
				rs = pstmt.executeQuery();

				if (rs.next()) {
					return rs.getInt(1) > 0;
				}
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
		return false;
	}

	@Override
	public int count() {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;

		try {
			conn = DatabaseConnection.getConnection();

			if (conn != null) {
				stmt = conn.createStatement();
				rs = stmt.executeQuery(COUNT);

				if (rs.next()) {
					return rs.getInt(1);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) rs.close();
				if (stmt != null) stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}

	/**
	 * Metodo privato di utilità per creare uno Scenario da un ResultSet
	 */
	private Scenario creaScenarioDaResultSet(ResultSet rs) throws SQLException {
		String id = rs.getString("id");
		String nome = rs.getString("nome");
		EnumScenarioType tipo = EnumScenarioType.valueOf(rs.getString("tipo"));
		boolean attivo = rs.getBoolean("attivo");
		LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
		LocalDateTime updatedAt = rs.getTimestamp("updated_at").toLocalDateTime();

		return new Scenario(id, nome, tipo, attivo, createdAt, updatedAt);
	}

}

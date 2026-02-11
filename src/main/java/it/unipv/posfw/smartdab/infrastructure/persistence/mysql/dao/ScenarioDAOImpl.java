package it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import it.unipv.posfw.smartdab.core.domain.enums.EnumScenarioType;
import it.unipv.posfw.smartdab.core.domain.exception.PersistenzaException;
import it.unipv.posfw.smartdab.core.domain.model.scenario.Scenario;
import it.unipv.posfw.smartdab.core.domain.model.scenario.StanzaConfig;
import it.unipv.posfw.smartdab.infrastructure.persistence.mysql.DatabaseConnection;

public class ScenarioDAOImpl implements ScenarioDAO {

	private StanzaConfigDAOImpl configDAO;

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
		this.configDAO = new StanzaConfigDAOImpl();
	}

	@Override
	public void insertScenario(Scenario scenario) {
		// Il controllo esisteScenario e' gia' fatto in ScenarioManager.creaScenario()
		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			conn = DatabaseConnection.getConnection();

			if (conn != null) {
				// TRANSACTION: disabilita auto-commit
				conn.setAutoCommit(false);

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
				for (StanzaConfig config : scenario ) {
					configDAO.insertConfig(conn, id, config);
				}

				// TRANSACTION: commit se tutto ok
				conn.commit();
				System.out.println("Scenario salvato con successo: " + scenario.getNome());
			}

		} catch (SQLException e) {
			// TRANSACTION: rollback in caso di errore
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException rollbackEx) {
					System.err.println("Errore durante rollback: " + rollbackEx.getMessage());
				}
			}
			throw new PersistenzaException("Errore durante l'inserimento dello scenario: " + scenario.getNome(), e);
		} finally {
			// Ripristina auto-commit prima di chiudere
			if (conn != null) {
				try {
					conn.setAutoCommit(true);
				} catch (SQLException e) {
					System.err.println("Errore ripristino auto-commit: " + e.getMessage());
				}
			}
			chiudiRisorse(null, pstmt, conn);
		}
	}

	@Override
	public void updateScenario(Scenario scenario) {
		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			conn = DatabaseConnection.getConnection();

			if (conn != null) {
				// TRANSACTION: disabilita auto-commit
				conn.setAutoCommit(false);

				pstmt = conn.prepareStatement(UPDATE);
				pstmt.setString(1, scenario.getTipo_scenario().name());
				pstmt.setBoolean(2, scenario.isActive());
				pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
				pstmt.setString(4, scenario.getId());

				pstmt.executeUpdate();

				// Aggiorna le configurazioni: cancella le vecchie e inserisce le nuove
				configDAO.deleteByScenario(conn, scenario.getId());
				for (StanzaConfig config : scenario) {
					configDAO.insertConfig(conn, scenario.getId(), config);
				}

				// TRANSACTION: commit se tutto ok
				conn.commit();
				System.out.println("Scenario aggiornato con successo: " + scenario.getNome());
			}

		} catch (SQLException e) {
			// TRANSACTION: rollback in caso di errore
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException rollbackEx) {
					System.err.println("Errore durante rollback: " + rollbackEx.getMessage());
				}
			}
			throw new PersistenzaException("Errore durante l'aggiornamento dello scenario: " + scenario.getNome(), e);
		} finally {
			// Ripristina auto-commit prima di chiudere
			if (conn != null) {
				try {
					conn.setAutoCommit(true);
				} catch (SQLException e) {
					System.err.println("Errore ripristino auto-commit: " + e.getMessage());
				}
			}
			chiudiRisorse(null, pstmt, conn);
		}
	}

	@Override
	public boolean deleteScenario(String id) {
		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			conn = DatabaseConnection.getConnection();

			if (conn != null) {
				// TRANSACTION: disabilita auto-commit
				conn.setAutoCommit(false);

				// Prima elimina le configurazioni associate
				configDAO.deleteByScenario(conn, id);

				// Poi elimina lo scenario
				pstmt = conn.prepareStatement(DELETE);
				pstmt.setString(1, id);
				int rowsAffected = pstmt.executeUpdate();

				// TRANSACTION: commit se tutto ok
				conn.commit();
				return rowsAffected > 0;
			}

		} catch (SQLException e) {
			// TRANSACTION: rollback in caso di errore
			if (conn != null) {
				try {
					conn.rollback();
				} catch (SQLException rollbackEx) {
					System.err.println("Errore durante rollback: " + rollbackEx.getMessage());
				}
			}
			throw new PersistenzaException("Errore durante l'eliminazione dello scenario con ID: " + id, e);
		} finally {
			// Ripristina auto-commit prima di chiudere
			if (conn != null) {
				try {
					conn.setAutoCommit(true);
				} catch (SQLException e) {
					System.err.println("Errore ripristino auto-commit: " + e.getMessage());
				}
			}
			chiudiRisorse(null, pstmt, conn);
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
					List<StanzaConfig> configs = configDAO.readConfigsByScenario(conn, id);
					scenario.setConfigurazioni(new LinkedHashSet<>(configs));
					return Optional.of(scenario);
				}
			}

		} catch (SQLException e) {
			throw new PersistenzaException("Errore durante la lettura dello scenario con ID: " + id, e);
		} finally {
			chiudiRisorse(rs, pstmt, conn);
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
					List<StanzaConfig> configs = configDAO.readConfigsByScenario(conn, scenario.getId());
					scenario.setConfigurazioni(new LinkedHashSet<>(configs));
					return Optional.of(scenario);
				}
			}

		} catch (SQLException e) {
			throw new PersistenzaException("Errore durante la lettura dello scenario con nome: " + nome, e);
		} finally {
			chiudiRisorse(rs, pstmt, conn);
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
					List<StanzaConfig> configs = configDAO.readConfigsByScenario(conn, scenario.getId());
					scenario.setConfigurazioni(new LinkedHashSet<>(configs));
					scenari.add(scenario);
				}
			}

		} catch (SQLException e) {
			throw new PersistenzaException("Errore durante la lettura di tutti gli scenari", e);
		} finally {
			chiudiRisorse(rs, stmt, conn);
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
					List<StanzaConfig> configs = configDAO.readConfigsByScenario(conn, scenario.getId());
					scenario.setConfigurazioni(new LinkedHashSet<>(configs));
					scenari.add(scenario);
				}
			}

		} catch (SQLException e) {
			throw new PersistenzaException("Errore durante la lettura degli scenari di tipo: " + tipo.name(), e);
		} finally {
			chiudiRisorse(rs, pstmt, conn);
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
					List<StanzaConfig> configs = configDAO.readConfigsByScenario(conn, scenario.getId());
					scenario.setConfigurazioni(new LinkedHashSet<>(configs));
					scenari.add(scenario);
				}
			}

		} catch (SQLException e) {
			throw new PersistenzaException("Errore durante la lettura degli scenari attivi: " + active, e);
		} finally {
			chiudiRisorse(rs, pstmt, conn);
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
			throw new PersistenzaException("Errore durante la verifica esistenza scenario: " + nome, e);
		} finally {
			chiudiRisorse(rs, pstmt, conn);
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
			throw new PersistenzaException("Errore durante il conteggio degli scenari", e);
		} finally {
			chiudiRisorse(rs, stmt, conn);
		}
		return 0;
	}

	/**
	 * Metodo privato di utilità per creare uno Scenario da un ResultSet
	 */
	private Scenario creaScenarioDaResultSet(ResultSet rs) throws SQLException {
		String id = rs.getString("id");
		String nome = rs.getString("nome");
		EnumScenarioType tipo = parseScenarioType(rs.getString("tipo"));
		boolean attivo = rs.getBoolean("attivo");
		LocalDateTime createdAt = rs.getTimestamp("created_at").toLocalDateTime();
		LocalDateTime updatedAt = rs.getTimestamp("updated_at").toLocalDateTime();

		return new Scenario(id, nome, tipo, attivo, createdAt, updatedAt);
	}

	/**
	 * Converte una stringa nel corrispondente EnumScenarioType.
	 * Se il valore non è valido, ritorna PERSONALIZZATO come fallback.
	 */
	private EnumScenarioType parseScenarioType(String tipoStr) {
		try {
			return EnumScenarioType.valueOf(tipoStr);
		} catch (IllegalArgumentException e) {
			System.err.println("Tipo scenario non valido nel DB: " + tipoStr + ". Default a PERSONALIZZATO.");
			return EnumScenarioType.PERSONALIZZATO;
		}
	}

	/**
	 * Metodo helper per chiudere le risorse JDBC in modo sicuro.
	 * Evita duplicazione di codice nei blocchi finally.
	 */
	private void chiudiRisorse(ResultSet rs, Statement stmt, Connection conn) {
		try {
			if (rs != null) rs.close();
		} catch (SQLException e) {
			System.err.println("Errore chiusura ResultSet: " + e.getMessage());
		}
		try {
			if (stmt != null) stmt.close();
		} catch (SQLException e) {
			System.err.println("Errore chiusura Statement: " + e.getMessage());
		}
		try {
			if (conn != null) conn.close();
		} catch (SQLException e) {
			System.err.println("Errore chiusura Connection: " + e.getMessage());
		}
	}
}

package it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Timestamp;
import java.util.ArrayList;

import it.unipv.posfw.smartdab.core.beans.DispositivoPOJO;
import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.enums.DispositivoStates;
import it.unipv.posfw.smartdab.infrastructure.persistence.mysql.DatabaseConnection;

public class DispositivoDAOImpl implements DispositivoDAO{
	
	private CommunicationDAOImpl communicationDAO;
	private Connection connection;
	public final int MAX_N = 100;
	
	public DispositivoDAOImpl() {
		communicationDAO = new CommunicationDAOImpl();
	}
	
	public ArrayList<DispositivoPOJO> selectN(int n) {
		ArrayList<DispositivoPOJO> result = new ArrayList<>();
		
		if(n > MAX_N) {
			System.out.println("Il numero di dispositivi da selezionare è troppo elevato");
			return result;
		}

		
		try {
			connection = DatabaseConnection.getConnection();
			
			PreparedStatement s;
			ResultSet r;
			
			String query = "SELECT * from Dispositivo LIMIT " + n;
			s = connection.prepareStatement(query);
			r = s.executeQuery(query);
			
			while(r.next()) {
				DispositivoPOJO d = new DispositivoPOJO(r.getString(1), 
														r.getString(8),
														DispositivoParameter.valueOf(r.getString(2)),
														r.getString(3),
														DispositivoStates.valueOf(r.getString(4)), 
														r.getBoolean(5),
														r.getTimestamp(6).toLocalDateTime()
														);
				result.add(d);
			} 
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return result;
	}

	@Override
	public boolean insertDispositivo(DispositivoPOJO d) {
		if (existsById(d.getId())) {
			System.out.println("Dispositivo " + d.getId() + " è già presente nel database");
			return false;
		}

		PreparedStatement s = null;

		try {
			Connection connection = DatabaseConnection.getConnection();

			if (connection != null) {
				s = connection.prepareStatement("INSERT INTO Dispositivo "
						+ "(id, parametro, tipo, stato, attivo, created_at, model, stanza) "
						+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)");

				s.setString(1, d.getId());
				s.setString(2, d.getParametro().toString());
				s.setString(3, d.getTipo());
				s.setString(4, d.getStato().toString());
				s.setBoolean(5, d.isAttivo());
				s.setTimestamp(6, Timestamp.valueOf(d.getCreated_at()));
				s.setString(7, "");
				s.setString(8, d.getStanza());

				s.executeUpdate();
				
				System.out.println("Dispositivo " + d.getId() + " inserito correttamente");
			}
			
			else return false;

		} catch (SQLException e) {
			System.out.println("Errore nell'inserimento del dispositivo nel DB");	
		} finally {
			try {
				if (s != null) s.close();
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean updateDispositivo(DispositivoPOJO d) {
		PreparedStatement s = null;
		try {
			Connection connection = DatabaseConnection.getConnection();

			if (connection != null) {
				s = connection.prepareStatement("UPDATE Dispositivo SET attivo = ? WHERE id = ?");
				s.setBoolean(1, d.isAttivo());
				s.setString(2, d.getId());

				s.executeUpdate();

				System.out.println("Dispositivo " + d.getId() + " aggiornato correttamente");
			}
			
			else return false;

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (s != null) s.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return true;
		
	}

	@Override
	public boolean disableDispositivo(String id) {
		PreparedStatement s = null;
		try {
			Connection connection = DatabaseConnection.getConnection();

			if (connection != null) {
				s = connection.prepareStatement("UPDATE Dispositivo SET stato = ? WHERE id = ?");
				s.setString(1, DispositivoStates.DISABLED.toString());
				s.setString(2, id);

				s.executeUpdate();

				System.out.println("Dispositivo " + id + " disabilitato correttamente");
			}
			
			else return false;

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (s != null) s.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return true;
		
	}
	
	@Override
	public boolean deleteDispositivo(String id) {
		Connection connection = null;
		PreparedStatement s = null;
		
		if(!existsById(id)) {
			System.out.println("Dispositivo non presente nel db");
			return false;
		}
		
		try {
			connection = DatabaseConnection.getConnection();

			if (connection != null) {
				
				communicationDAO.deleteCommunicationByDispositivoId(id);
				
				s = connection.prepareStatement("DELETE FROM Dispositivo WHERE id = ?");
				s.setString(1, id);
				int rowsAffected = s.executeUpdate();
				return rowsAffected > 0;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (s != null) s.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public boolean existsById(String id) {
		Connection connection = null;
		PreparedStatement s = null;
		ResultSet r = null;

		try {
			connection = DatabaseConnection.getConnection();

			if (connection != null) {
				s = connection.prepareStatement("SELECT COUNT(*) FROM Dispositivo WHERE id = ?");
				s.setString(1, id);
				r = s.executeQuery();

				if (r.next()) {
					return r.getInt(1) > 0;
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (r != null) r.close();
				if (s != null) s.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
}

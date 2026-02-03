package it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import it.unipv.posfw.smartdab.core.beans.CommunicationPOJO;
import it.unipv.posfw.smartdab.infrastructure.persistence.mysql.DatabaseConnection;

public class CommunicationDAOImpl implements CommunicationDAO {
	private Connection connection;
	public final int MAX_N = 100;
	
	public CommunicationDAOImpl() {

	}
	
	public ArrayList<CommunicationPOJO> selectN(int n) {
		ArrayList<CommunicationPOJO> result = new ArrayList<>();
		
		if(n > MAX_N) {
			System.out.println("Il numero di comunicazioni da selezionare è troppo elevato");
			return result;
		}
		

		
		try {
			connection = DatabaseConnection.getConnection();
			
			PreparedStatement s;
			ResultSet r;
			
			String query = "SELECT * from Dispositivi LIMIT " + n;
			s = connection.prepareStatement(query);
			r = s.executeQuery(query);
			
			while(r.next()) {
				CommunicationPOJO c = new CommunicationPOJO(
														r.getInt(1),
														r.getString(2),
														r.getString(3),
														r.getObject(4),
														r.getString(5),
														r.getTimestamp(6).toLocalDateTime()
														);
				result.add(c);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch(Exception e) {
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
	public boolean insertCommunication(CommunicationPOJO c) {
		if (existsById(c.getId())) {
			System.out.println("Comunicazione " + c.getId() + " è già presente nel database");
			return false;
		}

		PreparedStatement s = null;

		try {
			Connection connection = DatabaseConnection.getConnection();

			if (connection != null) {
				s = connection.prepareStatement("INSERT INTO Communication "
						+ "(id, esito, tipo, value, dispositivo, last_update) "
						+ "VALUES (?, ?, ?, ?, ?, ?)");

				s.setInt(1, c.getId());
				s.setString(2, c.getEsito());
				s.setString(3, c.getTipo());
				
				if(c.getValue() != null) s.setString(4, c.getValue().toString());
				else s.setString(4, "null");
				
				s.setString(5, c.getDispositivo());
				s.setTimestamp(6, Timestamp.valueOf(c.getLast_update()));

				s.executeUpdate();
				
				System.out.println("Comunicazione " + c.getId() + " inserito correttamente");
			}
			
			else return false;

		} catch (SQLException e) {
			e.printStackTrace();
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
	public boolean updateCommunication(CommunicationPOJO c) {
		PreparedStatement s = null;
		try {
			Connection connection = DatabaseConnection.getConnection();

			if (connection != null) {
				s = connection.prepareStatement("UPDATE Communication SET esito = ?, last_update = ? WHERE id = ?");
				s.setString(1, c.getEsito());
				s.setTimestamp(2, Timestamp.valueOf(c.getLast_update()));

				s.executeUpdate();

				System.out.println("Communication " + c.getId() + " aggiornata correttamente");
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
	public boolean existsById(int id) {
		Connection connection = null;
		PreparedStatement s = null;
		ResultSet r = null;

		try {
			connection = DatabaseConnection.getConnection();

			if (connection != null) {
				s = connection.prepareStatement("SELECT COUNT(*) FROM Communication WHERE id = ?");
				s.setInt(1, id);
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
	
	@Override
	public boolean deleteCommunicationByDispositivoId(String id) {
		Connection connection = null;
		PreparedStatement s = null;

		try {
			connection = DatabaseConnection.getConnection();

			if (connection != null) {

				s = connection.prepareStatement("DELETE FROM Communication WHERE dispositivo = ?");
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
}

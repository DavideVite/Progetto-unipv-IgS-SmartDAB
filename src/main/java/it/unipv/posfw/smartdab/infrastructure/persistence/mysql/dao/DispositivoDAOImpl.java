package it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import it.unipv.posfw.smartdab.core.beans.DispositivoPOJO;
import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.enums.DispositivoStates;
import it.unipv.posfw.smartdab.infrastructure.persistence.mysql.DatabaseConnection;

public class DispositivoDAOImpl implements DispositivoDAO{
	private Connection connection;
	public final int MAX_N = 100;
	
	public DispositivoDAOImpl() {

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
			
			String query = "SELECT * from Dispositivi LIMIT " + n;
			s = connection.prepareStatement(query);
			r = s.executeQuery(query);
			
			while(r.next()) {
				DispositivoPOJO d = new DispositivoPOJO(r.getString(1), 
														r.getString(2), 
														DispositivoParameter.valueOf(r.getString(3)), 
														r.getString(4), 
														DispositivoStates.valueOf(r.getString(5)), 
														r.getBoolean(6),
														r.getTimestamp(7).toLocalDateTime(), 
														r.getString(8)
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
}

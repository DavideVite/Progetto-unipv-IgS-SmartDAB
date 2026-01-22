package it.unipv.posfw.smartdab.infrastructure.persistence.mysql.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import it.unipv.posfw.smartdab.core.beans.DispositivoPOJO;
import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.enums.DispositivoStates;

public class DispositivoDAOImpl implements DispositivoDAO{
	private String schema;
	private Connection connection;
	public final int MAX_N = 100;
	
	public DispositivoDAOImpl() {
		this.schema = "";
	}
	
	public ArrayList<DispositivoPOJO> selectN(int n) {
		ArrayList<DispositivoPOJO> result = new ArrayList<>();
		
		if(n > MAX_N) {
			System.out.println("Il numero di dispositivi da selezionare è troppo elevato");
			return result;
		}
		
		// connection = DBConnection.startConnection(connection, schema);
		PreparedStatement s;
		ResultSet r;
		
		try {
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
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		// DBConnection.closeConnection(conn);
		return result;
	}
}

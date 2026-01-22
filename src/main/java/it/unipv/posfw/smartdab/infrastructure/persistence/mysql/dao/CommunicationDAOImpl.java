package it.unipv.posfw.smartdab.infrastructure.persistence.mysql.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import it.unipv.posfw.smartdab.core.beans.CommunicationPOJO;

public class CommunicationDAOImpl implements CommunicationDAO {
	private String schema;
	private Connection connection;
	public final int MAX_N = 100;
	
	public CommunicationDAOImpl() {
		this.schema = "";
	}
	
	public ArrayList<CommunicationPOJO> selectN(int n) {
		ArrayList<CommunicationPOJO> result = new ArrayList<>();
		
		if(n > MAX_N) {
			System.out.println("Il numero di comunicazioni da selezionare è troppo elevato");
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
				CommunicationPOJO c = new CommunicationPOJO(
														r.getString(1),
														r.getString(2),
														r.getString(3),
														r.getObject(4),
														r.getString(5),
														r.getTimestamp(6).toLocalDateTime()
														);
				result.add(c);
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		// DBConnection.closeConnection(conn);
		return result;
	}
}

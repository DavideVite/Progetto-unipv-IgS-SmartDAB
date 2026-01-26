package it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import it.unipv.posfw.smartdab.core.domain.model.casa.Stanza;
import it.unipv.posfw.smartdab.infrastructure.persistence.mysql.DatabaseConnection;

public class StanzaDAOImpl implements StanzaDAO{

	//compito di DatabaseConnection
	//final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	//final String DB_URL = "jdbc:mysql://hostname:port/dbname", "username", "password";
	
	@Override
	public void insertStanza(Stanza s) {
		Connection conn = null;
		PreparedStatement pstmt = null; 
		
		String sql = "INSERT INTO stanza (id, nome, mq) VALUES (?, ?, ?)";
		
		try {
			conn = DatabaseConnection.getConnection();
			
			if(conn != null) {
			pstmt = conn.prepareStatement(sql);
			
			//per associare i valori ai "?"
			pstmt.setString(1,  s.getId());
			pstmt.setString(2,  s.getNome());
			pstmt.setDouble(3,  s.getMq());
			
			pstmt.executeUpdate();
			System.out.println("Stanza inserita con successo");
		}
			
	} catch (SQLException e) {
		e.printStackTrace();		
	} finally {
		//chiudiamo lo statement
		try {
			if (pstmt != null ) pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
		
	}
	

	@Override
	public Stanza readStanza(String id) {
		Connection conn = null;
		PreparedStatement pstmt = null; 
		ResultSet rs = null;
		Stanza s = null;	
		
		String sql = "SELECT * FROM stanza WHERE id = ?";
		
		try {
			conn = DatabaseConnection.getConnection();
			
			
			if(conn != null) {
			pstmt = conn.prepareStatement(sql);
			
			//passiamo l'id
			pstmt.setString(1,  id);
			
		    rs = pstmt.executeQuery();
		    
		    if(rs.next()) {
			  String idTrovato = rs.getString("id");
			  String nome = rs.getString("nome");
			  double mq = rs.getDouble("mq");
			
			  //creiamo oggetto stanza con dati del DB
			  s = new Stanza(idTrovato, nome, mq);
			  System.out.println("Stanza trovata: " + nome);
		} else {
			System.out.println("Nessuna stanza trovata con ID: " + id);
		  }
		}
	} catch (SQLException e) {
		e.printStackTrace();		
	} finally {
		//chiudiamo lo statement
		try {
			if (rs != null) rs.close();
			if (pstmt != null ) pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
		return s;
	}
	

	@Override
	public void updateStanza(Stanza s) {
		Connection conn = null;
		PreparedStatement pstmt = null; 	
		
		String sql = "UPDATE stanza SET nome=?, mq = ? WHERE id = ?";
		
		try {
			conn = DatabaseConnection.getConnection();		
			
			if(conn != null) {
			pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,  s.getNome());
            pstmt.setDouble(2,  s.getMq());
			pstmt.setString(3,  s.getId());
			pstmt.executeUpdate();
		}
	} catch (SQLException e) {
		e.printStackTrace();		
	} finally {
		//chiudiamo lo statement
		try {
			if (pstmt != null ) pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
		
	}

	@Override
	public void deleteStanza(String id) {
		Connection conn = null;
		PreparedStatement pstmt = null; 	
		
		String sql = "DELETE FROM stanza WHERE id = ?";
		
		try {
			conn = DatabaseConnection.getConnection();
			
			
			if(conn != null) {
			pstmt = conn.prepareStatement(sql);
			
			//passiamo l'id
			pstmt.setString(1,  id);
			
            int stanzaDaEliminare = pstmt.executeUpdate();
            
            if(stanzaDaEliminare > 0) {
            	System.out.println("Stanza con ID " + id + "eliminata con successo dal database.");
            } else {
            	System.out.println("Nessuna stanza trovata con ID: " + id);
            }
		}
	} catch (SQLException e) {
		e.printStackTrace();		
	} finally {
		//chiudiamo lo statement
		try {
			if (pstmt != null ) pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	}

	@Override
	public Set<Stanza> readAllStanze() {
		Connection conn = null;
		PreparedStatement pstmt = null; 
		ResultSet rs = null;
		Set<Stanza> stanza = new HashSet<>();	 
		
		String sql = "SELECT * FROM stanza";
		
		try {
			conn = DatabaseConnection.getConnection();		
			
			if(conn != null) {
			pstmt = conn.prepareStatement(sql);
			
		    rs = pstmt.executeQuery();
		    
		    while(rs.next()) {;
			  String id = rs.getString("id");
			  String nome = rs.getString("nome");
			  double mq = rs.getDouble("mq");
			
			  //creiamo oggetto con dati del DB
			  Stanza s = new Stanza (id, nome, mq);
			  
			  stanza.add(s);
		  }
		}
	} catch (SQLException e) {
		e.printStackTrace();		
	} finally {
		//chiudiamo lo statement
		try {
			if (rs != null) rs.close();
			if (pstmt != null ) pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		
	}
	return stanza;
   }
}

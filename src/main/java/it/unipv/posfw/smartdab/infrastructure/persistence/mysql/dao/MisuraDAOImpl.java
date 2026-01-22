package it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.unipv.posfw.smartdab.core.beans.Misura;
import it.unipv.posfw.smartdab.infrastructure.persistence.mysql.DatabaseConnection;

public class MisuraDAOImpl implements MisuraDAO {

	@Override
	public void insertMisura(Misura m) {
		Connection conn = null;
		PreparedStatement pstmt = null; 
		
		//non metto id perché è auto-increment
		String sql = "INSERT INTO misure_sensori (tipo, unita, valore, idStanza) VALUES (?, ?, ?, ?)";
		
		try {
			conn = DatabaseConnection.getConnection();
			
			if(conn != null) {
			pstmt = conn.prepareStatement(sql);
			
			//per associare i valori ai "?"
			pstmt.setString(1,  m.getTipo());
			pstmt.setString(2,  m.getUnita());
			pstmt.setDouble(3, m.getValore());
			pstmt.setString(4, m.getIdStanza());
			
			pstmt.executeUpdate();
			System.out.println("Misura salvata con successo");
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
	public List<Misura> readMisuraStanza(String idStanza) {
		Connection conn = null;
		PreparedStatement pstmt = null; 
		ResultSet rs = null;
		List<Misura> misure = new ArrayList<>();	 
		
		String sql = "SELECT * FROM misure_sensori WHERE idStanza = ?";
		
		try {
			conn = DatabaseConnection.getConnection();		
			
			if(conn != null) {
			pstmt = conn.prepareStatement(sql);
			
			//passiamo l'id
			pstmt.setString(1, idStanza);
			
		    rs = pstmt.executeQuery();
		    
		    while(rs.next()) {
			  String idStanzaTrovata = rs.getString("idStanza");
			  String id = rs.getString("id");
			  String tipo = rs.getString("tipo");
			  double valore = rs.getDouble("valore");
			  String unita = rs.getString("unita");
			  
			  //recupero il timestamp
			  java.sql.Timestamp data = rs.getTimestamp("timestamp");
			
			  //creiamo oggetto con dati del DB
			  Misura m = new Misura (id, tipo, unita, valore, idStanzaTrovata, data);
			  
			  misure.add(m);
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
		return misure;
	}

	
	@Override
	public Misura readUltimaMisura(String idStanza, String tipo) {
		Connection conn = null;
		PreparedStatement pstmt = null; 
		ResultSet rs = null;
		Misura m = null;	
		
		String sql = "SELECT * FROM misure_sensori WHERE idStanza = ? AND tipo=? ORDER BY timestamp DESC LIMIT 1";
		
		try {
			conn = DatabaseConnection.getConnection();		
			
			if(conn != null) {
			pstmt = conn.prepareStatement(sql);
			
			//passiamo idStanza e tipo
			pstmt.setString(1,  idStanza);
			pstmt.setString(2,  tipo);
			
		    rs = pstmt.executeQuery();
		    
		    if(rs.next()) {
			  String idStanzaTrovata = rs.getString("idStanza");
			  String tipoTrovato = rs.getString("tipo");
			  String id = rs.getString("id");
			  double valore = rs.getDouble("valore");
			  String unita = rs.getString("unita");	
			  
			  java.sql.Timestamp data = rs.getTimestamp("timestamp");
			
			  //creiamo oggetto stanza con dati del DB
			  m = new Misura(id, tipoTrovato, unita, valore, idStanzaTrovata, data);
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
		return m;
	
	}

}

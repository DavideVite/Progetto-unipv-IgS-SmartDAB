package it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.unipv.posfw.smartdab.core.beans.MisuraPOJO;
import it.unipv.posfw.smartdab.infrastructure.persistence.mysql.DatabaseConnection;

public class MisuraDAOImpl implements MisuraDAO {

	@Override
	public void insertMisura(MisuraPOJO m) {
		Connection conn = null;
		PreparedStatement pstmt = null; 
		
		//non metto id perché è auto-increment
		String sql = "INSERT INTO misura (tipo, unita, valore, stanza) VALUES (?, ?, ?, ?)";
		
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
	public List<MisuraPOJO> readMisuraStanza(String stanza) {
		Connection conn = null;
		PreparedStatement pstmt = null; 
		ResultSet rs = null;
		List<MisuraPOJO> misure = new ArrayList<>();	 
		
		String sql = "SELECT * FROM misura WHERE stanza = ?";
		
		try {
			conn = DatabaseConnection.getConnection();		
			
			if(conn != null) {
			pstmt = conn.prepareStatement(sql);
			
			//passiamo l'id
			pstmt.setString(1, stanza);
			
		    rs = pstmt.executeQuery();
		    
		    while(rs.next()) {
			  String idStanzaTrovata = rs.getString("stanza");
			  String id = rs.getString("id");
			  String tipo = rs.getString("tipo");
			  double valore = rs.getDouble("valore");
			  String unita = rs.getString("unita");
			  
			  //recupero il timestamp
			  java.sql.Timestamp data = rs.getTimestamp("timestamp");
			
			  //creiamo oggetto con dati del DB
			  MisuraPOJO m = new MisuraPOJO (id, tipo, unita, valore, idStanzaTrovata, data);
			  
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
	public MisuraPOJO readUltimaMisura(String stanza, String tipo) {
		Connection conn = null;
		PreparedStatement pstmt = null; 
		ResultSet rs = null;
		MisuraPOJO m = null;	
		
		String sql = "SELECT * FROM misura WHERE stanza = ? AND tipo=? ORDER BY timestamp DESC LIMIT 1";
		
		try {
			conn = DatabaseConnection.getConnection();		
			
			if(conn != null) {
			pstmt = conn.prepareStatement(sql);
			
			//passiamo idStanza e tipo
			pstmt.setString(1,  stanza);
			pstmt.setString(2,  tipo);
			
		    rs = pstmt.executeQuery();
		    
		    if(rs.next()) {
			  String idStanzaTrovata = rs.getString("stanza");
			  String tipoTrovato = rs.getString("tipo");
			  String id = rs.getString("id");
			  double valore = rs.getDouble("valore");
			  String unita = rs.getString("unita");	
			  
			  java.sql.Timestamp data = rs.getTimestamp("timestamp");
			
			  //creiamo oggetto stanza con dati del DB
			  m = new MisuraPOJO(id, tipoTrovato, unita, valore, idStanzaTrovata, data);
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

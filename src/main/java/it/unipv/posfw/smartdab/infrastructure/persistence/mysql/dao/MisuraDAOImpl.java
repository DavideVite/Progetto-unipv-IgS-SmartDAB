package it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.unipv.posfw.smartdab.core.beans.MisuraPOJO;
import it.unipv.posfw.smartdab.infrastructure.persistence.mysql.DatabaseConnection;

public class MisuraDAOImpl implements MisuraDAO {

	@Override
	public void insertMisura(MisuraPOJO m) {
		Connection conn = null;
		PreparedStatement pstmt = null; 
		
		//non metto id perché è auto-increment
		String sql = "INSERT INTO misura (tipo, unita, valore, stanza, timestamp) VALUES (?, ?, ?, ?, ?)";
		
		try {
			conn = DatabaseConnection.getConnection();
			
			if(conn != null) {
			pstmt = conn.prepareStatement(sql);
			
			//per associare i valori ai "?"
			pstmt.setString(1,  m.getTipo());
			pstmt.setString(2,  m.getUnita());
			pstmt.setDouble(3, m.getValore());
			pstmt.setString(4, m.getIdStanza());
			pstmt.setTimestamp(5, java.sql.Timestamp.valueOf(m.getTimestamp()));
			
			pstmt.executeUpdate();
			System.out.println("Misura salvata con successo");
		}
			
	} catch (SQLException e) {
		e.printStackTrace();		
	} finally {
		//chiudiamo lo statement
		try {
			if (pstmt != null ) pstmt.close();
			if (conn != null) conn.close();
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
			
			//passiamo l'id della stanza
			pstmt.setString(1, stanza);
			
		    rs = pstmt.executeQuery();
		    
		    while(rs.next()) {
			  String idStanzaTrovata = rs.getString("stanza");
			  int id = rs.getInt("id");
			  String tipo = rs.getString("tipo");
			  double valore = rs.getDouble("valore");
			  String unita = rs.getString("unita");
			  
			  //recupero il timestamp
			  java.sql.Timestamp sqlTimestamp = rs.getTimestamp("timestamp");
			  LocalDateTime data = null;
			  if (sqlTimestamp != null) {
				  data = sqlTimestamp.toLocalDateTime();
			  }
			
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
			if (conn != null) conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
		return misure;
	}

	
	@Override
	public List<MisuraPOJO> readUltimeMisurePerStanza() {
		Connection conn = null;
		PreparedStatement pstmt = null; 
		ResultSet rs = null;
		List<MisuraPOJO> ultimeMisure = new ArrayList<>();	
		Set<String> misureSalvate = new HashSet<>();
		
		String sql = "SELECT * FROM misura ORDER BY stanza, tipo, timestamp DESC";
		
		try {
			conn = DatabaseConnection.getConnection();		
			
			if(conn != null) {
			pstmt = conn.prepareStatement(sql);
		    rs = pstmt.executeQuery();
		    
		    while(rs.next()) {
			  String stanza = rs.getString("stanza");
			  String tipo = rs.getString("tipo");	
			  
			  //chiave unica
			  String chiave = stanza + "-" + tipo;
			  
			  //riga più recente (ORDER BY DESC)
			  if(!misureSalvate.contains(chiave)) {
				  int id = rs.getInt("id");
				  String unita = rs.getString("unita");
				  double valore = rs.getDouble("valore");
		
			  java.sql.Timestamp sqlTimestamp = rs.getTimestamp("timestamp");
			  LocalDateTime data = null;
			  if (sqlTimestamp != null) {
				  data = sqlTimestamp.toLocalDateTime();
			  }
			  
			  //creiamo oggetto pojo con dati del DB
			  ultimeMisure.add(new MisuraPOJO(id, tipo, unita, valore, stanza, data));
		      misureSalvate.add(chiave);
			  }
             }
		}
	} catch (SQLException e) {
		e.printStackTrace();		
	} finally {
		//chiudiamo lo statement
		try {
			if (rs != null) rs.close();
			if (pstmt != null ) pstmt.close();
			if (conn != null) conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
		return ultimeMisure;
	
	}

}

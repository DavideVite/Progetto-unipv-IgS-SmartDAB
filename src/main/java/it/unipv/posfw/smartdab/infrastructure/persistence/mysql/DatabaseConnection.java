package it.unipv.posfw.smartdab.infrastructure.persistence.mysql;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
	private static Connection connection = null;
	private static final Properties props = new Properties();
	
	static {
		try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
			if(input == null) {
				System.out.println("db.properties non trovato");			
			} else {
				//carica dati dal file
				props.load(input);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Connection getConnection() throws SQLException {
		if (connection == null || connection.isClosed()) {
			//legge i valori caricati dalle properties
			connection = DriverManager.getConnection(
				props.getProperty("db.url"),
				props.getProperty("db.user"),
				props.getProperty("db.password")
			);
		  }
		  return connection;
	}
}


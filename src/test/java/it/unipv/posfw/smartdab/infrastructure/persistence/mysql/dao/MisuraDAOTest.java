package it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.sql.Timestamp;

import org.junit.jupiter.api.Test;

import it.unipv.posfw.smartdab.core.beans.MisuraPOJO;

public class MisuraDAOTest {
	
	@Test
	public void testInsertMisura() {
		MisuraDAO dao = new MisuraDAOImpl();
		
		MisuraPOJO m = new MisuraPOJO("1", "Temperatura", "°C", 22.5, "SOGG1", new Timestamp(System.currentTimeMillis())
	);
		
	assertDoesNotThrow(() -> {
		dao.insertMisura(m);
	}, "Inserimento misura fallito");		
	}

}

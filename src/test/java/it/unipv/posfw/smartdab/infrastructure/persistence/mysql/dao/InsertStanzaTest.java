package it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import it.unipv.posfw.smartdab.core.domain.model.casa.Stanza;

public class InsertStanzaTest {
	
		StanzaDAO dao = new StanzaDAOImpl();
		
		@Test
		public void insertStanzaTest() {

		Stanza s0 = new Stanza ("S00", "Sala", 13, LocalDateTime.now());	
		Stanza s1 = new Stanza("S01", "Cucina", 10, LocalDateTime.now());
		Stanza s2 = new Stanza("S02", "Studio", 12, LocalDateTime.now());
		Stanza s3 = new Stanza("S03", "Camera da letto", 16, LocalDateTime.now());			
		Stanza s4 = new Stanza("S04", "Bagno", 8, LocalDateTime.now());
        Stanza s5 = new Stanza("S05", "Soggiorno", 22, LocalDateTime.now());
		
        
		assertDoesNotThrow(() -> {
			dao.insertStanza(s0);
		}, "Inserimento sala fallito");
		
		assertDoesNotThrow(() -> {
				dao.insertStanza(s1);
			}, "Inserimento cucina fallito");
			
			assertDoesNotThrow(() -> {
				dao.insertStanza(s2);
			}, "Inserimento studio fallito");
			
			assertDoesNotThrow(() -> {
				dao.insertStanza(s3);
			}, "Inserimento camera da letto fallito");
			
			assertDoesNotThrow(() -> {
				dao.insertStanza(s4);
			}, "Inserimento bagno fallito");
			
			assertDoesNotThrow(() -> {
				dao.insertStanza(s5);
			}, "Inserimento soggiorno fallito");
		}
}
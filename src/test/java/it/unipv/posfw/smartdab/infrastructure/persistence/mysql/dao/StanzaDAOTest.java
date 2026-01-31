package it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import it.unipv.posfw.smartdab.core.domain.model.casa.Stanza;

public class StanzaDAOTest {
	
	@Test
	public void insertStanzaTest() {
		StanzaDAO dao = new StanzaDAOImpl();
		
		Stanza s = new Stanza("SOGG1", "Soggiorno", 22, LocalDateTime.now());
		
		assertDoesNotThrow(() -> {
			dao.insertStanza(s);
		}, "Inserimento stanza fallito");
	}

}

package it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

import it.unipv.posfw.smartdab.core.domain.model.casa.Stanza;

public class StanzaDAOTest1 {
		
		@Test
		public void insertStanzaTest() {
			StanzaDAO dao = new StanzaDAOImpl();
			
			Stanza s = new Stanza("S02", "Cucina", 18.5);
			
			assertDoesNotThrow(() -> {
				dao.insertStanza(s);
			}, "Inserimento stanza fallito");
		}

	}

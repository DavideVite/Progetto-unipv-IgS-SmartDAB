package it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import it.unipv.posfw.smartdab.core.beans.MisuraPOJO;
import it.unipv.posfw.smartdab.core.domain.model.casa.Stanza;

public class MisuraDAOTest {
		
	MisuraDAO dao = new MisuraDAOImpl();	
	
	@Test
	public void testInsertMisura() {
            
		    StanzaDAO stanzaDao = new StanzaDAOImpl();
			// Prima creo la stanza se no mi dà un errore di foreign key (Davide)
			Stanza stanzaTest = new Stanza("StanzaDiProva", 18);
			stanzaDao.insertStanza(stanzaTest);
		
		MisuraPOJO m1 = new MisuraPOJO(0, "Temperatura", "°C", 22.5, "S01", LocalDateTime.now());
		MisuraPOJO m2 = new MisuraPOJO(0, "Luminosita", "lx", 300, "S03", LocalDateTime.now());
		MisuraPOJO m3 = new MisuraPOJO(0, "Temperatura", "°C", 21, "S02", LocalDateTime.now());
		MisuraPOJO m4 = new MisuraPOJO(0, "Temperatura", "°C", 20, "S04", LocalDateTime.now());
		
	assertDoesNotThrow(() -> {
		dao.insertMisura(m1);
		dao.insertMisura(m2);
		dao.insertMisura(m3);
		dao.insertMisura(m4);
	}, "Inserimento misura fallito");		
	}
}

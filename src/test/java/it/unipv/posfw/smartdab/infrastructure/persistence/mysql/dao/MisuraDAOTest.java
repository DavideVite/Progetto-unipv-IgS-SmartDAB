package it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.sql.Timestamp;

import org.junit.jupiter.api.Test;

import it.unipv.posfw.smartdab.core.beans.MisuraPOJO;
import it.unipv.posfw.smartdab.core.domain.model.casa.Stanza;

public class MisuraDAOTest {
		
	MisuraDAO dao = new MisuraDAOImpl();	
	
	@Test
	public void testInsertMisura() {

			// Prima creo la stanza se no mi dà un errore di foreign key (Davide)
			Stanza stanzaTest = new Stanza("StanzaDiProva");
			stanzaDAO.insertStanza(stanzaTest);
		
		MisuraPOJO m1 = new MisuraPOJO("M1", "Temperatura", "°C", 22.5, "S01", new Timestamp(System.currentTimeMillis()));
		MisuraPOJO m2 = new MisuraPOJO("M2", "Luminosita", "lx", 300, "S03", new Timestamp(System.currentTimeMillis()));
		MisuraPOJO m3 = new MisuraPOJO("M3", "Temperatura", "°C", 21, "S02", new Timestamp(System.currentTimeMillis()));
		MisuraPOJO m4 = new MisuraPOJO("M4", "Temperatura", "°C", 20, "S04", new Timestamp(System.currentTimeMillis()));
		
	assertDoesNotThrow(() -> {
		dao.insertMisura(m1);
		dao.insertMisura(m2);
		dao.insertMisura(m3);
		dao.insertMisura(m4);
	}, "Inserimento misura fallito");		
	}

	@Test
	public void readUltimaMisuraTest() {
		
		assertDoesNotThrow(() -> {
			dao.readUltimaMisura("S01", "Temperatura");
		}, "Lettura misura fallita");
	}
}

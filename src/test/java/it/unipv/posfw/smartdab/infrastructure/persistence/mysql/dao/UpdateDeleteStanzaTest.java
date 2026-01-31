package it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import it.unipv.posfw.smartdab.core.domain.model.casa.Stanza;

public class UpdateDeleteStanzaTest {
	
	StanzaDAO dao = new StanzaDAOImpl();
	
	@Test
	public void updateStanza() {
		Stanza s = new Stanza("S00", "Sala", 13, LocalDateTime.now());
		
		s.setNome("Sala da pranzo");
		s.setMq(12);
		
		assertDoesNotThrow(() -> {
			dao.updateStanza(s);
		}, "Modifica stanza fallita");
		}
    
	
	@Test
	public void deleteStanza() {
		Stanza s = new Stanza("S05", "Soggiorno", 22, LocalDateTime.now());
		
		assertDoesNotThrow(() -> {
			dao.deleteStanza(s);
		}, "Eliminazione stanza fallita");
		} 
	}



package it.unipv.posfw.smartdab.core.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unipv.posfw.smartdab.core.domain.model.casa.Casa;
import it.unipv.posfw.smartdab.core.domain.model.casa.Hub;
import it.unipv.posfw.smartdab.core.domain.model.casa.Stanza;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.lampadaON_OFF.Lampada_Communicator;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.lampadaON_OFF.Lampada_ON_OFF;

public class GestoreStanzeTest {
	
	private GestoreStanze gestore;
	private Casa casa;
	private Lampada_Communicator communicator;
	
	@BeforeEach
	void setUp() {
		Hub.getInstance("prod123");
		casa = new Casa();
		gestore = new GestoreStanze(casa);
		communicator = new Lampada_Communicator();
		Hub.getInstance().getAutenticazione().impostaPinIniziale("prod123", "12345");
	}
	
	@Test
	public void testCreaStanzaConSuccesso() {
		boolean risultato = gestore.creaStanza("S01", "Cucina", 20 , "12345", "prod123");
	    
	    // Questo ti dirà in console se il problema è il PIN (torna false)
	    System.out.println("Risultato creazione: " + risultato);
	    
	    assertTrue(risultato, "ERRORE: Il metodo ha restituito false. Controlla il PIN dell'Hub!");
	    assertTrue(casa.esisteStanza("Cucina"));

	}

	@Test
	public void testModificaNomeStanza() {
		casa.nuovaStanza(new Stanza("S02", "Bagno", 10));
		
		boolean modificato = gestore.modificaNomeStanza("Bagno", "Bagno Ospiti");
		
		assertTrue(modificato);
		assertNotNull(casa.cercaStanza("Bagno Ospiti"));
		assertFalse(casa.esisteStanza("Bagno"));
	}
	
	@Test
	public void testEliminaStanzaNonVuota() {
		Stanza cucina = new Stanza("S03", "Cucina", 20.0);
		casa.nuovaStanza(cucina);
		
	    cucina.getDispositivi().add(new Lampada_ON_OFF("L01", communicator, 3000));
	    
	    boolean eliminata = gestore.eliminaStanza("Cucina");
	    
	    assertFalse(eliminata, "La stanza non dovrebbe essere eliminata se ha dispositivi");
	    assertTrue(casa.esisteStanza("Cucina"), "La stanza dovrebbe essere ancora presente");
	}
}

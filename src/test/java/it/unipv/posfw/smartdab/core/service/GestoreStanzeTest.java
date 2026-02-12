package it.unipv.posfw.smartdab.core.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.model.casa.Casa;
import it.unipv.posfw.smartdab.core.domain.model.casa.Stanza;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.lampadaON_OFF.Lampada_Communicator;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.lampadaON_OFF.Lampada_ON_OFF;
import it.unipv.posfw.smartdab.core.domain.model.parametro.ObservableParameter;
import it.unipv.posfw.smartdab.infrastructure.messaging.topic.Topic;

public class GestoreStanzeTest {

	private GestoreStanze gestore;
	private Casa casa;
	private Lampada_Communicator communicator;

	@BeforeEach
	void setUp() {
		casa = new Casa();
		gestore = new GestoreStanze(casa);
		communicator = new Lampada_Communicator();
	}

	@Test
	public void testCreaStanzaConSuccesso() {
		assertFalse(casa.esisteStanza("Cucina2"), "La stanza non dovrebbe esistere prima del test");
		
		Stanza risultato = gestore.creaStanza("Cucina2", 20);

		assertNotNull(risultato, "La stanza dovrebbe essere stata creata");
		assertTrue(casa.esisteStanza("Cucina2"));
	}

	@Test
	public void testModificaNomeStanza() {
		casa.nuovaStanza(new Stanza("Bagno1", 10));

		boolean modificato = gestore.modificaNomeStanza("Bagno1", "Bagno Ospiti1");

		assertTrue(modificato);
		assertNotNull(casa.cercaStanza("Bagno Ospiti1"));
		assertFalse(casa.esisteStanza("Bagno1"));
	}

	@Test
	public void testEliminaStanzaNonVuota() throws Exception {
		Stanza cucina = new Stanza("S03", "Cucina", 20.0, LocalDateTime.now());
		casa.nuovaStanza(cucina);

		// Creo il Topic per la lampada
		Topic topic = Topic.createTopic("L01", cucina, DispositivoParameter.LUMINOSITA);

		// Creo ObservableParameter
		ObservableParameter obsParam = new ObservableParameter(DispositivoParameter.LUMINOSITA);

		// Creo la lampada con la signature corretta: (Topic, Lampada_Communicator, ObservableParameter, int)
	    cucina.getDispositivi().add(new Lampada_ON_OFF(topic, communicator, obsParam, 3000));
	    
	    // eliminaStanza() presenta un'eccezione che deve essere gestita qui
	    boolean eliminata = true;
	    
		try {
			eliminata = gestore.eliminaStanza("Cucina");
		} catch (Exception e) {
			e.printStackTrace();
		}

	    assertFalse(eliminata, "La stanza non dovrebbe essere eliminata se ha dispositivi");
	    assertTrue(casa.esisteStanza("Cucina"), "La stanza dovrebbe essere ancora presente");
	}
}

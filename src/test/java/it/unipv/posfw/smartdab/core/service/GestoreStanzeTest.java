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
		Stanza risultato = gestore.creaStanza("S01", 20);

		assertNotNull(risultato, "La stanza dovrebbe essere stata creata");
		assertTrue(casa.esisteStanza("Cucina"));
	}

	@Test
	public void testModificaNomeStanza() {
		casa.nuovaStanza(new Stanza("S02", "Bagno", 10, LocalDateTime.now()));

		boolean modificato = gestore.modificaNomeStanza("Bagno", "Bagno Ospiti");

		assertTrue(modificato);
		assertNotNull(casa.cercaStanza("Bagno Ospiti"));
		assertFalse(casa.esisteStanza("Bagno"));
	}

	@Test
	public void testEliminaStanzaNonVuota() {
		Stanza cucina = new Stanza("S03", "Cucina", 20.0, LocalDateTime.now());
		casa.nuovaStanza(cucina);

		// Creo il Topic per la lampada
		Topic topic = Topic.createTopic("L01", cucina, DispositivoParameter.LUMINOSITA);

		// Creo ObservableParameter
		ObservableParameter obsParam = new ObservableParameter(DispositivoParameter.LUMINOSITA);

		// Creo la lampada con la signature corretta: (Topic, Lampada_Communicator, ObservableParameter, int)
	    cucina.getDispositivi().add(new Lampada_ON_OFF(topic, communicator, obsParam, 3000));

	    boolean eliminata = gestore.eliminaStanza("Cucina");

	    assertFalse(eliminata, "La stanza non dovrebbe essere eliminata se ha dispositivi");
	    assertTrue(casa.esisteStanza("Cucina"), "La stanza dovrebbe essere ancora presente");
	}
}

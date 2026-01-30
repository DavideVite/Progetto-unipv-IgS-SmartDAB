package it.unipv.posfw.core.domain.model.casa;

import org.junit.jupiter.api.Test;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.model.casa.Stanza;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.lampadaON_OFF.Lampada_Communicator;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.lampadaON_OFF.Lampada_ON_OFF;
import it.unipv.posfw.smartdab.core.domain.model.parametro.ObservableParameter;
import it.unipv.posfw.smartdab.infrastructure.messaging.topic.Topic;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

public class StanzaTest {

		private Stanza stanza;
		private Lampada_Communicator communicator;

		@BeforeEach
		void setUp() {
			stanza = new Stanza("ID1", "Soggiorno", 25.5);
			communicator = new Lampada_Communicator();
	}

	/**
	 * Helper method per creare una Lampada_ON_OFF con la signature corretta.
	 * La signature è: (Topic, Lampada_Communicator, ObservableParameter, int)
	 */
	private Lampada_ON_OFF creaLampada(String id, int intensita) {
		// 1. Creo il Topic
		Topic topic = Topic.createTopic(id, stanza, DispositivoParameter.LUMINOSITA);

		// 2. Creo ObservableParameter
		ObservableParameter obsParam = new ObservableParameter(DispositivoParameter.LUMINOSITA);

		// 3. Creo la lampada con la signature corretta
		return new Lampada_ON_OFF(topic, communicator, obsParam, intensita);
	}

	@Test
	@DisplayName("Costruttore consegna correttamente i valori iniziali")
	void constructor_setsCorrectValues() {
		assertEquals("ID1", stanza.getId());
		assertEquals("Soggiorno", stanza.getNome());
		assertEquals(25.5, stanza.getMq());
		assertTrue(stanza.isEmpty(), "La stanza all'inizio dovrebbe essere vuota");
	}

	@Test
	@DisplayName("Aggiunta dispositivo e aggiornamento corretto della lista")
	void addDispositivo_addDeviceToList() {
		Lampada_ON_OFF lampada = creaLampada("L01", 3000);
		stanza.addDispositivo(lampada);

		assertFalse(stanza.isEmpty());
		assertEquals(1, stanza.getDispositivi().size());
		assertTrue(stanza.getDispositivi().contains(lampada));
		assertEquals(3000, lampada.getIntensita());
	}

	@Test
	@DisplayName("Aggiunta dispositivo null non modifica la lista")
	void addDispositivo_withNull_doesNothing() {
		stanza.addDispositivo(null);
		assertTrue(stanza.isEmpty());
	}

	@Test
	@DisplayName("Rimozione dispositivo funziona correttamente")
	void removeDispositivo_removesFromList() {
		Lampada_ON_OFF lampada = creaLampada("L01", 800);
        stanza.addDispositivo(lampada);
        stanza.removeDispositivo(lampada);

        assertTrue(stanza.isEmpty());
    }

    @Test
    @DisplayName("updateParameter inserisce il parametro e lo recupera correttamente")
    void updateParameter_setsAndRetrievesValue() {
        stanza.updateParameter("Temperatura", 21.5);

        assertEquals(21.5, stanza.getMisura("Temperatura"));
        assertTrue(stanza.getParametri().containsKey("Temperatura"));
    }

    @Test
    @DisplayName("isEmpty restituisce true se la lista dispositivi è null o vuota")
    void isEmpty_returnsTrueWhenNoDevices() {
        assertTrue(stanza.isEmpty());

        Lampada_ON_OFF lampada = creaLampada("L02", 3000);
        stanza.addDispositivo(lampada);
        assertFalse(stanza.isEmpty());
    }

    @Test
    @DisplayName("setNome aggiorna il nome correttamente")
    void setNome_updatesName() {
        stanza.setNome("Cucina");
        assertEquals("Cucina", stanza.getNome());
    }
	}

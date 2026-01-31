package it.unipv.posfw.smartdab.infrastructure.messaging.topic;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.lampadaON_OFF.Lampada_Communicator;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.lampadaON_OFF.Lampada_ON_OFF;
import it.unipv.posfw.smartdab.core.domain.model.parametro.ObservableParameter;
import it.unipv.posfw.smartdab.core.port.room.RoomPort;
import it.unipv.posfw.smartdab.infrastructure.messaging.topic.Topic;
import it.unipv.posfw.smartdab.core.domain.model.casa.Stanza;

public class TopicTest {
	private static Topic topic;
	private static Lampada_ON_OFF d;
	private static RoomPort r;
	private static DispositivoParameter p;
	private static String deviceId;


	@BeforeAll
	public static void initTest() {

		// Effettuo test su implementazioni concrete
		deviceId = "lamp1";
		r = new Stanza("1", "room1", 0.0, LocalDateTime.now());
		p = Lampada_ON_OFF.parameter;

		// Creo prima il Topic
		Topic lampTopic = Topic.createTopic(deviceId, r, p);

		// Creo ObservableParameter
		ObservableParameter obsParam = new ObservableParameter(p);

		// Creo Lampada con la signature corretta: (Topic, Lampada_Communicator, ObservableParameter, int)
		Lampada_Communicator lc = new Lampada_Communicator();
		d = new Lampada_ON_OFF(lampTopic, lc, obsParam, 2000);
	}

	@BeforeEach
	public void reInitTest() {
		topic = null;
	}

	// L'unico punto critico è l'inizializzazione, il resto è con logica analoga o semplice

	@Test
	@DisplayName("Inizializzazione topic valida: OK")
	public void createTopicEq1() {
		try {
			topic = Topic.createTopic(deviceId, r, p);

		} catch(IllegalArgumentException e) {
			fail(e.getMessage());
		}
	}

	@Test
	@DisplayName("Inizializzazione topic con stanza nulla: KO")
	public void createTopicEq2() {
		try {
			topic = Topic.createTopic(deviceId, null, p);
			fail("Topic ha fallito nell'inizializzazione");

		} catch(IllegalArgumentException e) {

		}
	}

	@Test
	@DisplayName("Inizializzazione topic con id nullo: KO")
	public void createTopicEq3() {
		try {
			topic = Topic.createTopic(null, r, p);
			fail("Topic ha fallito nell'inizializzazione");

		} catch(IllegalArgumentException e) {

		}
	}

	@Test
	@DisplayName("Inizializzazione topic con parametro nullo: KO")
	public void createTopicEq4() {
		try {
			topic = Topic.createTopic(deviceId, r, null);
			fail("Topic ha fallito nell'inizializzazione");

		} catch(IllegalArgumentException e) {

		}
	}


}

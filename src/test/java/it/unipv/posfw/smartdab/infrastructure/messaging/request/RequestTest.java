package it.unipv.posfw.smartdab.infrastructure.messaging.request;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.enums.Message;
import it.unipv.posfw.smartdab.core.domain.model.casa.Stanza;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.lampadaON_OFF.Lampada_Communicator;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.lampadaON_OFF.Lampada_ON_OFF;
import it.unipv.posfw.smartdab.core.domain.model.parametro.ObservableParameter;
import it.unipv.posfw.smartdab.core.port.room.RoomPort;
import it.unipv.posfw.smartdab.infrastructure.messaging.topic.Topic;

public class RequestTest {
	private static Topic topic;
	private static Lampada_ON_OFF d;
	private static RoomPort r;
	private static DispositivoParameter p;
	private static String type;
	private static Object val;
	private static Request req;
	private static String deviceId;


	@BeforeAll
	public static void initTest() {

		// Effettuo test su implementazioni concrete
		deviceId = "lamp1";
		r = new Stanza("1", "room1", 0.0);
		p = Lampada_ON_OFF.parameter;

		// Creo prima il Topic
		topic = Topic.createTopic(deviceId, r, p);

		// Creo ObservableParameter
		ObservableParameter obsParam = new ObservableParameter(p);

		// Creo Lampada con la signature corretta: (Topic, Lampada_Communicator, ObservableParameter, int)
		Lampada_Communicator lc = new Lampada_Communicator();
		d = new Lampada_ON_OFF(topic, lc, obsParam, 2000);

		type = Message.CONFIG.toString();
		val = "Stato-1";
	}

	@BeforeEach
	public void reInitTest() {
		req = null;
	}


	// L'unico punto critico è l'inizializzazione, il resto è con logica analoga o semplice

	@Test
	@DisplayName("Inizializzazione request valida: OK")
	public void createRequestEq1() {
		try {
			req = Request.createRequest(topic, type, val);
		} catch(IllegalArgumentException e) {
			fail(e.getMessage());
		}
	}

	@Test
	@DisplayName("Inizializzazione request con topic nullo: KO")
	public void createRequestEq2() {
		try {
			req = Request.createRequest(null, type, val);
			fail("Request ha fallito nell'inizializzazione");

		} catch(IllegalArgumentException e) {

		}
	}

	@Test
	@DisplayName("Inizializzazione request con type nullo: KO")
	public void createRequestEq3() {
		try {
			req = Request.createRequest(topic, null, val);
			fail("Topic ha fallito nell'inizializzazione");

		} catch(IllegalArgumentException e) {

		}
	}

	@Test
	@DisplayName("Inizializzazione request con val nullo: KO")
	public void createRequestEq4() {
		try {
			req = Request.createRequest(topic, type, null);
			fail("Topic ha fallito nell'inizializzazione");

		} catch(IllegalArgumentException e) {

		}
	}

	@Test
	@DisplayName("Inizializzazione request con type senza composizione di message: KO")
	public void createRequestEq5() {
		String[] testCases = {"", "CONFIG.ECO", "ECO.CONFIG", "SCR", "TRIAC.MOS", "1"};

		for(String test: testCases) {
			try {
				req = Request.createRequest(topic, test, val);
				fail("E' stata inizializzata una request erronea");
				break;
			} catch(IllegalArgumentException e) {
				continue;
			}
		}
	}
}

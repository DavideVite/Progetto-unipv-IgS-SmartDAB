package it.unipv.posfw.smartdab.infrastructure.messaging.topic;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.lampadaON_OFF.Lampada_Communicator;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.lampadaON_OFF.Lampada_ON_OFF;
import it.unipv.posfw.smartdab.core.port.room.RoomPort;
import it.unipv.posfw.smartdab.infrastructure.messaging.topic.Topic;
import it.unipv.posfw.smartdab.core.domain.model.casa.Stanza;

public class TopicTest {
	private static Topic topic;
	private static Lampada_ON_OFF d;
	private static RoomPort r;
	private static DispositivoParameter p;
	
	
	@BeforeAll
	public static void initTest() {
		
		// Effettuo test su implementazioni concrete
		
		Lampada_Communicator lc = new Lampada_Communicator();
		d = new Lampada_ON_OFF("lamp1", lc, 2000);
		r = new Stanza("1", "room1");
		p = Lampada_ON_OFF.parameter;
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
			topic = Topic.createTopic(r, d, p);
		
		} catch(IllegalArgumentException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	@DisplayName("Inizializzazione topic con stanza nulla: KO")
	public void createTopicEq2() {
		try {
			topic = Topic.createTopic(null, d, p);
			fail("Topic ha fallito nell'inizializzazione");
		
		} catch(IllegalArgumentException e) {
			assertTrue(topic == null, "Topic non inizializzato");
		}
	}
	
	@Test
	@DisplayName("Inizializzazione topic con dispositivo nullo: KO")
	public void createTopicEq3() {
		try {
			topic = Topic.createTopic(r, null, p);
			fail("Topic ha fallito nell'inizializzazione");
		
		} catch(IllegalArgumentException e) {
			assertTrue(topic == null, "Topic non inizializzato");
		}
	}
	
	@Test
	@DisplayName("Inizializzazione topic con parametro nullo: KO")
	public void createTopicEq4() {
		try {
			topic = Topic.createTopic(r, d, null);
			fail("Topic ha fallito nell'inizializzazione");
		
		} catch(IllegalArgumentException e) {
			assertTrue(topic == null, "Topic non inizializzato");
		}
	}
	

}

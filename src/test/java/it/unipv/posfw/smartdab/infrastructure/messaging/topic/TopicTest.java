package it.unipv.posfw.smartdab.infrastructure.messaging.topic;

import static org.junit.jupiter.api.Assertions.assertFalse;
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
	private static String id;
	private static RoomPort r;
	private static DispositivoParameter p;
	
	
	@BeforeAll
	public static void initTest() {
		
		// Effettuo test su implementazioni concrete
		
		id = "ab12";
		r = new Stanza("1", "room1");
		p = Lampada_ON_OFF.parameter;
	}
	
	@BeforeEach
	public void reInitTest() {
		topic = Topic.createTopic(id, r, p);
	}
	
	// L'unico punto critico è l'inizializzazione, il resto è con logica analoga o semplice 
	
	@Test
	@DisplayName("Caratteri alfanumerici da 1-20: OK")
	public void setIdEq1() {
		String[] testValues = {"dispositivo1", "dispositivortrtrt123", "dev0"};
		boolean res = true;
		for(String test : testValues) {
			res = (res && topic.setId(test));
		}
		assertTrue(res);
	}
	
	@Test
	@DisplayName("Caratteri alfanumerici con maiuscole da 1-20: OK")
	public void setIdEq2() {
		String[] testValues = {"DISPOSITIVO1", "DispOsitivOrtrtrt123", "deV012"};
		boolean res = true;
		for(String test : testValues) {
			res = (res && topic.setId(test));
		}
		assertTrue(res);
	}
	
	@Test
	@DisplayName("Caratteri alfanumerici > 17: KO")
	public void setIdEq3() {
		String[] testValues = {"DISPOSITIVOWERTYUU1", "DispOsitivOrtrtrtTTT123", "deV012"};
		boolean res = true;
		for(String test : testValues) {
			res = (res && topic.setId(test));
		}
		assertFalse(res);
	}
	
	@Test
	@DisplayName("Caratteri numerici > 3: KO")
	public void setIdEq4() {
		String[] testValues = {"A12345", "Dispositivo1111", "deV012"};
		boolean res = true;
		for(String test : testValues) {
			res = (res && topic.setId(test));
		}
		assertFalse(res);
	}
	
	@Test
	@DisplayName("Caratteri alfanumerici < 1: KO")
	public void setIdEq5() {
		String[] testValues = {"", "1", "12"};
		boolean res = true;
		for(String test : testValues) {
			res = (res && topic.setId(test));
		}
		assertFalse(res);
	}
	
	@Test
	@DisplayName("Caratteri speciali: KO")
	public void setIdEq6() {
		String[] testValues = {"A$$$7", "Dispositivo@#§", "d§§*?^"};
		boolean res = true;
		for(String test : testValues) {
			res = (res && topic.setId(test));
		}
		assertFalse(res);
	}
	
	@Test
	@DisplayName("Inizializzazione topic con stanza nulla: KO")
	public void createTopicEq1() {
		try {
			topic = Topic.createTopic(id, null, p);
			fail("Topic ha fallito nell'inizializzazione");
		
		} catch(IllegalArgumentException e) {

		}
	}
	
	@Test
	@DisplayName("Inizializzazione topic con id nullo: KO")
	public void createTopicEq2() {
		try {
			topic = Topic.createTopic(null, r, p);
			fail("Topic ha fallito nell'inizializzazione");
		
		} catch(IllegalArgumentException e) {

		}
	}
	
	@Test
	@DisplayName("Inizializzazione topic con parametro nullo: KO")
	public void createTopicEq3() {
		try {
			topic = Topic.createTopic(id, r, null);
			fail("Topic ha fallito nell'inizializzazione");
		
		} catch(IllegalArgumentException e) {

		}
	}
	

}

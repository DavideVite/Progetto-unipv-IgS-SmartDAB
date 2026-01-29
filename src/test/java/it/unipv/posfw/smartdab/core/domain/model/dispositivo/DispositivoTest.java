package it.unipv.posfw.smartdab.core.domain.model.dispositivo;

import org.junit.jupiter.api.Test;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.model.casa.Stanza;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.lampadaON_OFF.Lampada_Communicator;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.lampadaON_OFF.Lampada_ON_OFF;
import it.unipv.posfw.smartdab.core.domain.model.parametro.ObservableParameter;
import it.unipv.posfw.smartdab.infrastructure.messaging.topic.Topic;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;

/**
 * Test per Dispositivo.
 * Poiché Dispositivo è ora astratto, testiamo attraverso un'implementazione concreta (Lampada_ON_OFF).
 * L'ID del dispositivo è ora gestito tramite Topic, quindi testiamo Topic.setId().
 */
public class DispositivoTest {

	private static Dispositivo d;
	private static Topic topic;

	@BeforeAll
	public static void initTest() {
		// Creo una Stanza per il Topic
		Stanza stanza = new Stanza("S01", "TestRoom", 20.0);
		DispositivoParameter param = DispositivoParameter.LUMINOSITA;

		// Creo il Topic
		topic = Topic.createTopic("esempio1", stanza, param);

		// Creo ObservableParameter
		ObservableParameter obsParam = new ObservableParameter(param);

		// Creo una Lampada (implementazione concreta di Dispositivo)
		Lampada_Communicator ic = new Lampada_Communicator();
		d = new Lampada_ON_OFF(topic, ic, obsParam, 3000);
	}

	// Faccio solo dei test sull'ID in quanto tutte le altre funzionalità riguardano
	// Tipi semistrutturati, dunque che si controllano internamente
	// L'ID è ora gestito tramite Topic.setId()

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
}

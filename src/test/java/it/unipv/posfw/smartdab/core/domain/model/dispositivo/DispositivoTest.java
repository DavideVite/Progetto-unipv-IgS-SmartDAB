package it.unipv.posfw.smartdab.core.domain.model.dispositivo;

import org.junit.jupiter.api.Test;

import it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.lampadaON_OFF.Lampada_Communicator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;

public class DispositivoTest {
	
	private static Dispositivo d;
	
	@BeforeAll
	public static void initTest() {
		
		Lampada_Communicator ic = new Lampada_Communicator();
		d = new Dispositivo("esempio1", ic, true);
		
	}
	
	// Faccio solo dei test sull'ID in quanto tutte le altre funzionalità riguardano
	// Tipi semistrutturati, dunque che si controllano internamente
	
	@Test
	@DisplayName("Caratteri alfanumerici da 1-20: OK")
	public void setIdEq1() {
		String[] testValues = {"dispositivo1", "dispositivortrtrt123", "dev0"};
		boolean res = true;
		for(String test : testValues) {
			res = (res && d.setId(test));
		}
		assertTrue(res);
	}
	
	@Test
	@DisplayName("Caratteri alfanumerici con maiuscole da 1-20: OK")
	public void setIdEq2() {
		String[] testValues = {"DISPOSITIVO1", "DispOsitivOrtrtrt123", "deV012"};
		boolean res = true;
		for(String test : testValues) {
			res = (res && d.setId(test));
		}
		assertTrue(res);
	}
	
	@Test
	@DisplayName("Caratteri alfanumerici > 17: KO")
	public void setIdEq3() {
		String[] testValues = {"DISPOSITIVOWERTYUU1", "DispOsitivOrtrtrtTTT123", "deV012"};
		boolean res = true;
		for(String test : testValues) {
			res = (res && d.setId(test));
		}
		assertFalse(res);
	}
	
	@Test
	@DisplayName("Caratteri numerici > 3: KO")
	public void setIdEq4() {
		String[] testValues = {"A12345", "Dispositivo1111", "deV012"};
		boolean res = true;
		for(String test : testValues) {
			res = (res && d.setId(test));
		}
		assertFalse(res);
	}
	
	@Test
	@DisplayName("Caratteri alfanumerici < 1: KO")
	public void setIdEq5() {
		String[] testValues = {"", "1", "12"};
		boolean res = true;
		for(String test : testValues) {
			res = (res && d.setId(test));
		}
		assertFalse(res);
	}
	
	@Test
	@DisplayName("Caratteri speciali: KO")
	public void setIdEq6() {
		String[] testValues = {"A$$$7", "Dispositivo@#§", "d§§*?^"};
		boolean res = true;
		for(String test : testValues) {
			res = (res && d.setId(test));
		}
		assertFalse(res);
	}
}

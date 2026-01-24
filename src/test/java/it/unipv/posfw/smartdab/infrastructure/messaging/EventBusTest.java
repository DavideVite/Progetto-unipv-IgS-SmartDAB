package it.unipv.posfw.smartdab.infrastructure.messaging;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import it.unipv.posfw.smartdab.factory.EventBusFactory;

public class EventBusTest {
	private static EventBus b;

	@BeforeAll
	public static void initTest() {
		b = EventBusFactory.getEventBus();
	}
	
	@Test
	@DisplayName("Chiamata getSubscribers senza nessun dispositivo: OK")
	public void getSubscribersEq1() {
		assertTrue(b.getSubscribers().isEmpty(), "Nessun dispositivo trovato");
	}
	
	// Test di integrazione Dispositivi-EventBus
	

	
}

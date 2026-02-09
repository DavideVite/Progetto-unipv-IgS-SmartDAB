package it.unipv.posfw.smartdab.core.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.enums.Message;
import it.unipv.posfw.smartdab.core.domain.model.casa.Casa;
import it.unipv.posfw.smartdab.core.domain.model.casa.Stanza;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.lampadaON_OFF.Lampada_ON_OFF;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.lampadaON_OFF.Lampada_Communicator;
import it.unipv.posfw.smartdab.core.domain.model.parametro.ObservableParameter;
import it.unipv.posfw.smartdab.core.domain.model.scenario.Scenario;
import it.unipv.posfw.smartdab.core.domain.model.scenario.StanzaConfig;
import it.unipv.posfw.smartdab.core.port.messaging.IEventBusClient;
import it.unipv.posfw.smartdab.core.service.GestoreStanze;
import it.unipv.posfw.smartdab.core.service.ParametroManager;
import it.unipv.posfw.smartdab.core.service.ScenarioManager;
import it.unipv.posfw.smartdab.infrastructure.messaging.request.Request;
import it.unipv.posfw.smartdab.infrastructure.messaging.topic.Topic;

/**
 * Test per ScenarioController.
 * Verifica il flusso completo di gestione scenari.
 */
public class ScenarioControllerTest {

	private ScenarioController controller;
	private ScenarioManager scenarioManager;
	private ParametroManager parametroManager;
	private MockEventBusClient mockEventBus;
	private Casa casa;
	private Stanza stanzaSoggiorno;
	private Lampada_ON_OFF lampada;

	@BeforeEach
	void setUp() {
		// Setup ScenarioManager e Controller
		scenarioManager = new ScenarioManager();
		controller = new ScenarioController(scenarioManager);

		// Setup casa e stanza per i test di attivazione
		casa = new Casa();
		stanzaSoggiorno = new Stanza("S01", "Soggiorno", 25.0, LocalDateTime.now());
		casa.nuovaStanza(stanzaSoggiorno);

		// Setup lampada con la signature corretta
		// 1. Creo il Topic prima
		Topic topic = Topic.createTopic("Lamp01", stanzaSoggiorno, DispositivoParameter.LUMINOSITA);

		// 2. Creo ObservableParameter
		ObservableParameter obsParam = new ObservableParameter(DispositivoParameter.LUMINOSITA);

		// 3. Creo la lampada con la signature corretta: (Topic, Lampada_Communicator, ObservableParameter, int)
		Lampada_Communicator communicator = new Lampada_Communicator();
		lampada = new Lampada_ON_OFF(topic, communicator, obsParam, 3000);
		lampada.switchDispositivo();
		stanzaSoggiorno.addDispositivo(lampada);

		// Setup ParametroManager con mock EventBus
		GestoreStanze gestoreStanze = new GestoreStanze(casa);
		mockEventBus = new MockEventBusClient();
		parametroManager = new ParametroManager(gestoreStanze, mockEventBus);
	}

	// ========== TEST CREAZIONE SCENARIO ==========

	@Test
	@DisplayName("Crea scenario vuoto con successo")
	void testCreaScenarioVuoto() {
		Scenario scenario = controller.creaScenarioVuoto("ScenarioTest");

		assertNotNull(scenario);
		assertEquals("ScenarioTest", scenario.getNome());
		assertTrue(scenario.getConfigurazioni().isEmpty());
		assertFalse(scenario.isActive());
	}

	@Test
	@DisplayName("Scenario vuoto viene registrato nel manager")
	void testScenarioRegistratoInManager() {
		controller.creaScenarioVuoto("ScenarioTest");

		assertTrue(scenarioManager.esisteScenario("ScenarioTest"));
	}

	// ========== TEST AGGIUNTA STANZACONFIG ==========

	@Test
	@DisplayName("Aggiunge StanzaConfig a scenario esistente")
	void testAggiungiStanzaConfig() {
		controller.creaScenarioVuoto("ScenarioTest");

		StanzaConfig config = controller.creaStanzaConfig(
			"S01",
			DispositivoParameter.TEMPERATURA,
			22.0
		);
		boolean risultato = controller.aggiungiConfigAScenario("ScenarioTest", config);

		assertTrue(risultato);
		List<StanzaConfig> configurazioni = controller.getConfigurazioniScenario("ScenarioTest");
		assertEquals(1, configurazioni.size());
	}

	@Test
	@DisplayName("Aggiunge multiple StanzaConfig allo stesso scenario")
	void testAggiungiMultipleConfig() {
		controller.creaScenarioVuoto("ScenarioTest");

		StanzaConfig config1 = controller.creaStanzaConfig("S01", DispositivoParameter.TEMPERATURA, 22.0);
		StanzaConfig config2 = controller.creaStanzaConfig("S01", DispositivoParameter.LUMINOSITA, 5000.0);
		StanzaConfig config3 = controller.creaStanzaConfig("S02", DispositivoParameter.TEMPERATURA, 20.0);

		controller.aggiungiConfigAScenario("ScenarioTest", config1);
		controller.aggiungiConfigAScenario("ScenarioTest", config2);
		controller.aggiungiConfigAScenario("ScenarioTest", config3);

		List<StanzaConfig> configurazioni = controller.getConfigurazioniScenario("ScenarioTest");
		assertEquals(3, configurazioni.size());
	}

	// ========== TEST RIMOZIONE STANZACONFIG ==========

	@Test
	@DisplayName("Rimuove StanzaConfig da scenario")
	void testRimuoviStanzaConfig() {
		controller.creaScenarioVuoto("ScenarioTest");
		StanzaConfig config = controller.creaStanzaConfig("S01", DispositivoParameter.TEMPERATURA, 22.0);
		controller.aggiungiConfigAScenario("ScenarioTest", config);

		boolean risultato = controller.rimuoviConfigDaScenario("ScenarioTest", "S01", DispositivoParameter.TEMPERATURA);

		assertTrue(risultato);
		List<StanzaConfig> configurazioni = controller.getConfigurazioniScenario("ScenarioTest");
		assertTrue(configurazioni.isEmpty());
	}

	@Test
	@DisplayName("Rimozione fallisce per config inesistente")
	void testRimuoviConfigInesistente() {
		controller.creaScenarioVuoto("ScenarioTest");

		boolean risultato = controller.rimuoviConfigDaScenario("ScenarioTest", "S99", DispositivoParameter.TEMPERATURA);

		assertFalse(risultato);
	}

	// ========== TEST MODIFICA STANZACONFIG (rimuovi + aggiungi) ==========

	@Test
	@DisplayName("Modifica StanzaConfig (rimuovi vecchia, aggiungi nuova)")
	void testModificaStanzaConfig() {
		controller.creaScenarioVuoto("ScenarioTest");

		// Aggiungi config originale
		StanzaConfig configOriginale = controller.creaStanzaConfig("S01", DispositivoParameter.TEMPERATURA, 22.0);
		controller.aggiungiConfigAScenario("ScenarioTest", configOriginale);

		// Modifica: rimuovi vecchia e aggiungi nuova
		controller.rimuoviConfigDaScenario("ScenarioTest", "S01", DispositivoParameter.TEMPERATURA);
		StanzaConfig configNuova = controller.creaStanzaConfig("S01", DispositivoParameter.TEMPERATURA, 25.0);
		controller.aggiungiConfigAScenario("ScenarioTest", configNuova);

		// Verifica
		List<StanzaConfig> configurazioni = controller.getConfigurazioniScenario("ScenarioTest");
		assertEquals(1, configurazioni.size());
		assertTrue(configurazioni.get(0).getParametro().getDisplayString().contains("25"));
	}

	// ========== TEST ELIMINAZIONE SCENARIO ==========

	@Test
	@DisplayName("Elimina scenario esistente")
	void testEliminaScenario() {
		controller.creaScenarioVuoto("ScenarioTest");

		boolean risultato = controller.eliminaScenario("ScenarioTest");

		assertTrue(risultato);
		assertFalse(scenarioManager.esisteScenario("ScenarioTest"));
	}

	@Test
	@DisplayName("Eliminazione fallisce per scenario inesistente")
	void testEliminaScenarioInesistente() {
		boolean risultato = controller.eliminaScenario("ScenarioFantasma");

		assertFalse(risultato);
	}

	// ========== TEST ATTIVAZIONE SCENARIO ==========

	@Test
	@DisplayName("Attiva scenario con configurazioni - invia comandi a ParametroManager")
	void testAttivaScenario() {
		// Crea scenario con config per la lampada
		controller.creaScenarioVuoto("ScenarioSera");
		StanzaConfig configLampada = controller.creaStanzaConfig(
			"Soggiorno",  // Deve corrispondere all'ID usato in GestoreStanze
			DispositivoParameter.LUMINOSITA,
			3000.0
		);
		controller.aggiungiConfigAScenario("ScenarioSera", configLampada);

		// Attiva scenario
		boolean risultato = controller.attivaScenario("ScenarioSera", parametroManager);

		// Verifica
		assertTrue(risultato);
		assertTrue(mockEventBus.isRequestSent());
		assertTrue(scenarioManager.getScenario("ScenarioSera").isActive());
	}

	@Test
	@DisplayName("Attivazione scenario vuoto ha successo")
	void testAttivaScenarioVuoto() {
		controller.creaScenarioVuoto("ScenarioVuoto");

		boolean risultato = controller.attivaScenario("ScenarioVuoto", parametroManager);

		assertTrue(risultato);
		assertTrue(scenarioManager.getScenario("ScenarioVuoto").isActive());
	}

	@Test
	@DisplayName("Attivazione fallisce per scenario inesistente")
	void testAttivaScenarioInesistente() {
		boolean risultato = controller.attivaScenario("ScenarioFantasma", parametroManager);

		assertFalse(risultato);
	}

	// ========== TEST DISATTIVAZIONE SCENARIO ==========

	@Test
	@DisplayName("Disattiva scenario attivo")
	void testDisattivaScenario() {
		controller.creaScenarioVuoto("ScenarioTest");
		controller.attivaScenario("ScenarioTest", parametroManager);

		boolean risultato = controller.disattivaScenario("ScenarioTest");

		assertTrue(risultato);
		assertFalse(scenarioManager.getScenario("ScenarioTest").isActive());
	}

	@Test
	@DisplayName("Disattivazione fallisce per scenario inesistente")
	void testDisattivaScenarioInesistente() {
		boolean risultato = controller.disattivaScenario("ScenarioFantasma");

		assertFalse(risultato);
	}

	// ========== TEST VISUALIZZAZIONE CONFIGURAZIONI ==========

	@Test
	@DisplayName("Visualizza configurazioni di scenario")
	void testGetConfigurazioniScenario() {
		controller.creaScenarioVuoto("ScenarioTest");
		controller.aggiungiConfigAScenario("ScenarioTest",
			controller.creaStanzaConfig("S01", DispositivoParameter.TEMPERATURA, 22.0));
		controller.aggiungiConfigAScenario("ScenarioTest",
			controller.creaStanzaConfig("S02", DispositivoParameter.LUMINOSITA, 5000.0));

		List<StanzaConfig> configurazioni = controller.getConfigurazioniScenario("ScenarioTest");

		assertNotNull(configurazioni);
		assertEquals(2, configurazioni.size());
	}

	@Test
	@DisplayName("Ritorna null per scenario inesistente")
	void testGetConfigurazioniScenarioInesistente() {
		List<StanzaConfig> configurazioni = controller.getConfigurazioniScenario("ScenarioFantasma");

		assertNull(configurazioni);
	}

	// ========== MOCK EVENTBUS CLIENT ==========

	private static class MockEventBusClient implements IEventBusClient {
		private Request lastRequest;
		private boolean requestSent = false;

		@Override
		public void setRequest(Request request) {
			this.lastRequest = request;
		}

		@Override
		public Message sendRequest(Request request) {
			this.requestSent = true;
			return Message.ACK;
		}

		public Request getLastRequest() {
			return lastRequest;
		}

		public boolean isRequestSent() {
			return requestSent;
		}
	}
}

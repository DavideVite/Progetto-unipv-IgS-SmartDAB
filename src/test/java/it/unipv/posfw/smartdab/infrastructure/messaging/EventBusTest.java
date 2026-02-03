package it.unipv.posfw.smartdab.infrastructure.messaging;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.enums.DispositivoStates;
import it.unipv.posfw.smartdab.core.domain.enums.Message;
import it.unipv.posfw.smartdab.core.domain.model.casa.Stanza;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.pompa_di_calore.PompaDiCalore;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.pompa_di_calore.PompaDiCalore_Communicator;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.sensori.Termometro.Termometro;
import it.unipv.posfw.smartdab.core.domain.model.parametro.ObservableParameter;
import it.unipv.posfw.smartdab.core.port.communication.StandardCommunicator;
import it.unipv.posfw.smartdab.core.service.DispositiviManager;
import it.unipv.posfw.smartdab.factory.EventBusFactory;
import it.unipv.posfw.smartdab.infrastructure.messaging.request.Request;
import it.unipv.posfw.smartdab.infrastructure.messaging.topic.Topic;

public class EventBusTest {
	private static EventBus b;
	private static DispositiviManager dm;
	private static Termometro sensore;
	private static PompaDiCalore attuatore;
	private static Stanza stanza;

	@BeforeAll
	public static void initTest() {
		
		dm = new DispositiviManager();
		b = EventBusFactory.getEventBus(dm);
		
		// Creo la stanza
		stanza = new Stanza("S00", 12);
		
		// Creo il parametro osservabile
		ObservableParameter op = new ObservableParameter(DispositivoParameter.TEMPERATURA);
		
		// Iscrivo la stanza al parametro osservabile
		op.addObserver(stanza);
		
		// Istanzio sensore e attuatore
		Topic t1 = Topic.createTopic("Pompa1", stanza, DispositivoParameter.TEMPERATURA);
		Topic t2 = Topic.createTopic("Termometro1", stanza, DispositivoParameter.TEMPERATURA);
		
		PompaDiCalore_Communicator pompa_c = new PompaDiCalore_Communicator();
		pompa_c.addObserver(b);
		StandardCommunicator temp_c = new StandardCommunicator();
		temp_c.addObserver(b);
		
		attuatore = new PompaDiCalore(t1, pompa_c, op);
		sensore = new Termometro(t2, temp_c);
		
		// Faccio osservare la stanza al sensore
		stanza.addObserver(sensore);
		stanza.addDispositivo(attuatore);
		stanza.addDispositivo(sensore);
	}
	
	@BeforeEach
	public void reInitTest() {
		b.clearDispositivi();
	}
	
	@Test
	@DisplayName("Chiamata getSubscribers senza nessun dispositivo")
	public void getSubscribers() {
		assertTrue(b.getSubscribers().isEmpty(), "Nessun dispositivo trovato");
	}
	
	@Test
	@DisplayName("Ricerca del dispositivo quando è presente: OK")
	public void searchDispositivoByNameEq1() {
		b.addDispositivo(attuatore);
		assertTrue(b.searchDispositivoByName(attuatore.toString()) != null, "Dispositivo trovato");
	}
	
	@Test
	@DisplayName("Ricerca del dispositivo quando è assente: KO")
	public void searchDispositivoByNameEq2() {
		assertTrue(b.searchDispositivoByName(attuatore.toString()) == null, "Dispositivo non trovato");
	}
	
	@Test
	@DisplayName("Aggiungi dispositivo")
	public void addDispositivo() {
		// L'errore di integrità non è rilevante, voglio solo assicurarmi che il dispositivo venga inserito
		b.addDispositivo(attuatore);
		
		if(dm.getDispositivi().isEmpty()) fail("DispositivoPOJO non aggiunto al dispositivo manager");
		if(b.searchDispositivoByName(attuatore.getTopic().getId()) == null) fail("Dispositivo non inserito corretamente");
	}
	
	// Test di integrazione Dispositivi-EventBus
	
	@Test
	@DisplayName("Percorso dalla misura del sensore all'azione dell'attuatore")
	public void catenaSensoreAttuatore() {
		
		attuatore.setState(DispositivoStates.ALIVE);
		
		// Riconoscimento dei dispositivi nell'event bus
		b.addDispositivo(attuatore);
		b.addDispositivo(sensore);
		
		
		// Voglio impostare il setpoint della pompa di calore a 25°
		Request r1 = Request.createRequest(attuatore.getTopic(), Message.UPDATE + "." + Message.SETPOINT, 25.0);
		b.setRequest(r1);
		b.sendRequest(r1);
		
		// Ipotizzo di partire nella condizione in cui la stanza è a 10°
		stanza.updateParameter(DispositivoParameter.TEMPERATURA.toString(), 10.0);
		
		// Si attiva la catena sensore -> eventBus -> attuatore -> ...
		
		// Verifico che la temperatura sia nel range di tolleranza della pompa
		assertTrue(
				(stanza.getMisura(DispositivoParameter.TEMPERATURA.toString()) >= 22.0) &&
				(stanza.getMisura(DispositivoParameter.TEMPERATURA.toString()) <= 28.0));
		
	}

	
}

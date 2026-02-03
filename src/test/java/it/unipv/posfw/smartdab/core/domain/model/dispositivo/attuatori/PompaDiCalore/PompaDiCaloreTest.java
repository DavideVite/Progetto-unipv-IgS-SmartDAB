package it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.PompaDiCalore;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.model.casa.Stanza;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.pompa_di_calore.PompaDiCalore;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.pompa_di_calore.PompaDiCalore_Communicator;
import it.unipv.posfw.smartdab.core.domain.model.parametro.ObservableParameter;
import it.unipv.posfw.smartdab.infrastructure.messaging.topic.Topic;

public class PompaDiCaloreTest {
	private static PompaDiCalore d;
	private static PompaDiCalore_Communicator c;
	private static Stanza stanza;
	private static ObservableParameter obsParam;

	@BeforeAll
	public static void initTest() {
		// Creo una Stanza per il Topic
		stanza = new Stanza("S01", "TestRoom", 20.0, LocalDateTime.now());
		DispositivoParameter param = DispositivoParameter.TEMPERATURA;

		// Creo il Topic
		Topic topic = Topic.createTopic("esempio1", stanza, param);

		// Creo ObservableParameter
		obsParam = new ObservableParameter(param);
		
		// Collego ObservableParameter alla stanza
		obsParam.addObserver(stanza);
		
		// Inserisco il nuovo parametro da gestire nella stanza
		stanza.updateParameter(obsParam.getParameterName().toString(), 10);
		
		// Creo una Lampada (implementazione concreta di Dispositivo)
		c = new PompaDiCalore_Communicator();
		d = new PompaDiCalore(topic, c, obsParam);
		
		// Aggiungo il dispositivo nella stanza
		stanza.addDispositivo(d);

	}
	
	@Test
	@DisplayName("Azione pompa di calore")
	public void actionTest() {
		double prev_misura = stanza.getMisura(DispositivoParameter.TEMPERATURA.toString());
		
		d.changeSetpoint(25.0);
		d.action(prev_misura);
		
		assertTrue(prev_misura != stanza.getMisura(DispositivoParameter.TEMPERATURA.toString()));
		
	}
}

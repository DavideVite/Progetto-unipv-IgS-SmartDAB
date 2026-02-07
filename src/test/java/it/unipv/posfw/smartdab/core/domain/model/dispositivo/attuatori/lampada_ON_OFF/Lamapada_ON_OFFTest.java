package it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.lampada_ON_OFF;

import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.model.casa.Stanza;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.lampadaON_OFF.Lampada_Communicator;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.lampadaON_OFF.Lampada_ON_OFF;
import it.unipv.posfw.smartdab.core.domain.model.parametro.ObservableParameter;
import it.unipv.posfw.smartdab.core.service.DispositiviManager;
import it.unipv.posfw.smartdab.factory.EventBusFactory;
import it.unipv.posfw.smartdab.infrastructure.messaging.EventBus;
import it.unipv.posfw.smartdab.infrastructure.messaging.topic.Topic;

public class Lamapada_ON_OFFTest {
	private static Lampada_ON_OFF d;
	private static Lampada_Communicator c;
	private static Stanza stanza;
	private static ObservableParameter obsParam;

	@BeforeAll
	public static void initTest() {
		// Creo una Stanza per il Topic
		stanza = new Stanza("S01", "TestRoom", 20.0, LocalDateTime.now());
		DispositivoParameter param = DispositivoParameter.LUMINOSITA;

		// Creo il Topic
		Topic topic = Topic.createTopic("esempio1", stanza, param);

		// Creo ObservableParameter
		obsParam = new ObservableParameter(param);
		
		// Collego ObservableParameter alla stanza
		obsParam.addObserver(stanza);
		
		// Inserisco il nuovo parametro da gestire nella stanza
		stanza.updateParameter(obsParam.getParameterName().toString(), 0);
		
		// Creo una Lampada (implementazione concreta di Dispositivo)
		c = new Lampada_Communicator();
		d = new Lampada_ON_OFF(topic, c, obsParam, 3000);
		
		// Aggiungo il dispositivo nella stanza
		stanza.addDispositivo(d);

	}
	
	
	// Verifico solo la funzionalità action principale
	
	@Test
	@DisplayName("Lampada ON/OFF effettua correttamente action()")
	public void actionTest() {
		int current_illuminazione = d.getIlluminazione();
		double actual_state = stanza.getMisura(obsParam.toString());
		
		// Eseguo l'azione del dispositivo
		d.action(null);
		
		if(!d.isActive()) fail("La lampada non è stata commutata");
		if(current_illuminazione == d.getIlluminazione()) fail("La lampada non ha cambiato illuminazione");
		if(actual_state == stanza.getMisura(obsParam.toString())) fail("Parametro non modificato");
		
		// Eseguo un'altra azione
		actual_state = stanza.getMisura(obsParam.toString());
		current_illuminazione = d.getIlluminazione();

		d.action(null);
		
		if(d.isActive()) fail("La lampada non è stata commutata");
		if(current_illuminazione == d.getIlluminazione()) fail("La lampada non ha cambiato illuminazione");
		if(actual_state == stanza.getMisura(obsParam.toString())) fail("Parametro non modificato");
	}
	
}

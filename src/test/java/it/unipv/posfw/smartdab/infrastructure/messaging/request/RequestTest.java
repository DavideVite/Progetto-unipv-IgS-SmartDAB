package it.unipv.posfw.smartdab.infrastructure.messaging.request;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.model.casa.Stanza;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.lampadaON_OFF.Lampada_Communicator;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.lampadaON_OFF.Lampada_ON_OFF;
import it.unipv.posfw.smartdab.core.port.room.RoomPort;
import it.unipv.posfw.smartdab.infrastructure.messaging.topic.Topic;

public class RequestTest {
	private static Topic topic;
	private static Lampada_ON_OFF d;
	private static RoomPort r;
	private static DispositivoParameter p;
	private static String type;
	private static Object val;
	
	
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

}

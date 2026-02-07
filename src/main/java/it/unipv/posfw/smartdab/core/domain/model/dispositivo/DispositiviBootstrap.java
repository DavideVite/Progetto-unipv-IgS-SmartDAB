package it.unipv.posfw.smartdab.core.domain.model.dispositivo;

import java.util.ArrayList;

import it.unipv.posfw.smartdab.core.beans.DispositivoPOJO;
import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.model.casa.Stanza;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.lampadaON_OFF.Lampada_Communicator;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.lampadaON_OFF.Lampada_ON_OFF;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.pompa_di_calore.PompaDiCalore;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.pompa_di_calore.PompaDiCalore_Communicator;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.sensori.RilevatoreFumo.RilevatoreFumo;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.sensori.Termometro.Termometro;
import it.unipv.posfw.smartdab.core.domain.model.parametro.ObservableParameter;
import it.unipv.posfw.smartdab.core.port.communication.StandardCommunicator;
import it.unipv.posfw.smartdab.core.port.messaging.IEventBus_dispositiviAdder;
import it.unipv.posfw.smartdab.core.service.DispositiviManager;
import it.unipv.posfw.smartdab.infrastructure.messaging.topic.Topic;

public class DispositiviBootstrap {
	DispositiviManager dm;
	IEventBus_dispositiviAdder e;
	ArrayList<Dispositivo> basic_dispositivi = new ArrayList<>();
	
	
	public DispositiviBootstrap(DispositiviManager dm, IEventBus_dispositiviAdder e) {
		this.dm = dm;
		this.e = e;
	}
	
	public void removeAllDispositivi() {
		for(DispositivoPOJO d: dm.getDispositivi())
			dm.deleteDispositivo(d.getId());
	}
	
	public void initDispositiviDb(Stanza s) {
		Topic t1 = Topic.createTopic("D1", s, DispositivoParameter.LUMINOSITA);
		Topic t2 = Topic.createTopic("D2", s, DispositivoParameter.LUMINOSITA);
		Topic t3 = Topic.createTopic("D3", s, DispositivoParameter.TEMPERATURA);
		Topic t4 = Topic.createTopic("D4", s, DispositivoParameter.TEMPERATURA);
		Topic t5 = Topic.createTopic("D5", s, DispositivoParameter.FUMO);
		
		ObservableParameter o1 = new ObservableParameter(DispositivoParameter.LUMINOSITA);
		ObservableParameter o2 = new ObservableParameter(DispositivoParameter.TEMPERATURA);
		
		o1.addObserver(s);
		o2.addObserver(s);
		
		
		basic_dispositivi.add(new Lampada_ON_OFF(t1, new Lampada_Communicator(), o1, 5000));
		basic_dispositivi.add(new Lampada_ON_OFF(t2, new Lampada_Communicator(), o1, 3000));
		
		Termometro termo = new Termometro(t3, new StandardCommunicator());
		s.addObserver(termo);
		
		basic_dispositivi.add(termo);
		basic_dispositivi.add(new PompaDiCalore(t4, new PompaDiCalore_Communicator(), o2));
		basic_dispositivi.add(new RilevatoreFumo(t5, new StandardCommunicator()));
		
		for(Dispositivo d: basic_dispositivi) {
			e.addDispositivo(d);
			s.addDispositivo(d);
		}
	}
	
	
}

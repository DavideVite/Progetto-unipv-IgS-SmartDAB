package it.unipv.posfw.smartdab.core.domain.model.dispositivo.sensori.RilevatoreFumo;

import it.unipv.posfw.smartdab.adapter.facade.SensoreFacade;
import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.port.communication.StandardCommunicator;
import it.unipv.posfw.smartdab.core.port.communication.observer.Observable;
import it.unipv.posfw.smartdab.infrastructure.messaging.topic.Topic;

public class RilevatoreFumo extends SensoreFacade {
	
	private boolean smoke_detected;
	
	public RilevatoreFumo(Topic topic, StandardCommunicator c) {
		super(topic, c);
		
		c.setDispositivo(this);
		setSmoke_detected(false);
	}
	
	@Override
	public void update(Observable o, Object args) {
		if(args.toString().equals(DispositivoParameter.FUMO.getTrueLabel())) {
			setSmoke_detected(true);
			action();
		}
		else if(args.toString().equals(DispositivoParameter.FUMO.getFalseLabel())) 
			setSmoke_detected(false);
	}

	@Override
	public void publishMeasure() {
		this.getCommunicator().notifyObservers(smoke_detected);
	}

	@Override
	public int action() {
		publishMeasure();
		System.out.println("Attenzione: rilevato fumo nella stanza " + this.getTopic().getRoom());
		return 0;
	}

	public boolean isSmoke_detected() {
		return smoke_detected;
	}

	public void setSmoke_detected(boolean smoke_detected) {
		this.smoke_detected = smoke_detected;
	}

}

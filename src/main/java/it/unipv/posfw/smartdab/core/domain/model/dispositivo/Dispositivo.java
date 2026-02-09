package it.unipv.posfw.smartdab.core.domain.model.dispositivo;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoStates;
import it.unipv.posfw.smartdab.core.port.communication.ICommunicator;
import it.unipv.posfw.smartdab.core.port.device.DevicePort;
import it.unipv.posfw.smartdab.infrastructure.messaging.topic.Topic;

/** Classe astratta che contiene le informazioni di qualunque dispositivo 
 * @see Topic
 * @see ICommunicator
 * @see DispositivoStates
 * @author Alessandro Ingenito
 * @version 1.2
 */

public abstract class Dispositivo implements DevicePort {
	private Topic topic;	// home/room/dispositivo/parameter
	private boolean active;
	private DispositivoStates state;
	private ICommunicator c;

	/**
	 * 
	 * @param topic topic da associare al dispositivo
	 * @param c ai livelli di specializzazione inferiori è l'implementazione dello specifico communicator
	 * @param active rappresenta lo stato di attività del dispositivo
	 */
	
	public Dispositivo(Topic topic, ICommunicator c, boolean active) {
		this.topic = topic;
		this.c = c;
		this.active = active;
		state = DispositivoStates.ALIVE;
	}
	
	public Topic getTopic() {
		return topic;
	}
	
	public void setTopic(Topic topic) {
		this.topic = topic;
	}

	public DispositivoStates getState() {
		return state;
	}

	public void setState(DispositivoStates state) {
		this.state = state;
	}
	
	public ICommunicator getCommunicator() {
		return c;
	}
	
	public void setCommunicator(ICommunicator c) {
		if(c != null) this.c = c;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public void switchDispositivo() {
		active = !active;
	}
	
	@Override
	public String toString() {
		return topic.getId();
	}

	/**
	 * Questo metodo deve essere implementato da ogni dispositivo e si connette alla sua
	 * reale azione che deve eseguire
	 * 
	 * @param args argomenti che possono essere richiesti da un dispositivo
	 */
	
	public abstract int action(Object args);
}

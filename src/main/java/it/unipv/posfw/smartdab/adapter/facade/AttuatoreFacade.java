package it.unipv.posfw.smartdab.adapter.facade;

import it.unipv.posfw.smartdab.core.domain.model.dispositivo.Dispositivo;
import it.unipv.posfw.smartdab.core.domain.model.parametro.ObservableParameter;
import it.unipv.posfw.smartdab.core.port.communication.ICommunicator;
import it.unipv.posfw.smartdab.infrastructure.messaging.topic.Topic;


/**
 * Questa classe astratta è una facade ai vari dispositivi di tipo attuatore esistenti
 * @see Dispositivo
 * @see ObservableParameter
 * @see ICommunicator
 * @author Alessandro Ingenito
 * @version 1.1
 */

public abstract class AttuatoreFacade extends Dispositivo {
	private ObservableParameter parameter;
	
	/**
	 * 
	 * @param topic topic del dispositivo
	 * @param c communicator del dispositivo
	 * @param parameter parametro modificato dall'attuatore, osservato dalla stanza
	 */
	
	public AttuatoreFacade(Topic topic, ICommunicator c, ObservableParameter parameter) {
		super(topic, c, false);
		this.setParameter(parameter);
	}

	public ObservableParameter getParameter() {
		return parameter;
	}

	public void setParameter(ObservableParameter parameter) {
		this.parameter = parameter;
	}
	
	/**
	 * Questo metodo astratto deve essere implementato da ogni attuatore per definire il modo 
	 * in cui applica una variazione
	 * 
	 * @param state stato attuale della stanza osservata
	 * @return restituisce un intero che tipicamente se è 0 rappresenta un errore, se è 1 la corretta esecuzione
	 */
	public abstract int applyVariation(Object state);
}

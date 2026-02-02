package it.unipv.posfw.smartdab.adapter.facade;

import it.unipv.posfw.smartdab.core.domain.model.parametro.ObservableParameter;
import it.unipv.posfw.smartdab.core.port.communication.ICommunicator;
import it.unipv.posfw.smartdab.infrastructure.messaging.topic.Topic;

/**
 * Attuatore generico utilizzato come fallback quando non esiste
 * un'implementazione specifica per il parametro richiesto.
 * Aggiorna semplicemente il valore dell'ObservableParameter.
 */
public class GenericAttuatore extends AttuatoreFacade {

	public GenericAttuatore(Topic topic, ICommunicator c, ObservableParameter parameter) {
		super(topic, c, parameter);
	}

	@Override
	public int applyVariation(Object state) {
		try {
			double valore = (double) state;
			this.getParameter().setValue(valore);
			this.getParameter().notifyObservers(this);
			return 1;
		} catch (ClassCastException e) {
			System.out.println("Valore non valido per il parametro");
			return 0;
		}
	}

	@Override
	public int action(Object args) {
		return applyVariation(args);
	}
}

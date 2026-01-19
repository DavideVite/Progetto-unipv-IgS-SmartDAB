package it.unipv.posfw.smartdab.adapter.facade;

import it.unipv.posfw.smartdab.core.domain.model.dispositivo.Dispositivo;
import it.unipv.posfw.smartdab.core.domain.model.parametro.ObservableParameter;
import it.unipv.posfw.smartdab.core.port.communication.ICommunicator;


// Questa classe serve solo per dividere semanticamente sensori da attuatori
// non ha particolari logiche e implementazioni

public abstract class AttuatoreFacade extends Dispositivo {
	private ObservableParameter parameter;
	
	public AttuatoreFacade(String id, ICommunicator c, ObservableParameter parameter) {
		super(id, c, false);
		this.setParameter(parameter);
	}

	public ObservableParameter getParameter() {
		return parameter;
	}

	public void setParameter(ObservableParameter parameter) {
		this.parameter = parameter;
	}
	
	// Il modo in cui un attuatore applica la variazione dipende dalla particolare specializzazione
	public abstract int applyVariation(Object state);
}

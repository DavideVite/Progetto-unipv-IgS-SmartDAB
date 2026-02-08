package it.unipv.posfw.smartdab.adapter.facade;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.Dispositivo;
import it.unipv.posfw.smartdab.core.domain.model.parametro.ObservableParameter;
import it.unipv.posfw.smartdab.core.port.communication.ICommunicator;
import it.unipv.posfw.smartdab.infrastructure.messaging.topic.Topic;


// Questa classe serve solo per dividere semanticamente sensori da attuatori
// non ha particolari logiche e implementazioni

public abstract class AttuatoreFacade extends Dispositivo {
	private ObservableParameter parameter;

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

	// Il modo in cui un attuatore applica la variazione dipende dalla particolare specializzazione
	public abstract int applyVariation(Object state);

	// Controlla se l'attuatore supporta il parametro richiesto (Protected Variation / Demeter)
	public boolean supportaParametro(DispositivoParameter tipoParametro) {
		return this.parameter != null && this.parameter.getParameterName() == tipoParametro;
	}
}
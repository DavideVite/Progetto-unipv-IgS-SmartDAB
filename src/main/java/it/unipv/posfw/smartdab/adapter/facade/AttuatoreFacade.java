package it.unipv.posfw.smartdab.adapter.facade;

import it.unipv.posfw.smartdab.core.domain.model.dispositivo.Dispositivo;
import it.unipv.posfw.smartdab.core.domain.model.parametro.ObservableParameter;
import it.unipv.posfw.smartdab.core.port.communication.ICommunicator;

public class AttuatoreFacade extends Dispositivo {
	private double setpoint;
	private double variation;
	private ObservableParameter parameter;
	
	public AttuatoreFacade(String id, ICommunicator c, ObservableParameter parameter) {
		super(id, c, false);
		this.setParameter(parameter);
	}
	
	public int applyVariation(double state) {
		
		// Inserisco un controllo proporzionale per semplicità
		
		double error = setpoint - state;
		double new_state = Math.round(state + error * variation * 10) / 10;
		super.getCommunicator().notifyObservers(Double.toString(new_state));
		return 1;

	}

	public void applySetpoint(double setpoint) {
		this.setpoint = setpoint;
	}

	public double getSetpoint() {
		return setpoint;
	}

	public ObservableParameter getParameter() {
		return parameter;
	}

	public void setParameter(ObservableParameter parameter) {
		this.parameter = parameter;
	}

	//metodo inserito da Davide per identificare attuatori e sensori
	@Override
	public boolean isAttuatore() {
		return true;
	}
}

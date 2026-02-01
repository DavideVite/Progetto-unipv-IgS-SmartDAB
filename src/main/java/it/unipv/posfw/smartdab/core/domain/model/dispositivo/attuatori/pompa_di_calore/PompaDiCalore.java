package it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.pompa_di_calore;

import it.unipv.posfw.smartdab.adapter.facade.AttuatoreFacade;
import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.model.parametro.ObservableParameter;
import it.unipv.posfw.smartdab.infrastructure.messaging.topic.Topic;

public class PompaDiCalore extends AttuatoreFacade {

	private double stored_temp;
	private double temp_setpoint;
	
	public static final DispositivoParameter parameter = DispositivoParameter.TEMPERATURA;
	
	// Costante del controllo proporzionale
	public final double k = 0.2;
	
	public PompaDiCalore(Topic topic, PompaDiCalore_Communicator c, ObservableParameter o) {
		super(topic, c, o);
		c.setDispositivo(this);
	}
	
	@Override
	public int applyVariation(Object state) {
		try {
			double error = temp_setpoint - ((double) state);
			stored_temp = ((double) state) + k * error;


			this.getParameter().setValue(stored_temp);
			this.getParameter().notifyObservers(this);
			
			return 1;
			
		} catch(ClassCastException e) {
			System.out.println("Lo stato inserito non è coerente con la temperatura");
			return 0;
		}
	}

	@Override
	public int action(Object state) {
		if(state == null) return applyVariation(state);
		
		try {
			setTemp_setpoint((double) state);
		} catch(ClassCastException e) {
			System.out.println("Stato inserito non valido");
		}
		return 0;
	}

	public void setTemp_setpoint(double temp_setpoint) {
		this.temp_setpoint = temp_setpoint;
	}
	
	
}

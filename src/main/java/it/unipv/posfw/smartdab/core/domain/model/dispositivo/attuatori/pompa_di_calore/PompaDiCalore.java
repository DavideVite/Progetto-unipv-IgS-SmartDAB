package it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.pompa_di_calore;

import it.unipv.posfw.smartdab.adapter.facade.AttuatoreFacade;
import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.model.parametro.ObservableParameter;
import it.unipv.posfw.smartdab.core.port.device.AttuatorePort;
import it.unipv.posfw.smartdab.infrastructure.messaging.topic.Topic;

/**
 * Implementazione di un dispositivo di tipo attuatore con logica proporzionale
 * @see Dispositivo
 * @see AttuatoreFacade
 * @see AttuatorePort
 * @see PompaDiCalore_Communicator
 * @see ObservableParameter
 * @author Alessandro Ingenito
 * @version 1.0
 */

public class PompaDiCalore extends AttuatoreFacade implements AttuatorePort {

	private double stored_temp;
	private double setpoint;
	
	public static final DispositivoParameter parameter = DispositivoParameter.TEMPERATURA;
	
	// Costante del controllo proporzionale
	public final double k = 0.2;
	
	public PompaDiCalore(Topic topic, PompaDiCalore_Communicator c, ObservableParameter o) {
		super(topic, c, o);
		c.setDispositivo(this);
	}
	/**
	 * La variazione applicata dalla pompa di calore è gestita con logica proporzionale
	 * Vout = (Setpoint - Actual_state) * k 
	 */
	
	@Override
	public int applyVariation(Object state) {
		try {
			System.out.println(state.toString() + " QUA");
			if(((double) state <= setpoint + 3 && (double) state >= setpoint - 3)) return 1;
			double error = setpoint - ((double) state);
			stored_temp = Math.floor(((double) state + k * error) * 10) / 10;
			
			this.getParameter().setValue(stored_temp);
			this.getParameter().notifyObservers(this);
			
			System.out.println(stored_temp + " Nuova temp");
			return 1;
			
		} catch(ClassCastException e) {
			System.out.println("Lo stato inserito non è coerente con la temperatura");
			return 0;
		}
	}

	/**
	 * L'azione della pompa di calore consiste solo nell'applicare la variazione
	 */
	
	@Override
	public int action(Object state) {
		return applyVariation(state);
	}
	
	/**
	 * Consente di impostare l'obiettivo di temperatura della pompa di calore
	 */
	public void changeSetpoint(double setpoint) {
		this.setpoint = setpoint;
	}

	public double getSetpoint() {
		return setpoint;
	}
	
	
	
}

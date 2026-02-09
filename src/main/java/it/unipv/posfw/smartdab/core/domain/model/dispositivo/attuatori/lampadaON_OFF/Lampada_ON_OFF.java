package it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.lampadaON_OFF;

import it.unipv.posfw.smartdab.adapter.facade.AttuatoreFacade;
// FIX: Rimosso import duplicato di DispositivoParameter
import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.model.parametro.ObservableParameter;
import it.unipv.posfw.smartdab.infrastructure.messaging.topic.Topic;

/**
 * Implementazione di un dispositivo di tipo attuatore con logica ON/OFF
 * @see Dispositivo
 * @see AttuatoreFacade
 * @see Lampada_Communicator
 * @see ObservableParameter
 * @author Alessandro Ingenito
 * @version 1.1
 */

public class Lampada_ON_OFF extends AttuatoreFacade {
	
	private int illuminazione;
	private int intensita; // u.m. lumen
	public static final DispositivoParameter parameter = DispositivoParameter.LUMINOSITA;
	public final int MAX_INTENSITA = 5000; 
	
	public Lampada_ON_OFF(Topic topic, Lampada_Communicator c, ObservableParameter o, int intensita) {
		super(topic, c, o);
		
		if(intensita <= MAX_INTENSITA) this.intensita = intensita;
		else {
			System.out.println("L'intensità inserita non è corretta");
			intensita = 0;
		}
		
		illuminazione = 0;
		c.setDevicePort(this);
	}

	public int getIntensita() {
		return intensita;
	}

	public void setIntensita(int intensita) {
		if(intensita <= MAX_INTENSITA && intensita >= 0) this.intensita = intensita;
		else System.out.println("L'intensità inserita non è valida: 0 <= val <= 5000 lumen");
	}

	public int getIlluminazione() {
		return illuminazione;
	}
	
	/**
	 * Questo metodo effettua l'override sul metodo swicthDispositivo() della super classe 
	 * Dispositivo, in quanto è parte del processo di attuazione di Lampada_ON_OFF.
	 * Spegnere o accendere il dispositivo significa applicare la variazione.
	 */
	
	@Override
	public void switchDispositivo() {
		super.switchDispositivo();
		super.getCommunicator().notifyObservers(this.isActive());
		illuminazione = intensita * (this.isActive() ? 1 : 0);
	}
	
	/**
	 * Per Lampada_ON_OFF applicare una variazione significa solo modificare l'ObservableParameter
	 */
	
	@Override
	public int applyVariation(Object state) {
		try {
			
			this.getParameter().setValue((int)state);
			this.getParameter().notifyObservers(this);
			
		} catch(ClassCastException e) {
			e.printStackTrace();
			return 0;
		}
		
		return 1;
	}
	
	/*
	 * L'azione di Lampada_ON_OFF è switchDispositivo() + applyVariation() degli attuatori
	 */
	
	@Override
	public int action(Object args) {
		switchDispositivo();
		return applyVariation(illuminazione);
	}
	
	
	
	
}

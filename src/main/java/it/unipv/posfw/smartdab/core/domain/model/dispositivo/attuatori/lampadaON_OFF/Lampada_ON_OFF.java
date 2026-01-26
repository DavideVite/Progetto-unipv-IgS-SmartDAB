package it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.lampadaON_OFF;

import it.unipv.posfw.smartdab.adapter.facade.AttuatoreFacade;
import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.model.parametro.ObservableParameter;
import it.unipv.posfw.smartdab.infrastructure.messaging.topic.Topic;

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
	
	// Parte di attuazione
	@Override
	public void switchDispositivo() {
		super.switchDispositivo();
		super.getCommunicator().notifyObservers(this.isActive());
		illuminazione = intensita * (this.isActive() ? 1 : 0);
		applyVariation(illuminazione);
	}
	
	// Devo implementare controllo ON/OFF
	
	@Override
	public int applyVariation(Object state) {
		try {
			
			super.getParameter().setValue((int)state);
			super.getParameter().notifyObservers(this);
			
		} catch(ClassCastException e) {
			e.printStackTrace();
			return 0;
		}
		
		return 1;
	}

	@Override
	public int action() {
		switchDispositivo();
		return applyVariation(illuminazione);
	}
	
	
	
	
}

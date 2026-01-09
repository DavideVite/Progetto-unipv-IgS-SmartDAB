package main.java.it.unipv.posfw.smartdab.src.core.domain.model.dispositivo.attuatori.lampadaON_OFF;

import main.java.it.unipv.posfw.smartdab.src.adapter.facade.AttuatoreFacade;
import main.java.it.unipv.posfw.smartdab.src.core.domain.model.parametro.ObservableParameter;

public class Lampada_ON_OFF extends AttuatoreFacade {
	
	private int illuminazione;
	private int intensita; // u.m. lumen
	public final int MAX_INTENSITA = 5000; 
	
	public Lampada_ON_OFF(String id, Lampada_CommandDispatcher c, ObservableParameter parameter, int intensita) {
		super(id, c, parameter);
		this.intensita = intensita;
		illuminazione = 0;
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

	@Override
	public void switchDispositivo() {
		super.switchDispositivo();
		illuminazione = intensita * (this.isActive() ? 1 : 0);
	}
	
	
	
	
}

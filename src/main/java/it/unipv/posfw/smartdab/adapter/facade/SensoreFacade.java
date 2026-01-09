package it.unipv.posfw.smartdab.adapter.facade;

import it.unipv.posfw.smartdab.core.domain.model.dispositivo.Dispositivo;
import it.unipv.posfw.smartdab.core.port.communication.ICommunicator;
import it.unipv.posfw.smartdab.core.port.communication.observer.Observable;
import it.unipv.posfw.smartdab.core.port.communication.observer.Observer;


public class SensoreFacade extends Dispositivo implements Observer{
	private double error;
	private double noiseSensitivity;
	private double measure;
	
	public SensoreFacade(String id, ICommunicator c) {
		super(id, c, true);
		error = 0;			// Ipotizzo sensore ideale
		noiseSensitivity = 2;
	}

	public double getError() {
		return error;
	}

	public void setError(double error) {
		if(error >= 0) this.error = error;
	}

	public double getMeasure() {
		return measure;
	}

	public void setMeasure(double measure) {
		this.measure = measure;
	}
	
	public void publishMeasure() {
		String payload = Double.toString(measure);
		super.getCommunicator().notifyObservers(payload);
	}
	
	@Override
	public void update(Observable room, Object arg) {
		try {
			// Estraggo la misura reale fornita dalla stanza
			double real_measure = (double)arg;
			
			// Simulo un errore di misura
			double noise = (Math.random() * noiseSensitivity - noiseSensitivity / 2) * error * real_measure;
			setMeasure(Math.round((real_measure + noise) * 10) / 10);
			
			publishMeasure();
		}
		catch(ClassCastException e) {
			e.printStackTrace();
		}
	}

	public double getNoise_sensitivity() {
		return noiseSensitivity;
	}

	public void setNoise_sensitivity(double noise_sensitivity) {
		this.noiseSensitivity = noise_sensitivity;
	}
	
	
}

package it.unipv.posfw.smartdab.core.port.communication;

import java.util.ArrayList;
import java.util.Iterator;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoStates;
import it.unipv.posfw.smartdab.core.domain.enums.Message;
import it.unipv.posfw.smartdab.core.port.device.DevicePort;
import it.unipv.posfw.smartdab.infrastructure.messaging.request.Request;
import it.unipv.posfw.smartdab.infrastructure.messaging.DispositiviObserver;

public class StandardCommunicator implements ICommunicator {

	private DevicePort dispositivo;
	private ArrayList<DispositiviObserver> observers = new ArrayList<>();
	
	public StandardCommunicator() {

	}
	
	public void setDispositivo(DevicePort dispositivo) {
		if(dispositivo != null) this.dispositivo = dispositivo;
		else System.out.println("Dispositivo inserito non corretto");
	}

	@Override
	public Message processRequest(Request request) {
		if (!dispositivo.getState().toString().equals(DispositivoStates.ALIVE.toString())) {
			return Message.ERROR;
		}

		// Esegui l'azione sul dispositivo con il valore della request
		try {
			dispositivo.action(request.getVal());
		} catch (Exception e) {
			System.out.println("StandardCommunicator: errore durante action - " + e.getMessage());
			return Message.ERROR;
		}
		return Message.ACK;
	}

	@Override
	public void addObserver(DispositiviObserver observer) {
		if(observer != null) observers.add(observer);
		else System.out.println("Osservatore non valido");
	}

	@Override
	public void removeObserver(DispositiviObserver observer) {
		if(observers.remove(observer)) System.out.println("Osservatore " + observer.toString() + " è stato rimosso");
		else System.out.println("Osservatore " + observer.toString() + " non presente nella lista");
	}

	@Override
	public void notifyObservers(Object args) {
		Request request = Request.createRequest(dispositivo.getTopic(), Message.UPDATE + "." + Message.PARAMETER, args);
		
		if(request != null) {
			Iterator<DispositiviObserver> iter = observers.iterator();

			while(iter.hasNext()) {
				iter.next().update(request);
			}
		}
		
		else System.out.println("Richiesta non istanziata correttamente");
	}

}

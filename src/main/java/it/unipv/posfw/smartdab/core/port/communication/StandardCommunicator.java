package it.unipv.posfw.smartdab.core.port.communication;

import java.util.ArrayList;
import java.util.Iterator;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoStates;
import it.unipv.posfw.smartdab.core.domain.enums.Message;
import it.unipv.posfw.smartdab.core.port.communication.observer.Observer;
import it.unipv.posfw.smartdab.core.port.device.DevicePort;
import it.unipv.posfw.smartdab.infrastructure.messaging.request.Request;

public class StandardCommunicator implements ICommunicator {

	private DevicePort dispositivo;
	private ArrayList<Observer> observers = new ArrayList<>();
	
	public StandardCommunicator() {

	}
	
	public void setDispositivo(DevicePort dispositivo) {
		if(dispositivo != null) this.dispositivo = dispositivo;
		else System.out.println("Dispositivo inserito non corretto");
	}

	@Override
	public Message processRequest(Request request) {
		if(dispositivo.getState().toString().equals(DispositivoStates.ALIVE.toString())) 
			return Message.ACK;
		
		return Message.ERROR;
	}

	@Override
	public void addObserver(Observer observer) {
		if(observer != null) observers.add(observer);
		else System.out.println("Osservatore non valido");
	}

	@Override
	public void removeObserver(Observer observer) {
		if(observers.remove(observer)) System.out.println("Osservatore " + observer.toString() + " è stato rimosso");
		else System.out.println("Osservatore " + observer.toString() + " non presente nella lista");
	}

	@Override
	public void notifyObservers(Object args) {
		Request request = Request.createRequest(dispositivo.getTopic(), Message.UPDATE + "." + Message.PARAMETER, String.valueOf(args));
		
		if(request != null) {
			Iterator<Observer> iter = observers.iterator();

			while(iter.hasNext()) {
				iter.next().update(this, request);
			}
		}
		
		else System.out.println("Richiesta non istanziata correttamente");
	}

}

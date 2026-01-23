package it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.lampadaON_OFF;

import java.util.ArrayList;
import java.util.Iterator;

import it.unipv.posfw.smartdab.core.domain.enums.Message;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.dispatcher.CommandDispatcher;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.dispatcher.IDispatcherBootstrap;
import it.unipv.posfw.smartdab.core.port.communication.ICommunicator;
import it.unipv.posfw.smartdab.core.port.communication.observer.Observer;
import it.unipv.posfw.smartdab.core.port.device.DevicePort;
import it.unipv.posfw.smartdab.infrastructure.messaging.request.Request;

public class Lampada_Communicator implements ICommunicator {

	private DevicePort dispositivo;
	private ArrayList<Observer> observers = new ArrayList<>();
	private CommandDispatcher dispatcher;
	
	
	public Lampada_Communicator() {
		Lampada_DispatcherBootstrap boot = new Lampada_DispatcherBootstrap();
		dispatcher = boot.createDispatcher();
	}
	
	@Override
	public Message processRequest(Request request) {
		// Ricavo il comnando ed eseguo
		try {
			dispatcher.dispatch(request, dispositivo);
		} catch(Exception e) {
			e.printStackTrace();
			return Message.ERROR;
		}
		
		return Message.ACK;
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
		try {
			
			// L'unico motivo per cui lampada notifica è un cambio di stato
			Request request = Request.createRequest(dispositivo.getTopic(), Message.STATE + "", String.valueOf(args));
			
			Iterator<Observer> iter = observers.iterator();
			
			while(iter.hasNext()) {
				iter.next().update(this, request);
			}
			
		} catch(ClassCastException e) {
			System.out.println("L'argomento inserito non è valido");
		}
		
	}
	
	protected void setDevicePort(DevicePort d) {
		if(d != null) dispositivo = d;
		else System.out.println("DevicePort non impostata");
	}
}

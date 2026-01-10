package main.java.it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.lampadaON_OFF;

import java.util.ArrayList;
import java.util.Iterator;

import main.java.it.unipv.posfw.smartdab.core.domain.enums.Message;
import main.java.it.unipv.posfw.smartdab.core.domain.model.dispositivo.dispatcher.CommandDispatcher;
import main.java.it.unipv.posfw.smartdab.core.domain.model.dispositivo.dispatcher.IDispatcherBootstrap;
import main.java.it.unipv.posfw.smartdab.core.port.communication.ICommunicator;
import main.java.it.unipv.posfw.smartdab.core.port.communication.observer.Observer;
import main.java.it.unipv.posfw.smartdab.core.port.device.DevicePort;
import main.java.it.unipv.posfw.smartdab.infrastructure.messaging.request.Request;

public class Lampada_Communicator implements ICommunicator {

	private DevicePort dispositivo;
	private ArrayList<Observer> observers = new ArrayList<>();
	private CommandDispatcher dispatcher;
	
	
	public Lampada_Communicator(IDispatcherBootstrap boot) {
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
		observers.add(observer);
	}
	
	@Override
	public void removeObserver(Observer observer) {
		observers.remove(observer);
	}
	
	@Override
	public void notifyObservers(Object args) {
		try {
			int payload = (int)args;
			if(payload > 0 && payload <= 5000) {
				
			}
			Iterator<Observer> iter = observers.iterator();
			
			while(iter.hasNext()) {
				iter.next().update(this, payload);
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

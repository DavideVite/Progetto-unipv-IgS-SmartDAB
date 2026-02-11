package it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.lampadaON_OFF;

import java.util.ArrayList;
import java.util.Iterator;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.enums.DispositivoStates;
import it.unipv.posfw.smartdab.core.domain.enums.Message;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.dispatcher.CommandDispatcher;
import it.unipv.posfw.smartdab.core.port.communication.ICommunicator;
import it.unipv.posfw.smartdab.core.port.device.DevicePort;
import it.unipv.posfw.smartdab.infrastructure.messaging.DispositiviObserver;
import it.unipv.posfw.smartdab.infrastructure.messaging.request.Request;

/**
 * Implementazione di un comunicatore specifico per Lampada_ON_OFF
 * @see Lampada_ON_OFF
 * @see DevicePort
 * @see DispositiviObserver
 * @see CommandDispatcher
 * @author Alessandro Ingenito
 * @version 1.0
 */

public class Lampada_Communicator implements ICommunicator {

	private DevicePort dispositivo;
	private ArrayList<DispositiviObserver> observers = new ArrayList<>();
	private CommandDispatcher dispatcher;
	
	/**
	 * Istanziare una pompa di calore, istanzia internamente anche il suo command dispatcher 
	 * specifico
	 */
	
	public Lampada_Communicator() {
		Lampada_DispatcherBootstrap boot = new Lampada_DispatcherBootstrap();
		dispatcher = boot.createDispatcher();
	}
	
	/**
	 * Implementazione di un metodo per il processing della richiesta e individuazione del comando
	 * 
	 * @param request richiesta ricevuta dal communicator da processare
	 * @return restituisce un messaggio che esplicita l'esito del processing
	 */
	
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
	public void addObserver(DispositiviObserver observer) {
		if(observer != null) observers.add(observer);
		else System.out.println("Osservatore non valido");
	}
	
	@Override
	public void removeObserver(DispositiviObserver observer) {
		if(observers.remove(observer)) System.out.println("Osservatore " + observer.toString() + " è stato rimosso");
		else System.out.println("Osservatore " + observer.toString() + " non presente nella lista");
	}
	
	/**
	 * Questo metodo notifica tutti gli observer dello stato del dispositivo (ON/OFF)
	 */
	
	@Override
	public void notifyObservers(Object args) {

		// L'unico motivo per cui lampada notifica è un cambio di stato (ON/OFF)
		Request request = Request.createRequest(dispositivo.getTopic(), 
												dispositivo.isActive() ? Message.ONLINE.toString() : Message.OFFLINE.toString(), 
												String.valueOf(args));
		
		if(request != null) {
			Iterator<DispositiviObserver> iter = observers.iterator();

			while(iter.hasNext()) {
				iter.next().update(request);
			}
		}
		else System.out.println("Richiesta non istanziata correttamente");
	}
	
	/**
	 * Questo metodo consente di impostare l'entry point del communicator al dispositivo che 
	 * lo contiene (solo a Lampada_ON_OFF)
	 * @param d interfaccia al dispositivo che possiede il communicator
	 */
	protected void setDevicePort(DevicePort d) {
		if(d != null) dispositivo = d;
		else System.out.println("DevicePort non impostata");
	}
}

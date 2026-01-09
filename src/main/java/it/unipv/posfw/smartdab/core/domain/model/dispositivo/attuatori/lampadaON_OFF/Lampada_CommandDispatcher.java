package it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.lampadaON_OFF;

import java.util.ArrayList;
import java.util.Iterator;

import it.unipv.posfw.smartdab.adapter.interfaces.DispatcherAdapter;
import it.unipv.posfw.smartdab.core.domain.enums.Message;
import it.unipv.posfw.smartdab.core.port.communication.observer.Observer;
import it.unipv.posfw.smartdab.core.port.device.DevicePort;

/* I dispositivi implementati qui sono quelli strutturati in funzione del sistema
 * SmartDAB, dunque forniscono direttamente la logica implementando l'interfaccia
 * DispatcherAdapter.
 * Un dispositivo proveniente da un produttore generico avrà una sua classe che 
 * effettua determinate operazioni sul dispositivo ed è poi compito del produttore, 
 * o comunque del tecnico che adatta il dispositivo al sistema, fornire una classe
 * che implementa l'adapter e che adatta quella del produttore al sistema.
 */

public class Lampada_CommandDispatcher implements DispatcherAdapter {

	private DevicePort dispositivo;
	private ArrayList<Observer> observers = new ArrayList<>();
	

	
	// Formato tipico del payload: value (in questo caso int)
	@Override
	public void notifyObservers(Object args) {
		try {
			int payload = (int)args;
			if(payload > 0 && payload <= 900) {
				
			}
			Iterator<Observer> iter = observers.iterator();
			
			while(iter.hasNext()) {
				iter.next().update(this, payload);
			}
			
		} catch(ClassCastException e) {
			System.out.println("L'argomento inserito non è valido");
		}
	}
	
	/* Formato delle richieste:
	 * - CONFIG.SETPOINT.val
	 * - CONFIG.STATE.val
	 */
	@Override
	public Message processRequest(String request, Object val) {
		// TEMPORANEAMENTE STUB PER COMPILAZIONE
		// Implementazione corretta da fare con due parametri
		return Message.ACK;
	}

	/* IMPLEMENTAZIONE ORIGINALE COMMENTATA - Firma errata (mancava parametro Object val)
	@Override
	public Message processRequest(String request) {

		return Message.ACK;
	}
	*/

	@Override
	public boolean dispatchCommand(String command) {
		// TODO Auto-generated method stub
		return true;
	}

	public void setDispositivo(DevicePort dispositivo) {
		this.dispositivo = dispositivo;
	}

	public DevicePort getDispositivo() {
		return dispositivo;
	}

	@Override
	public void addObserver(Observer observer) {
		observers.add(observer);
	}

	@Override
	public void removeObserver(Observer observer) {
		observers.remove(observer);
	}


}

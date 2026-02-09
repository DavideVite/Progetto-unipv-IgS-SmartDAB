package it.unipv.posfw.smartdab.core.domain.model.parametro;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import it.unipv.posfw.smartdab.adapter.facade.AttuatoreFacade;
import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.port.communication.observer.Observable;
import it.unipv.posfw.smartdab.core.port.communication.observer.Observer;
import it.unipv.posfw.smartdab.core.port.room.RoomPort;

/**
 * Classe che rappresenta un parametro osservato dalle stanze e modificato dagli attuatori incaricati
 * @see Observable
 * @see Stanza
 * @see DispositivoParameter
 * @author Alessandro Ingenito
 * @version 1.1
 */

public class ObservableParameter implements Observable {
	
	private List<Observer> rooms = new ArrayList<>();
	private DispositivoParameter parameterName;
	private double value;
	
	public ObservableParameter(DispositivoParameter name) {
		parameterName = name;
	}
	
	@Override
	public void addObserver(Observer observer) {
		rooms.add(observer);
	}

	@Override
	public void removeObserver(Observer observer) {
		rooms.remove(observer);
	}

	/**
	 * Modifica il parametro della stanza in cui è presente l'attuatore che chiama il metodo
	 */
	@Override
	public void notifyObservers(Object args) {
		
		// Questa funzionalità deve essere chiamata solo dagli attuatori

		try {
			AttuatoreFacade attuatore = (AttuatoreFacade)args;
			
			
			if(attuatore.getTopic().getParameter().equals(parameterName)) {
				Iterator<Observer> roomsIterator = rooms.iterator();
				RoomPort room;
			
				while(roomsIterator.hasNext()) {
					room = (RoomPort) roomsIterator.next();
					if(room.getDispositivi().contains(attuatore)) room.update(this, value);
				}
			}
			
		} catch(ClassCastException e) {
			System.out.println("Errore: il dispositivo chiamante non è un attuatore");
		}
		

	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public DispositivoParameter getParameterName() {
		return parameterName;
	}

	public void setParameterName(DispositivoParameter parameterName) {
		if(parameterName != null) this.parameterName = parameterName;
		else System.out.println("Nome del parametro non valido");
	}

	public List<Observer> getRooms() {
		return rooms;
	}

	public void setRooms(List<Observer> rooms) {
		this.rooms = rooms;
	}

	@Override
	public String toString() {
		return parameterName.toString();
	}
	
	
}

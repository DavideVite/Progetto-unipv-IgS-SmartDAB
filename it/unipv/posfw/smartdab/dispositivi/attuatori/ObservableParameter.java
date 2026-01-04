package it.unipv.posfw.smartdab.dispositivi.attuatori;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import it.unipv.posfw.smartdab.dispositivi.facade.AttuatoreFacade;
import it.unipv.posfw.smartdab.interfaces.Observable;
import it.unipv.posfw.smartdab.interfaces.Observer;

public class ObservableParameter implements Observable {
	
	private List<Observer> rooms = new ArrayList<>();
	private String parameterName;
	private double value;
	
	@Override
	public void addObserver(Observer observer) {
		rooms.add(observer);
	}

	@Override
	public void removeObserver(Observer observer) {
		rooms.remove(observer);
	}

	@Override
	public void notifyObservers(Object args) {
		
		// Questa funzionalità deve essere chiamata solo dagli attuatori

		try {
			AttuatoreFacade attuatore = (AttuatoreFacade)args;
			
			if(attuatore.getTopic().endsWith(parameterName)) {
				Iterator<Observer> roomsIterator = rooms.iterator();
			
				while(roomsIterator.hasNext()) {
				
					// se l'attuatore è nella stanza...
					roomsIterator.next().update(this, value);
				}
			}
			
		} catch(ClassCastException e) {
			e.printStackTrace();
		}
		

	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public List<Observer> getRooms() {
		return rooms;
	}

	public void setRooms(List<Observer> rooms) {
		this.rooms = rooms;
	}
}

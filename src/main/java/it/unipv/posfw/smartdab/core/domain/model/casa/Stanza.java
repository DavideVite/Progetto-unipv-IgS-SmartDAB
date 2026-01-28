package it.unipv.posfw.smartdab.core.domain.model.casa;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.Dispositivo;
import it.unipv.posfw.smartdab.core.domain.model.parametro.ObservableParameter;
import it.unipv.posfw.smartdab.core.port.communication.observer.Observable;
import it.unipv.posfw.smartdab.core.port.communication.observer.Observer;
import it.unipv.posfw.smartdab.core.port.room.RoomPort;

/**
 * FIX: Aggiunto RoomPort all'elenco delle interfacce implementate.
 * Stanza gia' aveva i metodi getId() e getDispositivi() richiesti da RoomPort.
 * Questo permette di usare Stanza come parametro in Topic.createTopic().
 */
public class Stanza implements Observable, Observer, RoomPort {
     private static int counter = 0;  //TODO verificare se torna a zero
	 private String id;
	 private String nome;
	 private double mq;
	 private List<Dispositivo> dispositivi = new ArrayList<>();
	 private Map<String, Double> parametri = new HashMap<>();
	 private List<Observer> observers = new ArrayList<>(); 

	 public Stanza(String id, String nome, double mq) {
		 counter++;
		 this.id = "S" + counter;
		 this.nome = nome;	
		 this.mq = mq;	  
	 }

	 public String getId() {
		 return id;
	 }
	 
	 public double getMq() {
		 return mq;
	 }
	 
	 public void setMq(double mq) {
		 this.mq=mq;
	 }

	 public String getNome() {
		 return nome;
	 }

	 public void setNome(String nome) {
		 this.nome = nome;
	 }

	 public List<Dispositivo> getDispositivi() {
		 return dispositivi;
	 }

	 public Map<String, Double> getParametri() {
		 return parametri;
	 }

	 public Double getMisura(String parametro) {
		 return parametri.get(parametro);
	 }

	 public boolean isEmpty() {
		 if (dispositivi == null || dispositivi.isEmpty()) {
				 return true;  
	     }
	     return false;
	 }

	 // CORREZIONE: Rimosso metodo addDispositivo duplicato (era presente due volte)
	 public void addDispositivo(Dispositivo d) {
		 if (d != null) {
			 this.dispositivi.add(d);
		 }
	 }

	public void removeDispositivo(Dispositivo d) {
		if (d != null) {
			this.dispositivi.remove(d);
		}
	}

	public void updateParameter(String nomeParametro, double nuovoValore) {
		this.parametri.put(nomeParametro, nuovoValore);

		notifyObservers(nomeParametro);
	}

	@Override
	public void addObserver(Observer observer) {
		observers.add(observer);
	}

	@Override
	public void removeObserver(Observer observer) {
		observers.remove(observer);	     
	}

	// CORREZIONE: Rimosso codice duplicato di notifyObservers (era presente due volte)
	// CORREZIONE: Sostituito riferimento a SensoreFacade con chiamata generica a Observer
	@Override
	public void notifyObservers(Object args) {
		for (Observer o : observers) {
			o.update(this, args);
		}
	}

	// CORREZIONE: Rimosso metodo update incompleto e duplicato
	// CORREZIONE: Mantenuta solo la versione completa che usa DispositivoParameter
	@Override
	public void update(Observable o, Object arg) {
		ObservableParameter obsParam = (ObservableParameter) o;

		DispositivoParameter paramEnum = obsParam.getParameterName();
		String nomeStr = paramEnum.name();
		double valore = obsParam.getValue();

		this.updateParameter(nomeStr, valore);
	}
}



package it.unipv.posfw.smartdab.core.domain.model.casa;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unipv.posfw.smartdab.adapter.facade.AttuatoreFacade;
import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.Dispositivo;
import it.unipv.posfw.smartdab.core.domain.model.parametro.ObservableParameter;
import it.unipv.posfw.smartdab.core.port.communication.observer.Observable;
import it.unipv.posfw.smartdab.core.port.communication.observer.Observer;
import it.unipv.posfw.smartdab.core.port.room.RoomPort;
import it.unipv.posfw.smartdab.core.service.strategy.ParameterSyncFunction;

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
	 private LocalDateTime createdAt;
	 private List<Dispositivo> dispositivi = new ArrayList<>();
	 private Map<String, Double> parametri = new HashMap<>();
	 private Map<String, Double> parametriTarget = new HashMap<>();
	 private ParameterSyncFunction syncFunction = (current, target) -> target;
	 private List<Observer> observers = new ArrayList<>();
	 
	 //costruttore per nuove stanze
	 public Stanza(String nome, double mq) {
		 counter++;
		 this.id = "S" + counter;
		 this.nome = nome;	
		 this.mq = mq;
		 this.createdAt = LocalDateTime.now();
	 }
	 
	 //costruttore per il DAO
	 public Stanza(String id, String nome, double mq, LocalDateTime createdAt) {
		 this.id = id; //prende ID del database
		 this.nome = nome;
		 this.mq = mq;
		 this.createdAt = createdAt;
		 
		 try {
		        // Estrae il numero dall'ID (es. da "S3" estrae 3)
		        int idNumerico = Integer.parseInt(id.substring(1));
		        
		        // Se l'ID che arriva dal DB è più grande del counter attuale, aggiorna il counter
		        if (idNumerico > counter) {
		            counter = idNumerico;
		        }
		    } catch (Exception e) {
		    	
		    }
		}
	 
	 public LocalDateTime getCreatedAt() { 
		 return createdAt;
	 }
	 
	 public static void setCounter(int value) {
		 counter = value;
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

	 /**
	  * Restituisce solo gli attuatori presenti nella stanza.
	  * Evita l'uso di instanceof nel codice chiamante, spostando
	  * la responsabilita' del filtraggio nell'entita' che possiede i dati.
	  */
	 public List<AttuatoreFacade> getAttuatori() {
		 List<AttuatoreFacade> attuatori = new ArrayList<>();
		 for (Dispositivo d : dispositivi) {
			 if (d instanceof AttuatoreFacade) {
				 attuatori.add((AttuatoreFacade) d);
			 }
		 }
		 return attuatori;
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

	public void updateTarget(String nomeParametro, double valoreTarget) {
		this.parametriTarget.put(nomeParametro, valoreTarget);
		double current = this.parametri.getOrDefault(nomeParametro, valoreTarget);
		double nuovoValore = syncFunction.apply(current, valoreTarget);
		this.parametri.put(nomeParametro, nuovoValore);
		notifyObservers(nomeParametro);
	}

	public Map<String, Double> getParametriTarget() {
		return parametriTarget;
	}

	public void setSyncFunction(ParameterSyncFunction syncFunction) {
		this.syncFunction = syncFunction;
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
         for (Observer o : observers) {
        		 o.update(this, args);
        	 }
         }

     @Override
     public void update(Observable o, Object arg) {
         ObservableParameter obsParam = (ObservableParameter) o;

		DispositivoParameter paramEnum = obsParam.getParameterName();
		String nomeStr = paramEnum.name();
		double valore = obsParam.getValue();

		this.updateParameter(nomeStr, valore);
	}
}



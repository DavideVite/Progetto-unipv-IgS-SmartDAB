package it.unipv.posfw.smartdab.core.domain.model.casa;


import java.time.LocalDateTime;
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
     private static int counter = 0;
	 private String id;
	 private String nome;
	 private double mq;
	 private LocalDateTime createdAt;
	 private List<Dispositivo> dispositivi = new ArrayList<>();
	 private Map<String, Double> parametri = new HashMap<>();
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



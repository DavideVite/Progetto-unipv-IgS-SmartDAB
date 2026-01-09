package src.main.java.it.unipv.posfw.smartdab.src.core.domain.model.casa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import src.main.java.it.unipv.posfw.smartdab.src.core.domain.model.dispositivo.Dispositivo;
import src.main.java.it.unipv.posfw.smartdab.src.core.domain.model.parametro.ObservableParameter;
import src.main.java.it.unipv.posfw.smartdab.src.core.port.communication.observer.Observable;
import src.main.java.it.unipv.posfw.smartdab.src.core.port.communication.observer.Observer;

public class Stanza implements Observable, Observer{
	 private String id;
	 private String nome;
	 private List<Dispositivo> dispositivi = new ArrayList<>();
	 private Map<String, Double> parametri = new HashMap<>();
	 private List<Observer> observers = new ArrayList<>(); 
	 
	 public Stanza(String id, String nome) {
		 this.id = id;
		 this.nome = nome;	 
	 }
	 
	 public String getId() {
		 return id;
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

	 public Double getMisura(String p) {
		 return parametri.get(p);
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
			 
			 notifyObservers("dispositivo_aggiunto");
		 }
	 }
	 
	 public void removeDispositivo(Dispositivo d) {
		 if (d != null) {
			 this.dispositivi.remove(d);
			 
			 notifyObservers("dispositivo_rimosso");
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
         for(Observer observer : observers) {
        	 observer.update(this, args);
         }
         }
     
     @Override
     public void update(Observable o, Object arg) {
         ObservableParameter obsParam = (ObservableParameter) o;
        	
         String nome = obsParam.getParameterName();
         double valore = obsParam.getValue();
    	 
         this.updateParameter(nome, valore);
         }
     }
 




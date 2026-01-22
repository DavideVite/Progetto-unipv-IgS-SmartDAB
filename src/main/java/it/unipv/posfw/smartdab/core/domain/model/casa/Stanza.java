package it.unipv.posfw.smartdab.core.domain.model.casa;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.unipv.posfw.smartdab.adapter.facade.SensoreFacade;
import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.Dispositivo;
import it.unipv.posfw.smartdab.core.domain.model.parametro.ObservableParameter;
import it.unipv.posfw.smartdab.core.port.communication.observer.Observable;
import it.unipv.posfw.smartdab.core.port.communication.observer.Observer;

public class Stanza implements Observable, Observer{
	 private String id;
	 private String nome;
	 private double mq;
	 private List<Dispositivo> dispositivi = new ArrayList<>();
	 private Map<String, Double> parametri = new HashMap<>();
	 private List<Observer> observers = new ArrayList<>(); 

	 public Stanza(String id, String nome, double mq) {
		 this.id = id;
		 this.nome = nome;	
		 this.mq = mq;	  
	 }
	 
		 this.nome = nome;	
		 this.mq = mq;	 }

	 public String getId() {
		 return id;
	 }
	 
	 public double getMq() {
		 return mq;
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
        	 if (o instanceof SensoreFacade) {
        		 SensoreFacade s = (SensoreFacade) o;
        		 s.update(this, args);
        	 }
         }
         }

     @Override
     public void update(Observable o, Object arg) {
         ObservableParameter obsParam = (ObservableParameter) o;

         //DispositivoParameter nome = obsParam.getParameterName();
         double valore = obsParam.getValue();

         this.updateParameter(nome, valore);
         }
     }



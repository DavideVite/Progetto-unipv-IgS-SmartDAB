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
 * Rappresenta una stanza della casa e funge da mediatore centrale per la logica dei dispositivi.
 * Implementa un doppio ruolo nel pattern Observer:
 * Observer: monitora i cambiamenti provenienti dagli attuatori (tramite {@link ObservableParameter}).
 * Observable: notifica i sensori quando i parametri cambiano.
 * 
 * * @author Beatrice Bertone
 * @version 2.0
 */

/**
 * Aggiunto RoomPort all'elenco delle interfacce implementate.
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
	 private Map<String, Double> parametriTarget = new HashMap<>();
	 private ParameterSyncFunction syncFunction = (current, target) -> target;
	 private List<Observer> observers = new ArrayList<>();
	 
	 /**
	     * Costruttore per la creazione di nuove stanze. 
	     * Genera automaticamente un ID incrementale prefissato da "S".
	     * * @param nome Il nome da assegnare alla stanza.
	     * @param mq della stanza.
	     */
	 public Stanza(String nome, double mq) {
		 counter++;
		 this.id = "S" + counter;
		 this.nome = nome;	
		 this.mq = mq;
		 this.createdAt = LocalDateTime.now();
	 }
	 
	 /**
	     * Costruttore utilizzato dal DAO.
	     * Ripristina l'ID dal database e aggiorna il contatore globale.
	     * * @param id esistente nel database.
	     * @param nome della stanza.
	     * @param mq della stanza.
	     * @param createdAt Data di creazione originale.
	     */
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
		    	System.err.println("Errore nel counter della Stanza ");
		    	e.printStackTrace();
		    }
		}
	 
	 public LocalDateTime getCreatedAt() { 
		 return createdAt;
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
			 try {
				 attuatori.add((AttuatoreFacade) d);
			 } catch (ClassCastException e) {
				 // non è un attuatore, ignora
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

	/**
     * Aggiorna un parametro ambientale e, se il valore è effettivamente cambiato,
     * notifica tutti gli osservatori registrati.
     * * @param nomeParametro 
     * @param nuovoValore rilevato.
     */
	public void updateParameter(String nomeParametro, double nuovoValore) {
		Double vecchioValore = this.parametri.get(nomeParametro);
		
		//procedo solo se il valore è cambiato 
		if(vecchioValore == null || vecchioValore != nuovoValore) {
		this.parametri.put(nomeParametro, nuovoValore);
		notifyObservers(nomeParametro);
		}
	}

	/**
     * Imposta un valore desiderato (target) per un parametro e applica la 
     * {@link ParameterSyncFunction} per determinare il nuovo stato corrente.
     * * @param nomeParametro.
     * @param valoreTarget Il valore desiderato dall'utente.
     */
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

     /**
      * Notifica tutti gli osservatori del cambiamento di un parametro.
      * * @param args Il nome del parametro che ha subito la variazione.
      */
     @Override
	 public void notifyObservers(Object args) {
         for (Observer o : observers) {
        	 o.update(this, parametri.get(args.toString()));
        	 }
         }

     /**
      * Metodo del pattern Observer. Riceve notifiche dai dispositivi 
      * (tramite {@link ObservableParameter}) e aggiorna lo stato interno della stanza.
      * * @param o L'oggetto osservabile (il parametro di un sensore).
      * @param arg Argomento della notifica (non utilizzato, i dati vengono estratti da o).
      */
     @Override
     public void update(Observable o, Object arg) {
         ObservableParameter obsParam = (ObservableParameter) o;
        
        //recupero l'Enum che identifica il parametro
		DispositivoParameter paramEnum = obsParam.getParameterName();
		String nomeStr = paramEnum.name();
		double valore = obsParam.getValue();

		this.updateParameter(nomeStr, valore);
	}
}



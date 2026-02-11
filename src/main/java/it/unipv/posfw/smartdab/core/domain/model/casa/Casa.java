package it.unipv.posfw.smartdab.core.domain.model.casa;


import java.util.HashSet;
import java.util.Set;

/**
 * Rappresenta l'entità principale del modello del dominio.
 * La classe è un contenitore di oggetti {@link Stanza} 
 * e fornisce metodi per la ricerca delle stanze.
 * *Utilizza un {@link HashSet} per garantire l'univocità delle stanze 
 * all'interno della collezione.
 * @author Beatrice Bertone
 * @version 1.0
 */
public class Casa {
      private Set<Stanza> stanze = new HashSet<>();

      /**
       * Costruttore predefinito. Inizializza una nuova istanza di Casa 
       * con una collezione di stanze vuota.
       */
      public Casa() {
      }

	  public Set<Stanza> getStanze() {
		  return stanze;
	  }

	  /**
	     * Verifica se esiste già una stanza con il nome specificato, 
	     * * @param nomeStanza Il nome della stanza da cercare.
	     * @return {@code true} se la stanza esiste, {@code false} altrimenti.
	     */
	  public boolean esisteStanza(String nomeStanza) {
		  for(Stanza s: stanze) {
			  if (s.getNome().equalsIgnoreCase(nomeStanza)) {
				  return true;
			  }
		  }
		  return false;
	  }

	  /**
	     * Ricerca una stanza all'interno della casa tramite il suo nome.
	     * * @param nomeStanza Il nome della stanza da trovare.
	     * @return L'oggetto {@link Stanza} corrispondente, o {@code null} se non trovata.
	     */
	   public Stanza cercaStanza(String nomeStanza) {
     	  for(Stanza s: stanze) {
     		  if(s.getNome().equalsIgnoreCase(nomeStanza)) {
     			  return s;
     		  }
     	  }
     	  return null; 
	 	  }
	   
	   //cerca la stanza tramite id
	   public Stanza cercaStanzaPerId(String idStanza) {
		   for(Stanza s: stanze) {
			   if(s.getId().equalsIgnoreCase(idStanza)) {
				   return s;
				   }
			   }
		   return null;
	   }
      
	   //aggiunge una stanza alla lista
	  public void nuovaStanza(Stanza s) {
           this.stanze.add(s);
	  }

	  //rimuove una stanza dalla lista
	  public void rimuoviStanza(Stanza s) {
		  this.stanze.remove(s);
	  }
	  
	  /**
	     * Calcola la superficie totale della casa sommando la metratura 
	     * di ogni singola stanza presente.
	     * * @return La somma dei metri quadri totali.
	     */
	  public double calcolaMqTotali() {
		  double mqTotali = 0;
		  for(Stanza s: stanze) {
			  mqTotali += s.getMq();
		  }
		  return mqTotali;
	  }


}
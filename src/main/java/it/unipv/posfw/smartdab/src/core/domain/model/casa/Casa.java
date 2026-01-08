package it.unipv.posfw.smartdab.src.core.domain.model.casa;

import java.util.HashSet;
import java.util.Set;

public class Casa {
      private Set<Stanza> stanze = new HashSet<>();
      double mqTotali;
      
      public Casa(double mqTotali) {
    	  this.mqTotali = mqTotali;
      }

	  public double getMqTotali() {
		  return mqTotali;
	  }

	  public Set<Stanza> getStanze() {
		  return stanze;
	  }
	  
	  public boolean esisteStanza(String nomeStanza) {
		  for(Stanza s: stanze) {
			  if (s.getNome().equalsIgnoreCase(nomeStanza)) {
				  return true;
			  }
		  }
		  return false;
	  }
	  
	   public Stanza cercaStanza(String nomeStanza) {
     	  for(Stanza s: stanze) {
     		  if(s.getNome().equalsIgnoreCase(nomeStanza)) {
     			  return s;
     		  }
     	  }
     	  return null; 
	 	  }
	  
	  public void nuovaStanza(Stanza s) {
           this.stanze.add(s);
	  }
	  
	  public void rimuoviStanza(Stanza s) {
		  this.stanze.remove(s);
	  }
      
      
}

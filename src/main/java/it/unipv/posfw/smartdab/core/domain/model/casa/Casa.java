package it.unipv.posfw.smartdab.core.domain.model.casa;


import java.util.HashSet;
import java.util.Set;


public class Casa {
      private Set<Stanza> stanze = new HashSet<>();


      public Casa() {
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
	  
	  public double calcolaMqTotali() {
		  double mqTotali = 0;
		  for(Stanza s: stanze) {
			  mqTotali += s.getMq();
		  }
		  return mqTotali;
	  }


}
package it.unipv.posfw.smartdab.core.service;

import java.util.List;
import java.util.Set;

import it.unipv.posfw.smartdab.core.domain.model.casa.Casa;
import it.unipv.posfw.smartdab.core.domain.model.casa.Stanza;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.Dispositivo;

public class GestoreStanze {
     private Casa casa;
     
     public GestoreStanze(Casa casa) {
    	 this.casa = casa;
    	 
     }

	 public Casa getCasa() {
		 return casa;
	 }

    public Set<Stanza> visualizzaStanze() {
    	return casa.getStanze();
    }
    
    public List<Dispositivo> getDispositiviPerStanza(String nomeStanza) {
    	Stanza s = casa.cercaStanza(nomeStanza);
    	
    	if(s != null) {
    		return s.getDispositivi();
    	}
    	return null;  	
    }
    
    public boolean creaStanza(String id, String nomeStanza) {
    	  if(casa.esisteStanza(nomeStanza)) {
    		  return false;
    		}
    	  
    	Stanza nuovaStanza = new Stanza(id, nomeStanza);
    	casa.nuovaStanza(nuovaStanza);
    	return true;
    }
    
    public boolean modificaStanza(String nome, String nuovoNome) {
    	Stanza s = casa.cercaStanza(nome); 
    	if (s!=null) {
    		if(casa.esisteStanza(nuovoNome)) {
    			return false;
    		}		 
    		 s.setNome(nuovoNome);
    		 return true;
    	 }
    	 return false;
    }
    
    public boolean eliminaStanza(String nomeStanza) {
    	if(casa.esisteStanza(nomeStanza)) {
    		Stanza s = casa.cercaStanza(nomeStanza);
    		if(s.isEmpty()) {
    			casa.rimuoviStanza(s);
    			return true;
    		} else {
    			System.out.println("Errore: la stanza contiene ancora dispositivi");
    			return false;
    		}
    	}
    	System.out.println("Errore: la stanza non esiste");
    	return false;
    }    
}


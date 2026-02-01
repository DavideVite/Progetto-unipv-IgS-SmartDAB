package it.unipv.posfw.smartdab.core.domain.model.casa;

import java.util.Set;

import it.unipv.posfw.smartdab.core.domain.model.utente.Autenticazione;
import it.unipv.posfw.smartdab.core.service.GestoreStanze;
import it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao.MisuraDAO;
import it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao.MisuraDAOImpl;
import it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao.StanzaDAO;
import it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao.StanzaDAOImpl;

	public class Hub {
		
		private static Hub instance;
		
		private Autenticazione autenticazione;
	    private GestoreStanze gestoreStanze;
	    
	    private Hub(String passwordProduttore) {
           this.autenticazione =new Autenticazione(passwordProduttore);

           // recupero dati (accensione)
          	StanzaDAO stanzaDao = new StanzaDAOImpl();
          	MisuraDAO misuraDao = new MisuraDAOImpl();
          	
	    	Set<Stanza> stanzeRecuperate = stanzaDao.readAllStanze(); 
	        Casa casa = new Casa();    		
	        
	        int maxId = 0;
            for (Stanza s : stanzeRecuperate) {
            	casa.nuovaStanza(s);
            
            }
	       
	       this.gestoreStanze = new GestoreStanze(casa, stanzaDao, misuraDao);

	    }  
	    
	    public static Hub getInstance(String passwordProduttore) {
	    	if(instance==null) {
	    		instance = new Hub(passwordProduttore);
	    	}
	    	return instance;
	    }
	    
	    public static Hub getInstance() {
	    	return instance;
	    }
	    
	    public GestoreStanze getGestoreStanze() {
	    	return gestoreStanze;
	    }
	    
	    public Autenticazione getAutenticazione() {
	    	return autenticazione;
	    }
	    
	    // Aggiungo un metodo che mette in contatto l'event bus con l'HUB
	    public void receiveCommunication() {
	    	
	    }
	} 


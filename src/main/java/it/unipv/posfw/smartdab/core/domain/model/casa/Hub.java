package it.unipv.posfw.smartdab.core.domain.model.casa;

import it.unipv.posfw.smartdab.core.domain.model.utente.Autenticazione;
import it.unipv.posfw.smartdab.core.service.GestoreStanze;

public class Hub {
	
	private static Hub instance;
	
	private Autenticazione autenticazione;
    private GestoreStanze gestoreStanze;
    
    private Hub(double mq, String passwordProduttore) {
    	Casa casa = new Casa(mq);
    	this.gestoreStanze = new GestoreStanze(casa);
    	this.autenticazione =new Autenticazione(passwordProduttore);
    }  
    
    public static Hub getInstance(double mq, String passwordProduttore) {
    	if(instance==null) {
    		instance = new Hub(mq, passwordProduttore);
    	}
    	return instance;
    }
    
    //metodo per richiamare hub senza inserire i parametri
    public static Hub getInstance() {
    	return instance;
    }
    
    public GestoreStanze getGestoreStanze() {
    	return gestoreStanze;
    }
    
    public Autenticazione getAutenticazione() {
    	return autenticazione;
    }
} 

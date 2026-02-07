package it.unipv.posfw.smartdab.core.domain.model.casa;

import it.unipv.posfw.smartdab.core.domain.model.utente.Autenticazione;
import it.unipv.posfw.smartdab.core.service.GestoreStanze;
import it.unipv.posfw.smartdab.factory.EventBusFactory;
import it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao.MisuraDAO;
import it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao.MisuraDAOImpl;
import it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao.StanzaDAO;
import it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao.StanzaDAOImpl;
import it.unipv.posfw.smartdab.core.port.messaging.IEventBusMalfunzionamenti;
import it.unipv.posfw.smartdab.core.service.DispositiviManager;
import it.unipv.posfw.smartdab.core.service.GestoreMalfunzionamenti;
import it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao.DispositivoDAO;
import it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao.DispositivoDAOImpl;
import it.unipv.posfw.smartdab.strategy.StrategiaStandard;

	public class Hub {
		
		private static Hub instance;
		
		private Autenticazione autenticazione;
	    private GestoreStanze gestoreStanze;
	    private GestoreMalfunzionamenti gestoreMalfunzionamenti;
	    
	    private boolean sistemaSbloccato = false;
	    
	    private Hub(String passwordProduttore) {
           this.autenticazione =new Autenticazione(passwordProduttore);

           // recupero dati (accensione)
          	StanzaDAO stanzaDao = new StanzaDAOImpl();
          	MisuraDAO misuraDao = new MisuraDAOImpl();
          	DispositivoDAO dispositivoDao = new DispositivoDAOImpl();
          	DispositiviManager dispManager = new DispositiviManager();
          	IEventBusMalfunzionamenti eventBus = EventBusFactory.getEventBus(dispManager);
          	
	        Casa casa = new Casa();    		
	        
	       this.gestoreStanze = new GestoreStanze(casa, stanzaDao, misuraDao);
	       this.gestoreMalfunzionamenti = new GestoreMalfunzionamenti(new StrategiaStandard(), dispositivoDao, eventBus);
	    }  
	    
	    //metodo per sbloccare il sistema all'accensione
	    public boolean accedi(String pin) {
	    	if (autenticazione.verificaPin(pin)) {
	    		this.sistemaSbloccato = true;
	    		return true;
	    	}
	    	return false;
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
	    
	    //per accedere all'hub il sistema deve essere sbloccato
	    public GestoreStanze getGestoreStanze() {
	    	if (sistemaSbloccato) {
	    	return gestoreStanze;
	    	}
	    	else {
	    		System.out.println("Sistema bloccato, inserire il PIN");
	    		return null;
	    	}
	    }
	    
	    public Autenticazione getAutenticazione() {
	    	return autenticazione;
	    }
	    
	    public GestoreMalfunzionamenti getGestoreMalfunzionamenti() {
	    	if (sistemaSbloccato) {
	    	return gestoreMalfunzionamenti;
	    	}
	    	else {
	    		System.out.println("Sistema bloccato, inserire il PIN");
	    		return null;
	    	}
	    }
	
	} 




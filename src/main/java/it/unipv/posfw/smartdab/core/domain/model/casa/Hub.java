package it.unipv.posfw.smartdab.core.domain.model.casa;

	import it.unipv.posfw.smartdab.core.domain.model.utente.Autenticazione;
	import it.unipv.posfw.smartdab.core.service.GestoreStanze;

	public class Hub {
		
		private static Hub instance;
		
		private Autenticazione autenticazione;
	    private GestoreStanze gestoreStanze;
	    
	    private Hub(String passwordProduttore) {
	    	Casa casa = new Casa();
	    	this.gestoreStanze = new GestoreStanze(casa);
	    	this.autenticazione =new Autenticazione(passwordProduttore);
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
	} 


package it.unipv.posfw.smartdab.core.domain.model.casa;

import java.util.ArrayList;

import it.unipv.posfw.smartdab.core.beans.CommunicationPOJO;
import it.unipv.posfw.smartdab.core.beans.DispositivoPOJO;
import it.unipv.posfw.smartdab.core.domain.model.utente.Autenticazione;
import it.unipv.posfw.smartdab.core.port.messaging.IEventBusMalfunzionamenti;
import it.unipv.posfw.smartdab.core.service.DispositiviManager;
import it.unipv.posfw.smartdab.core.service.GestoreMalfunzionamenti;
import it.unipv.posfw.smartdab.core.service.GestoreStanze;
import it.unipv.posfw.smartdab.factory.EventBusFactory;
import it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao.CommunicationDAO;
import it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao.CommunicationDAOImpl;
import it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao.DispositivoDAO;
import it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao.DispositivoDAOImpl;
import it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao.MisuraDAO;
import it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao.MisuraDAOImpl;
import it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao.StanzaDAO;
import it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao.StanzaDAOImpl;
import it.unipv.posfw.smartdab.strategy.StrategiaStandard;

	public class Hub {
		
		private static Hub instance;
		
		private Autenticazione autenticazione;
	    private GestoreStanze gestoreStanze;
	    private GestoreMalfunzionamenti gestoreMalfunzionamenti;
        private MisuraDAO misuraDao;
      	private DispositivoDAO dispositivoDao;	
      	private CommunicationDAO communicationDao;
	    private boolean sistemaSbloccato = false;
	    
	    private Hub(String passwordProduttore) {
           this.autenticazione =new Autenticazione(passwordProduttore);

           // recupero dati (accensione)
          	StanzaDAO stanzaDao = new StanzaDAOImpl();
          	this.misuraDao = new MisuraDAOImpl();
          	this.dispositivoDao = new DispositivoDAOImpl();
          	this.communicationDao = new CommunicationDAOImpl();

          	DispositiviManager dispManager = new DispositiviManager();
          	IEventBusMalfunzionamenti eventBus = EventBusFactory.getEventBus(dispManager);
          	
	        Casa casa = new Casa();    		
	        
	       this.gestoreStanze = new GestoreStanze(casa, stanzaDao, misuraDao);
	       this.gestoreMalfunzionamenti = new GestoreMalfunzionamenti(new StrategiaStandard(), dispositivoDao, eventBus);
	       
	       //la verifica dello stato dei dispositivi deve essere periodica
	       java.util.Timer timer = new java.util.Timer();
	       
	       //timer.scheduleAtFixedRate(task, delay, period);
	        timer.scheduleAtFixedRate(new java.util.TimerTask() {
	            @Override
	            public void run() {
	                // Eseguiamo il controllo solo se il sistema è stato sbloccato col PIN
	                if (sistemaSbloccato) {
	                    System.out.println("Monitoraggio periodico: controllo stato dispositivi...");
	                    verificaStatoDispositivi();
	                }
	            }
	        }, 5000, 60000); // Parte dopo 5 secondi dall'accensione e ripete ogni 60 secondi (60000 ms)
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
	    
	    public void verificaStatoDispositivi() {
	    	//Legge le ultime comunicazioni
	        ArrayList<CommunicationPOJO> ultimeCom = communicationDao.selectN(100); 
	        
	        ArrayList<DispositivoPOJO> tuttiIDispositivi = dispositivoDao.selectN(100);

	        for (DispositivoPOJO d : tuttiIDispositivi) {
	        	// Cerchiamo l'ultima comunicazione per questo dispositivo
	            CommunicationPOJO com = trovaComunicazionePerDispositivo(ultimeCom, d.getId());
	           
	            
	            //Se non c'è comunicazione (null) OPPURE se l'esito è "FAILED", 
	            //passiamo null al gestore per segnalare il problema
	            Object valoreDaControllare = null;
	            if (com != null && !"FAILED".equals(com.getEsito())) {
	                valoreDaControllare = com.getValue();
	            }
	            
	            gestoreMalfunzionamenti.controllaConnessione(d, valoreDaControllare);
	        }
	    }
	        
	        private CommunicationPOJO trovaComunicazionePerDispositivo(ArrayList<CommunicationPOJO> lista, String idDisp) {
	        	for(CommunicationPOJO c : lista) {
	        		//confronto id del dispositivo salvato nella comunicazione con quello del dispositivo che stiamo controllando
	        		if(c.getDispositivo().equals(idDisp)) {
	        			return c; //comunicazione più recente
	        		}
	        	}
	        	return null;
	        }
	}




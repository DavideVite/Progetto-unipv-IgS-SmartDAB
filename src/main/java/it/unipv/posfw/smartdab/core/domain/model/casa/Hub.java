package it.unipv.posfw.smartdab.core.domain.model.casa;

import java.util.ArrayList;

import it.unipv.posfw.smartdab.core.beans.CommunicationPOJO;
import it.unipv.posfw.smartdab.core.beans.DispositivoPOJO;
import it.unipv.posfw.smartdab.core.domain.enums.DispositivoStates;
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

/**
 * La classe Hub rappresenta il cuore del sistema di domotica.
 * Implementa il pattern Singleton per garantire un'unica istanza globale
 * e il pattern Facade per fornire un accesso semplificato ai sottosistemi
 * di gestione stanze, malfunzionamenti e autenticazione.
 * Include un sistema di monitoraggio periodico che verifica lo stato dei 
 * dispositivi ogni minuto, a condizione che il sistema sia sbloccato.
 * @author Beatrice Bertone
 * @version 1.2
 */

	public class Hub {
		
		/** Unica istanza della classe Hub (Singleton). */
		private static Hub instance;
		
		private Autenticazione autenticazione;
	    private GestoreStanze gestoreStanze;
	    private GestoreMalfunzionamenti gestoreMalfunzionamenti;
        private MisuraDAO misuraDao;
      	private DispositivoDAO dispositivoDao;	
      	private CommunicationDAO communicationDao;
	    private boolean sistemaSbloccato = false;
	    
	    /**
	     * Costruttore privato.
	     * Inizializza i sottosistemi, i DAO e avvia il timer di monitoraggio periodico.
	     * * @param passwordProduttore necessaria per il primo avvio.
	     */
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
	                if (sistemaSbloccato) {
	                try {
	                    System.out.println("Monitoraggio periodico: controllo stato dispositivi...");
	                    verificaStatoDispositivi();
	                } catch (Exception e) {
	                    System.err.println("ATTENZIONE: Monitoraggio fallito a causa di un errore dati: " + e.getMessage());
	                }
	                }
	            }
	        }, 5000, 60000); // Parte dopo 5 secondi dall'accensione e ripete ogni 60 secondi (60000 ms)
	    }  
	    
	    /**
	     * Metodo per sbloccare il sistema verificando il PIN inserito.
	     * * @param pin .
	     * @return {@code true} se il PIN è corretto, {@code false} altrimenti.
	     */
	    public boolean accedi(String pin) {
	    	if (autenticazione.verificaPin(pin)) {
	    		this.sistemaSbloccato = true;
	    		return true;
	    	}
	    	return false;
	    }
	    
	    /**
	     * Restituisce l'istanza unica dell'Hub, creandola se necessario.
	     * * @param passwordProduttore.
	     * @return L'istanza Singleton di {@link Hub}.
	     */
	    public static Hub getInstance(String passwordProduttore) {
	    	if(instance==null) {
	    		instance = new Hub(passwordProduttore);
	    	}
	    	return instance;
	    }
	    
	    /**
	     * Restituisce l'istanza dell'Hub senza richiedere parametri.
	     * * @return L'istanza Singleton esistente, o {@code null} se non ancora inizializzata.
	     */
	    public static Hub getInstance() {
	    	return instance;
	    }
	    
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
	    
	    /**
	     * Esegue la scansione di tutti i dispositivi registrati per verificarne la connessione.
	     * Recupera le ultime comunicazioni dal database e delega al 
	     * {@link GestoreMalfunzionamenti} la logica di controllo specifica.
	     */
	    public void verificaStatoDispositivi() {
	    	//Legge le ultime comunicazioni
	        ArrayList<CommunicationPOJO> ultimeCom = communicationDao.selectN(100); 
	        
	        ArrayList<DispositivoPOJO> tuttiIDispositivi = dispositivoDao.selectN(100);

	        for (DispositivoPOJO d : tuttiIDispositivi) {
	        	
	        	if(d.getStato() == DispositivoStates.DISABLED) {
	        		continue;
	        	}
	        	
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
	        
	    /**
	     * Cerca all'interno di una lista la comunicazione più recente associata a un dispositivo.
	     * * @param lista La lista di comunicazioni recenti.
	     * @param idDisp L'identificativo del dispositivo da cercare.
	     * @return La {@link CommunicationPOJO} trovata, o {@code null} se assente.
	     */
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




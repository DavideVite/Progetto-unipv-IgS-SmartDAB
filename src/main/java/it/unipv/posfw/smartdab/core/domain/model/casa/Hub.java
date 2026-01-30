package it.unipv.posfw.smartdab.core.domain.model.casa;

import java.util.Set;

import it.unipv.posfw.smartdab.core.beans.DispositivoPOJO;
import it.unipv.posfw.smartdab.core.beans.MisuraPOJO;
import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.Dispositivo;
import it.unipv.posfw.smartdab.core.domain.model.utente.Autenticazione;
import it.unipv.posfw.smartdab.core.service.GestoreStanze;
import it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao.DispositivoDAO;
import it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao.DispositivoDAOImpl;
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
            	
            	//estrae il numero dall'Id
            	int idNumerico = Integer.parseInt(s.getId().substring(1));
            	if (idNumerico > maxId) 
            		maxId = idNumerico;
            }
            Stanza.setCounter(maxId);
	    }
            
         /*  for (DispositivoParameter param : DispositivoParameter.values()) {
            if (param == DispositivoParameter.UNKNOWN) continue;
            
            MisuraPOJO ultimaMisura = misuraDao.readUltimaMisura(s.getId(), param.name());
            if (ultimaMisura != null) {
            	s.updateParameter(param.name(), ultimaMisura.getValore());
            }
            } */
            
          /*  DispositivoDAO dispositivoDao = new DispositivoDAOImpl();
            //ArrayList<DispositivoPOJO> dispositiviSalvati = dispositivoDao.selectAll();
            
            for (DispositivoPOJO pojo : dispositiviTrovati) {
            	
            	Stanza stanzaDestinazione = casa.getStanzabyId(pojo.getStanza());
            	
            	if (stanzaDestinazione != null ) {
            		Dispositivo d = new Dispositivo(pojo.getId(), pojo.getParametro());
            		
            		d.setState(pojo.isAttivo());
            		
            		stanzaDestinazione.addDispositivo(d);
            		
            		d.setState(pojo.getStato());
            	}
            	
            }
            
            Scenario scenarioDao = new ScenarioDAOImpl();
	       
	       this.gestoreStanze = new GestoreStanze(casa);

	    }  */
	    
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


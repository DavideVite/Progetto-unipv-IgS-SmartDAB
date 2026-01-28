package it.unipv.posfw.smartdab.core.service;

import java.util.List;
import java.util.Set;

import it.unipv.posfw.smartdab.core.domain.model.casa.Casa;
import it.unipv.posfw.smartdab.core.domain.model.casa.Hub;
import it.unipv.posfw.smartdab.core.domain.model.casa.Stanza;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.Dispositivo;
import it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao.StanzaDAO;
import it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao.StanzaDAOImpl;

/**
 * Servizio per la gestione delle stanze della casa.
 *
 * REFACTORING: Dependency Injection del DAO
 * - Prima: Il DAO veniva creato ad ogni operazione (new StanzaDAOImpl() in ogni metodo)
 * - Dopo: Il DAO viene iniettato nel costruttore e riutilizzato
 *
 * Vantaggi:
 * 1. Efficienza: una sola istanza del DAO invece di crearne una per ogni operazione
 * 2. Testabilita: possibilita di iniettare mock DAO nei test
 * 3. Dependency Inversion: dipendenza dall'interfaccia StanzaDAO, non dall'implementazione
 * 4. Separazione responsabilita: GestoreStanze non deve sapere come creare il DAO
 */
public class GestoreStanze {
    private final Casa casa;
    // FIX: DAO iniettato nel costruttore invece di creato ad ogni operazione
    private final StanzaDAO stanzaDAO;

    /**
     * Costruttore con Dependency Injection.
     * Usa l'implementazione di default StanzaDAOImpl.
     *
     * @param casa L'oggetto Casa da gestire
     */
    public GestoreStanze(Casa casa) {
        this(casa, new StanzaDAOImpl());
    }

    /**
     * Costruttore con Dependency Injection esplicita del DAO.
     * Permette di iniettare mock DAO per i test.
     *
     * @param casa L'oggetto Casa da gestire
     * @param stanzaDAO Il DAO per la persistenza delle stanze
     */
    public GestoreStanze(Casa casa, StanzaDAO stanzaDAO) {
        this.casa = casa;
        this.stanzaDAO = stanzaDAO;
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

    public boolean creaStanza(String id, String nomeStanza, double mqStanza, String pin, String passwordProduttore) {
        if(!Hub.getInstance().getAutenticazione().verificaPin(pin)) {
        	return false;
        }
      
    	  if(casa.esisteStanza(nomeStanza)) {
    		  return false;
    		}

    	Stanza nuovaStanza = new Stanza(id, nomeStanza, mqStanza);
    	casa.nuovaStanza(nuovaStanza);

    	// FIX: Usa il DAO iniettato invece di crearne uno nuovo
    	stanzaDAO.insertStanza(nuovaStanza);
    	return true;
        }

    public boolean modificaNomeStanza(String nome, String nuovoNome) {
    	Stanza s = casa.cercaStanza(nome); 
    	if (s!=null) {
    		if(casa.esisteStanza(nuovoNome)) {
    			return false;
    		}		 
    		 s.setNome(nuovoNome);

    		 // FIX: Usa il DAO iniettato invece di crearne uno nuovo
    		 stanzaDAO.updateStanza(s);
    		 return true;
    	 }
    	 return false;
    }

    public boolean eliminaStanza(String nomeStanza) {
    	if(casa.esisteStanza(nomeStanza)) {
    		Stanza s = casa.cercaStanza(nomeStanza);
    		if(s.isEmpty()) {
    			casa.rimuoviStanza(s);

    			// FIX: Usa il DAO iniettato invece di crearne uno nuovo
       		    stanzaDAO.deleteStanza(s);
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




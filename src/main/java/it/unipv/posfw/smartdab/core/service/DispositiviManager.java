package it.unipv.posfw.smartdab.core.service;

import java.util.ArrayList;
import java.util.Iterator;

import it.unipv.posfw.smartdab.core.beans.DispositivoPOJO;
import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.enums.DispositivoStates;
import it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao.DispositivoDAO;
import it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao.DispositivoDAOImpl;

public class DispositiviManager {
	private ArrayList<DispositivoPOJO> dispositivi;
	private final DispositivoDAO dao;
	
	public DispositiviManager() {
		this(new DispositivoDAOImpl());
	}

	public DispositiviManager(DispositivoDAOImpl dispositivoDAOImpl) {
		this.dao = dispositivoDAOImpl;
		caricaDalDatabase();
	}
	
    public void caricaDalDatabase() {
        try {
            dispositivi = dao.selectN(100);
            System.out.println("Caricati " + dispositivi.size() + " dispositivi dal database");
        } catch (Exception e) {
            System.err.println("Errore durante il caricamento dei dispositivi dal database: " + e.getMessage());
            e.printStackTrace();
        }
    }

	public ArrayList<DispositivoPOJO> getDispositivi() {
		return dispositivi;
	}
	
    public DispositivoPOJO aggiungiDispositivo(String id, String stanza, DispositivoParameter parametro, 
			   String tipo, DispositivoStates stato, boolean attivo) {
    	
    	DispositivoPOJO d = new DispositivoPOJO(id, stanza, parametro, tipo, stato, attivo);
    	
        if(dispositivi.contains(d)) {
            return null;
        }

        dispositivi.add(d);

        dao.insertDispositivo(d);
        return d;
    }
    
    public DispositivoPOJO aggiungiDispositivo(DispositivoPOJO d) {

        if(dispositivi.contains(d)) {
            return null;
        }

    	dispositivi.add(d);

    	dao.insertDispositivo(d);
    	return d;
    }
    
    public DispositivoPOJO aggiornaDispositivo(DispositivoPOJO d) {

        if(getDispositivoById(d.getId()) != null) {
        	System.out.println(dispositivi.get(dispositivi.indexOf(getDispositivoById(d.getId()))));
            dispositivi.set(dispositivi.indexOf(getDispositivoById(d.getId())), d);
            System.out.println(dispositivi.get(dispositivi.indexOf(getDispositivoById(d.getId()))));
            dao.updateDispositivo(d);
            return null;
        }

    	dispositivi.add(d);

    	dao.insertDispositivo(d);
    	return d;
    }
    
    // Metodo per rilevazione dei malfunzionamenti
    public boolean disableDispositivo(String id) {
    	if(dao.existsById(id)) {
    		dao.disableDispositivo(id);
    		getDispositivoById(id).setStato(DispositivoStates.DISABLED);
    		System.out.println(getDispositivoById(id).getStato());
    		return true;
    	} else {
    		System.out.println("Errore: il dispositivo non esiste");
    		return false;
    	}
    } 
    

    
    // Per i test
    public void clearDispositivi() {
		dispositivi.clear();
	}
    
    // Metodo per il DB
	public boolean deleteDispositivo(String id) {
    	if(dao.existsById(id)) {
    		dao.deleteDispositivo(id);
    		return true;
    	} else {
    		System.out.println("Errore: il dispositivo non esiste");
    		return false;
    	}
    } 
    
    public DispositivoPOJO getDispositivoById(String id) {
    	Iterator<DispositivoPOJO> iter = dispositivi.iterator();
    	DispositivoPOJO d;
    	
    	while(iter.hasNext()) {
    		d = iter.next();
    		if(d.getId().equals(id)) return d;
    	}
    	return null;
    }
}

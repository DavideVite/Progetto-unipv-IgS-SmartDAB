package it.unipv.posfw.smartdab.core.service;

import java.util.List;
import java.util.Set;

import it.unipv.posfw.smartdab.core.beans.MisuraPOJO;
import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.model.casa.Casa;
import it.unipv.posfw.smartdab.core.domain.model.casa.Stanza;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.Dispositivo;
import it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao.MisuraDAO;
import it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao.MisuraDAOImpl;
import it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao.StanzaDAO;
import it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao.StanzaDAOImpl;

/**
 * Servizio per la gestione delle stanze della casa.
 *
 * REFACTORING: Dependency Injection del DAO e caricamento dal database.
 * - Prima: Il DAO veniva creato ad ogni operazione (new StanzaDAOImpl() in ogni metodo)
 * - Dopo: Il DAO viene iniettato nel costruttore e riutilizzato
 * - Le stanze vengono caricate dal database all'avvio
 *
 * Vantaggi:
 * 1. Efficienza: una sola istanza del DAO invece di crearne una per ogni operazione
 * 2. Testabilita: possibilita di iniettare mock DAO nei test
 * 3. Dependency Inversion: dipendenza dall'interfaccia StanzaDAO, non dall'implementazione
 * 4. Separazione responsabilita: GestoreStanze non deve sapere come creare il DAO
 * 5. Persistenza: le stanze sopravvivono al riavvio dell'applicazione
 */
public class GestoreStanze {
    private final Casa casa;
    // FIX: DAO iniettato nel costruttore invece di creato ad ogni operazione
    private final StanzaDAO stanzaDAO;
    private final MisuraDAO misuraDAO;

    /**
     * Costruttore con Dependency Injection.
     * Usa le implementazioni di default.
     * Carica automaticamente le stanze e le ultime misure dal database all'avvio.
     *
     * @param casa L'oggetto Casa da gestire
     */
    public GestoreStanze(Casa casa) {
        this(casa, new StanzaDAOImpl(), new MisuraDAOImpl());
    }

    /**
     * Costruttore con Dependency Injection esplicita dei DAO.
     * Permette di iniettare mock DAO per i test.
     * Carica automaticamente le stanze e le ultime misure dal database all'avvio.
     *
     * @param casa L'oggetto Casa da gestire
     * @param stanzaDAO Il DAO per la persistenza delle stanze
     * @param misuraDAO Il DAO per la lettura delle misure
     */
    public GestoreStanze(Casa casa, StanzaDAO stanzaDAO, MisuraDAO misuraDAO) {
        this.casa = casa;
        this.stanzaDAO = stanzaDAO;
        this.misuraDAO = misuraDAO;
        caricaDalDatabase();
    }

    /**
     * Carica tutte le stanze dal database e le inserisce nella Casa.
     * Chiamato automaticamente nel costruttore.
     */
    private void caricaDalDatabase() {
        try {
            Set<Stanza> stanzeDalDb = stanzaDAO.readAllStanze();
            for (Stanza stanza : stanzeDalDb) {
                casa.nuovaStanza(stanza);
                caricaUltimeMisure(stanza);
            }
            System.out.println("Caricate " + stanzeDalDb.size() + " stanze dal database");
        } catch (Exception e) {
            System.err.println("Errore durante il caricamento delle stanze dal database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Carica le ultime misure dal database per ogni tipo di parametro numerico
     * e popola la mappa parametri della stanza.
     */
    private void caricaUltimeMisure(Stanza stanza) {
        for (DispositivoParameter param : DispositivoParameter.values()) {
            try {
                MisuraPOJO ultima = misuraDAO.readUltimaMisura(stanza.getId(), param.name());
                if (ultima != null) {
                    stanza.updateParameter(param.name(), ultima.getValore());
                }
            } catch (Exception e) {
                System.err.println("Errore caricamento misura " + param.name()
                        + " per stanza " + stanza.getNome() + ": " + e.getMessage());
            }
        }
    }

	 public Casa getCasa() {
		 return casa;
	 }

    public Set<Stanza> visualizzaStanze() {
    	return casa.getStanze();
    }

    public List<Dispositivo> getDispositiviPerStanza(String stanzaId) {
    	Stanza s = cercaStanzaPerId(stanzaId);

    	if(s != null) {
    		return s.getDispositivi();
    	}
    	return null;
    }

    /**
     * Crea una nuova stanza e la salva nel database.
     * L'autenticazione e' gia' stata verificata all'avvio dell'applicazione.
     * @param id L'identificatore della stanza (ignorato, verra' generato automaticamente)
     * @return la Stanza creata, oppure null se esiste gia' una stanza con lo stesso nome
     */
    public Stanza creaStanza(String nomeStanza, double mqStanza) {
        if(casa.esisteStanza(nomeStanza)) {
            return null;
        }

        Stanza nuovaStanza = new Stanza(nomeStanza, mqStanza);
        
        if(mqStanza <= 0) {
            System.err.println("Errore: I metri quadri devono essere maggiori di 0");
            return null; // Ritorna null perché la creazione non è valida
        }
        
        casa.nuovaStanza(nuovaStanza);

        stanzaDAO.insertStanza(nuovaStanza);
        return nuovaStanza;
    }

    public boolean modificaNomeStanza(String nome, String nuovoNome) {
    	return modificaStanza(nome, nuovoNome, -1);
    }

    /**
     * Modifica una stanza esistente (nome e/o metri quadrati).
     * @param nome Il nome attuale della stanza
     * @param nuovoNome Il nuovo nome (puo' essere uguale al precedente)
     * @param nuoviMq I nuovi metri quadrati (se <= 0, non viene modificato)
     * @return true se la modifica e' avvenuta con successo, false altrimenti
     */
    public boolean modificaStanza(String nome, String nuovoNome, double nuoviMq) {
    	Stanza s = casa.cercaStanza(nome);
    	if (s!=null) {
    		// Verifica che il nuovo nome non sia gia' usato da un'altra stanza
    		if(!nome.equals(nuovoNome) && casa.esisteStanza(nuovoNome)) {
    			return false;
    		}
    		s.setNome(nuovoNome);
    		if(nuoviMq > 0) {
    			s.setMq(nuoviMq);
    		}

    		// FIX: Usa il DAO iniettato invece di crearne uno nuovo
    		stanzaDAO.updateStanza(s);
    		return true;
    	 }
    	 return false;
    }

    public Stanza cercaStanzaPerId(String id) {
        for (Stanza s : casa.getStanze()) {
            if (s.getId().equals(id)) {
                return s;
            }
        }
        return null;
    }

    public boolean eliminaStanza(String idStanza) throws Exception {
    		Stanza s = casa.cercaStanzaPerId(idStanza);
    		if(s!=null) {
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




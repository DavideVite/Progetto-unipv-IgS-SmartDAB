package it.unipv.posfw.smartdab.core.service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import it.unipv.posfw.smartdab.core.beans.DispositivoPOJO;
import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.port.messaging.IEventBusMalfunzionamenti;
import it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao.DispositivoDAO;
import it.unipv.posfw.smartdab.strategy.MalfunzionamentoStrategy;

public class GestoreMalfunzionamenti{
	
	private Map<String, Integer> tentativiFalliti = new HashMap<>();
	private final DispositivoDAO dispositivoDao;
	private final IEventBusMalfunzionamenti eventBus;
    
    //Associa il tipo di parametro alla sua strategia specifica presente nel file
	private Map<DispositivoParameter, MalfunzionamentoStrategy> mappeStrategie = new HashMap<>();
    
    //La strategia da usare se un parametro non è presente nel file di configurazione
	private MalfunzionamentoStrategy strategiaDefault;
	
	public GestoreMalfunzionamenti(MalfunzionamentoStrategy strategiaDefault, DispositivoDAO dispositivoDao, IEventBusMalfunzionamenti eventBus) {
		this.strategiaDefault = strategiaDefault;
		this.dispositivoDao = dispositivoDao;
		this.eventBus = eventBus;
		caricaDispositiviDalDB();
		caricaConfigurazione(); // Metodo per leggere il file .properties
	}
	
	private void caricaDispositiviDalDB() {
		List<DispositivoPOJO> lista = dispositivoDao.selectN(100);
		for(DispositivoPOJO p: lista) {
			//uso l'ID come chiave
			tentativiFalliti.put(p.getId(), 0);
		}		
	}
	
	private void caricaConfigurazione() {
		Properties prop = new Properties();
        //Apre il file
		try(InputStream input = getClass().getClassLoader().getResourceAsStream("strategie.properties")) {
			if (input != null) {
			prop.load(input); // Carica le coppie chiave-valore
			
		for (String key : prop.stringPropertyNames()) {
             // Recupera il nome della classe (es: "it.unipv...StrategiaCritica")
             String className = prop.getProperty(key);
             
             //Trasforma la stringa in un oggetto reale. REFLECTION
             //Class.forName trova la classe, getDeclaredConstructor().newInstance() crea l'istanza
             MalfunzionamentoStrategy s = (MalfunzionamentoStrategy) Class.forName(className).getDeclaredConstructor().newInstance();    
             
             //Associa il parametro alla strategia appena creata
             mappeStrategie.put(DispositivoParameter.valueOf(key), s);
            }
			} else {
				System.err.println("File strategie.properties non trovato, verrà usata la strategia di default");
			}
        } catch (Exception e) {
            System.err.println("Errore caricamento strategie: " + e.getMessage());
        }	
	}
	
	public void controllaConnessione(DispositivoPOJO pojo, Object value) {

			
		if (value == null) {
	    	String id = pojo.getId();
			////cerca id nella mappa, se lo trova restituisce il numero di fallimenti salvati precedentemente
			//se non lo trova restituisce 0
			int conteggio = tentativiFalliti.getOrDefault(id,0) + 1;   
			tentativiFalliti.put(id, conteggio);
			
			//Cerca nella mappa se esiste una strategia speciale
            //Se non la trova, usa quella standard
			MalfunzionamentoStrategy strategyDaUsare = mappeStrategie.getOrDefault(pojo.getParametro(), strategiaDefault);
			
			//uso la strategia
			if(strategyDaUsare.deveDisabilitare(pojo, value, conteggio)) {
			//notifica l'event bus 
			eventBus.disableDispositivo(pojo);
			
			}
		} else {
			// Se il valore NON è null, il dispositivo sta comunicando correttamente
			tentativiFalliti.put(pojo.getId(),0); // Resetta il contatore dei fallimenti
			}
		}
}
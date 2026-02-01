package it.unipv.posfw.smartdab.core.service;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.enums.DispositivoStates;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.Dispositivo;
import it.unipv.posfw.smartdab.strategy.MalfunzionamentoStrategy;

public class GestoreMalfunzionamenti {
	
	private Map<Dispositivo, Integer> tentativiFalliti = new HashMap<>();
    
    //Associa il tipo di parametro alla sua strategia specifica presente nel file
	private Map<DispositivoParameter, MalfunzionamentoStrategy> mappeStrategie = new HashMap<>();
    
    //La strategia da usare se un parametro non è presente nel file di configurazione
	private MalfunzionamentoStrategy strategiaDefault;
	
	public GestoreMalfunzionamenti(MalfunzionamentoStrategy strategiaDefault) {
		this.strategiaDefault = strategiaDefault;
		caricaConfigurazione(); // Metodo per leggere il file .properties
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
             
             //Trasforma la stringa in un oggetto reale. 
             //Class.forName trova la classe, getDeclaredConstructor().newInstance() crea l'istanza
             MalfunzionamentoStrategy s = (MalfunzionamentoStrategy) Class.forName(className).getDeclaredConstructor().newInstance();    
             
             // Converte la stringa della chiave nel valore corrispondente dell'Enum DispositivoParameter
             mappeStrategie.put(DispositivoParameter.valueOf(key), s);
            }
			} else {
				System.err.println("File strategie.properties non trovato, verrà usata la strategia di default");
			}
        } catch (Exception e) {
            System.err.println("Errore caricamento strategie: " + e.getMessage());
        }	
	}
	
	public void controllaConnessione(Dispositivo d, Object value) {
		if (value == null) {
	     
		    //Se il dispositivo è già disabilitato, usciamo subito
	        if (d.getState() != DispositivoStates.DISABLED) {
	    
			////cerca d nella mappa, se lo trova restituisce il numero di fallimenti salvati precedentemente
			//se non lo trova restituisce 0
			int conteggio = tentativiFalliti.getOrDefault(d,0) + 1;   
			tentativiFalliti.put(d, conteggio);
			
			//Cerca nella mappa se esiste una strategia speciale
            //Se non la trova, usa quella standard
			MalfunzionamentoStrategy strategyDaUsare = mappeStrategie.getOrDefault(d.getTopic().getParameter(), strategiaDefault);
			
			//uso la strategia
			if(strategyDaUsare.deveDisabilitare(d, value, conteggio)) {
				disabilitaDispositivo(d);
			}
	        }
		} else {
			// Se il valore NON è null, il dispositivo sta comunicando correttamente
			tentativiFalliti.put(d,0); // Resetta il contatore dei fallimenti
			
			//se era disabilitato viene portato allo stato ALIVE
			if(d.getState() == DispositivoStates.DISABLED) {
				d.setState(DispositivoStates.ALIVE);
			}
		}
	}
	
	private void disabilitaDispositivo(Dispositivo d) {
		// Cambia lo stato interno in DISABLED
		d.setState(DispositivoStates.DISABLED);
		
		// Se il dispositivo è acceso lo spegne 
		if(d.isActive()) {
			d.switchDispositivo();
		}
	}
}
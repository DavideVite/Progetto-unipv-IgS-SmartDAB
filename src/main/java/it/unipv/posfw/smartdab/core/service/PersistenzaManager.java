package it.unipv.posfw.smartdab.core.service;

import java.util.prefs.Preferences;

/**
 * Classe per la gestione della persistenza leggera dei dati di sistema.
 * Utilizza le Java Preferences per memorizzare il PIN in modo che rimanga persistente tra i riavvii 
 * dell'applicazione.
 * * I dati vengono salvati in un nodo specifico del sistema operativo associato 
 * al package della classe.
 * * @author Beatrice Bertone
 * @version 1.0
 */

public class PersistenzaManager {
	
	//chiave identificativa con cui salviamo il dato nel computer
	private static final String PIN_KEY = "smartdab_pin";
	
	/**
	 * Memorizza permanentemente il PIN nel registro delle preferenze dell'utente.
	 * * @param pin La stringa del PIN da salvare.
	 */
	public static void salvaPin(String pin) {
		Preferences prefs = Preferences.userNodeForPackage(PersistenzaManager.class);
        prefs.put(PIN_KEY, pin);
    }

	/**
     * Recupera il PIN precedentemente salvato dal registro di sistema.
     * * @return Il PIN salvato come {@link String}, oppure {@code null} se nessun PIN 
     * è mai stato configurato.
     */
    public static String caricaPin() {
        Preferences prefs = Preferences.userNodeForPackage(PersistenzaManager.class);
        return prefs.get(PIN_KEY, null);		
	}

}

package it.unipv.posfw.smartdab.core.service;

import java.util.prefs.Preferences;

public class PersistenzaManager {
	
	//etichetta con cui salviamo il dato nel computer
	private static final String PIN_KEY = "smartdab_pin";
	
	//metodo per salvare il pin
	public static void salvaPin(String pin) {
		// Ottiene il nodo delle preferenze associato alla classe PersistenzaManager
	    // Questo crea o recupera una cartella specifica nel sistema per memorizzare i dati
		Preferences prefs = Preferences.userNodeForPackage(PersistenzaManager.class);
        prefs.put(PIN_KEY, pin);
    }

    // Metodo per recuperare il PIN (restituisce null se non esiste)
    public static String caricaPin() {
        Preferences prefs = Preferences.userNodeForPackage(PersistenzaManager.class);
        return prefs.get(PIN_KEY, null);		
	}

}

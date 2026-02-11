package it.unipv.posfw.smartdab.core.domain.model.utente;

import it.unipv.posfw.smartdab.core.service.PersistenzaManager;

/**
 * Gestisce l'accesso al sistema.
 * Implementa una logica di autenticazione a due livelli:
 * Password Produttore: Utilizzata esclusivamente per la configurazione iniziale.
 * PIN Personale: Utilizzato per le operazioni successive.
 * 
 * *La classe si appoggia a {@link PersistenzaManager} per garantire che il PIN 
 * venga mantenuto anche dopo il riavvio dell'applicazione.
 * * @author Beatrice Bertone
 * @version 1.0
 */

public class Autenticazione {
     private final String passwordProduttore;
     private String pin;
    
     /**
      * Inizializza il modulo di autenticazione con la password del produttore.
      * All'avvio il PIN è nullo finché non viene configurato dall'utente.
      * * @param passwordProduttore.
      */
     public Autenticazione(String passwordProduttore) {
    	 this.passwordProduttore = passwordProduttore;
    	 this.pin = null;          
     }
     
 
	 public boolean verificaPassword(String pwd) {
		 return passwordProduttore.equals(pwd);
	 }
	
	 /**
	  * Permette di impostare il PIN per la prima volta.
	  * Richiede la validazione della password del produttore e un PIN di esattamente 5 caratteri.
	  * Una volta impostato, il PIN viene salvato in modo persistente.
	  * * @param PasswordP Password del produttore.
	  * @param nuovoPin Il nuovo PIN di 5 cifre da configurare.
	  * @return {@code true} se l'operazione ha successo, {@code false} se la password è errata o il PIN non è valido.
	  */
	 public boolean impostaPinIniziale(String PasswordP, String nuovoPin ) {
		 if(verificaPassword(PasswordP) && nuovoPin.length() == 5) {
			 this.pin = nuovoPin;
			 PersistenzaManager.salvaPin(nuovoPin);
			 return true;
		 }
		 return false;
	 }
	 
	 	 
	 public boolean verificaPin(String pinInserito) {
		 if(this.pin == null) {
			 this.pin = PersistenzaManager.caricaPin();
		 }
		 
		 if (this.pin == null || pinInserito == null) {
	            return false;
	        }
		 
		 return pin.equals(pinInserito);
	 }
     
	 /**
	  * Consente la modifica del PIN esistente.
	  * Richiede la conoscenza del PIN attuale e la validazione del formato del nuovo PIN.
	  * * @param vecchioPin.
	  * @param nuovoPin Il nuovo PIN di 5 cifre da impostare.
	  * @return {@code true} se la modifica è avvenuta con successo, {@code false} in caso di PIN errato o formato non valido.
	  */	 
	 public boolean modificaPin(String vecchioPin, String nuovoPin) {
		 if(verificaPin(vecchioPin) && nuovoPin.length() == 5) {
			 this.pin = nuovoPin;
			 PersistenzaManager.salvaPin(nuovoPin);
			 return true;
		 }
		 return false;
	 }	 
     
}



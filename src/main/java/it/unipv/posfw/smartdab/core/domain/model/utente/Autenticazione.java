package it.unipv.posfw.smartdab.core.domain.model.utente;

public class Autenticazione {
     private final String passwordProduttore;
     private String pin;
     
     public Autenticazione(String passwordProduttore) {
    	 this.passwordProduttore = passwordProduttore;
    	 this.pin = null;          //all'inizio il pin non esiste
     }
     
 
	 public boolean verificaPassword(String pwd) {
		 return passwordProduttore.equals(pwd);
	 }
	
	 public boolean impostaPinIniziale(String PasswordP, String nuovoPin ) {
		 if(verificaPassword(PasswordP) && nuovoPin.length() == 5) {
			 this.pin = nuovoPin;
			 return true;
		 }
		 return false;
	 }
	 
	 	 
	 public boolean verificaPin(String pinInserito) {
		 return pin.equals(pinInserito);
	 }
     
	 
	 public boolean modificaPin(String vecchioPin, String nuovoPin) {
		 if(verificaPin(vecchioPin) && nuovoPin.length() == 5) {
			 this.pin = nuovoPin;
			 return true;
		 }
		 return false;
	 }	 
     
}



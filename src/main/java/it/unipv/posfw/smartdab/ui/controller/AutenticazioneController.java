package it.unipv.posfw.smartdab.ui.controller;

import java.awt.CardLayout;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import it.unipv.posfw.smartdab.core.service.PersistenzaManager;

public class AutenticazioneController {
	
	private JPanel container;
	private CardLayout layout;
	private Runnable azioneSuccesso;  //per verificare se l'autenticazione è andata a buon fine e passare a finestra stanze
	
	public AutenticazioneController(JPanel container, CardLayout layout) {
		this.container = container;
		this.layout = layout;
	}
	
	//metodo per aprire il main frame 
	public void setOnSuccess(Runnable azione) {
		this.azioneSuccesso = azione;
	}
	
	public void verificaPasswordProduttore (String password) {		
		if(password.equals("prod123")) {
			layout.show(container, "PIN_SETUP");
		} else {
			JOptionPane.showMessageDialog(container, "Password Produttore errata", "Accesso negato", JOptionPane.ERROR_MESSAGE);			
		}
	}
	
	public void configuraNuovoPin(String pin) {
		// \\d{5} d è un digit (cifra numerica da 0 a 9), 5 è il quantificatore
		if(pin != null && pin.matches("\\d{5}") ) {
			PersistenzaManager.salvaPin(pin);
			if(azioneSuccesso != null) {
				azioneSuccesso.run(); //chiude login e apre main frame
			}
		} else {
			JOptionPane.showMessageDialog(container, "Il PIN deve essere composto da 5 cifre numeriche", "Errore Formato", JOptionPane.WARNING_MESSAGE);
		}
	}
	
	public void verificaPinEsistente(String pinInserito) {
	    String pinSalvato = PersistenzaManager.caricaPin();
	    if(pinInserito.equals(pinSalvato)) {
	        if(azioneSuccesso != null) azioneSuccesso.run();
	    } else {
	        JOptionPane.showMessageDialog(container, "PIN errato!");
	    }
	}

}


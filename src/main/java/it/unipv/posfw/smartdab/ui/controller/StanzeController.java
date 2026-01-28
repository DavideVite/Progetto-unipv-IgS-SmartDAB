package it.unipv.posfw.smartdab.ui.controller;

import java.awt.CardLayout;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import it.unipv.posfw.smartdab.core.domain.model.casa.Stanza;
import it.unipv.posfw.smartdab.ui.view.stanze.StanzeFormPanel;
import it.unipv.posfw.smartdab.ui.view.stanze.StanzePanel;

public class StanzeController {
	    private StanzePanel elencoPanel;
	    private StanzeFormPanel formPanel;
	    private JPanel container; //pannello che usa CardLayout
	    private CardLayout layout;
	    private int rigaSelezionata = -1;  //-1 significa nuovo inserimento

	    public StanzeController(JPanel container, CardLayout layout) {
	    	this.container = container;
	    	this.layout = layout;
	    }
	    
	    public void setViews(StanzePanel elenco, StanzeFormPanel form) {
	    	this.elencoPanel = elenco;
	    	this.formPanel = form;
	    }
	  
	    public void mostraFormInserimento() {
	    	this.rigaSelezionata = -1;  //reset per nuova stanza
	    	formPanel.preparaPerInserimento();
	    	layout.show(container, "FORM_STANZA"); 	
	    }
	    
	    public void gestisceSelezioneStanza(String id, String nome, String mq) {
	    	this.rigaSelezionata = elencoPanel.getTabella().getSelectedRow();
	    	String[] opzioni = {"Modifica", "Elimina", "Annulla"};
	    	int scelta = JOptionPane.showOptionDialog(null, 
	    			"Scegli l'operazione: ", 
	    			"Gestione Stanza", 
	    			JOptionPane.DEFAULT_OPTION, 
	    			JOptionPane.QUESTION_MESSAGE,
	    			null, opzioni, opzioni[0]);
	    	
	    	if(scelta == 0) { //MODIFICA
	    		formPanel.preparaPerModifica(nome, mq);
	    		layout.show(container, "FORM_STANZA");
	    	} else if (scelta == 1) {  //ELIMINA
	    		int riga = elencoPanel.getTabella().getSelectedRow();
	    		int conferma = JOptionPane.showConfirmDialog(null,  "Sei sicuro di voler eliminare " + nome + "?", "Conferma", JOptionPane.YES_NO_OPTION);
	    		if (conferma == JOptionPane.YES_OPTION) {
	    			elencoPanel.rimuoviRigaTabella(riga);
	    			JOptionPane.showMessageDialog(null,  "Stanza eliminata");
	    		}
	    	}
	    }
	    
	    public void salvaStanza() {
	    	String id = formPanel.getId();
	    	String nome = formPanel.getNome();
	    	String mqStr = formPanel.getMq();
	    	
	    	if(nome.isEmpty() || mqStr.isEmpty()) {
	    		JOptionPane.showMessageDialog(null,  "Compila tutti i campi", "Errore", JOptionPane.ERROR_MESSAGE);
	    		return;
	    	}
	    	
	    	try {
	    		double mq = Double.parseDouble(mqStr);
	    		
	    		if(rigaSelezionata == -1) {
	    			//caso 1: nuova stanza
	    			Stanza nuovaStanza = new Stanza(id, nome,mq);
	    			
	    			elencoPanel.aggiungiRigaTabella(nuovaStanza.getId(), nuovaStanza.getNome(), nuovaStanza.getMq());
	    		} else {
	    			//caso 2: modifica
	    			elencoPanel.aggiornaRigaTabella(rigaSelezionata, nome, mq);
	    		}
	    		
	    		//torna alla lista
	    		layout.show(container,  "LISTA_STANZE");
	    		JOptionPane.showMessageDialog(null, "Operazione completata");
	    	} catch (NumberFormatException e) {
	    		JOptionPane.showMessageDialog(null,  "Inserisci un numero valido per i mq", "Errore", JOptionPane.ERROR_MESSAGE);
	    	}
	    }
	    
	    public void eliminaStanza() {
	        int conferma = JOptionPane.showConfirmDialog(null, 
	            "Sei sicuro di voler eliminare questa stanza?", 
	            "Conferma Eliminazione", 
	            JOptionPane.YES_NO_OPTION);

	        if (conferma == JOptionPane.YES_OPTION) {
                //torniamo alla lista
	            layout.show(container, "LISTA_STANZE");
	            
	            JOptionPane.showMessageDialog(null, "Stanza eliminata correttamente.");
	        }
	    }
	    
	    public void annullaStanza() {
	        layout.show(container, "LISTA_STANZE");
	    }
}


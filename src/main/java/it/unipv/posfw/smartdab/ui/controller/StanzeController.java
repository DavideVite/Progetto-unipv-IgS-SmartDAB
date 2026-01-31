package it.unipv.posfw.smartdab.ui.controller;

import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.*;

import java.util.Map;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.model.casa.Stanza;
import it.unipv.posfw.smartdab.core.domain.model.parametro.IParametroValue;
import it.unipv.posfw.smartdab.core.service.GestoreStanze;
import it.unipv.posfw.smartdab.core.service.ParametroManager;
import it.unipv.posfw.smartdab.factory.ParametroValueFactory;
import it.unipv.posfw.smartdab.ui.view.stanze.StanzeFormPanel;
import it.unipv.posfw.smartdab.ui.view.stanze.StanzePanel;

public class StanzeController {
	    private StanzePanel elencoPanel;
	    private StanzeFormPanel formPanel;
	    private JPanel container; //pannello che usa CardLayout
	    private CardLayout layout;
	    private GestoreStanze gestoreStanze;
	    private ParametroManager parametroManager;
	    private int rigaSelezionata = -1;  //-1 significa nuovo inserimento

	    public StanzeController(JPanel container, CardLayout layout, GestoreStanze gestoreStanze, ParametroManager parametroManager) {
	    	this.container = container;
	    	this.layout = layout;
	    	this.gestoreStanze = gestoreStanze;
	    	this.parametroManager = parametroManager;
	    }
	    
	    public void setViews(StanzePanel elenco, StanzeFormPanel form) {
	    	this.elencoPanel = elenco;
	    	this.formPanel = form;
	    	caricaStanzeInTabella();
	    }

	    private void caricaStanzeInTabella() {
	    	for (Stanza s : gestoreStanze.visualizzaStanze()) {
	    		elencoPanel.aggiungiRigaTabella(s.getId(), s.getNome(), s.getMq());
	    	}
	    }
	  
	    public void mostraFormInserimento() {
	    	this.rigaSelezionata = -1;  //reset per nuova stanza
	    	formPanel.preparaPerInserimento();
	    	layout.show(container, "FORM_STANZA"); 	
	    }
	    
	    public void gestisceSelezioneStanza(String id, String nome, String mq) {
	    	this.rigaSelezionata = elencoPanel.getTabella().getSelectedRow();
	    	String[] opzioni = {"Modifica", "Elimina", "Imposta Parametro", "Annulla"};
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
	    			boolean successo = gestoreStanze.eliminaStanza(nome);
	    			if(successo) {
	    				elencoPanel.rimuoviRigaTabella(riga);
	    				JOptionPane.showMessageDialog(null,  "Stanza eliminata");
	    			} else {
	    				JOptionPane.showMessageDialog(null, "Errore: impossibile eliminare la stanza (contiene dispositivi?)", "Errore", JOptionPane.ERROR_MESSAGE);
	    			}
	    		}
	    	} else if (scelta == 2) {  //IMPOSTA PARAMETRO MANUALE
	    		mostraDialogParametroManuale(id);
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
	    			//caso 1: nuova stanza - usa GestoreStanze per salvare
	    			Stanza nuovaStanza = gestoreStanze.creaStanza(nome, mq);

	    			if(nuovaStanza != null) {
	    				elencoPanel.aggiungiRigaTabella(nuovaStanza.getId(), nuovaStanza.getNome(), nuovaStanza.getMq());
	    			} else {
	    				JOptionPane.showMessageDialog(null, "Errore: esiste gia' una stanza con questo nome", "Errore", JOptionPane.ERROR_MESSAGE);
	    				return;
	    			}
	    		} else {
	    			//caso 2: modifica - usa GestoreStanze per aggiornare nome e mq
	    			String nomeOriginale = elencoPanel.getTabella().getValueAt(rigaSelezionata, 1).toString();
	    			boolean successo = gestoreStanze.modificaStanza(nomeOriginale, nome, mq);

	    			if(successo) {
	    				elencoPanel.aggiornaRigaTabella(rigaSelezionata, nome, mq);
	    			} else {
	    				JOptionPane.showMessageDialog(null, "Errore: impossibile modificare la stanza", "Errore", JOptionPane.ERROR_MESSAGE);
	    				return;
	    			}
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

	    public void mostraDettagliStanza(String id, String nome) {
	    	Stanza stanza = gestoreStanze.cercaStanzaPerId(id);
	    	if (stanza != null) {
	    		Map<String, Double> parametri = stanza.getParametri();
	    		elencoPanel.getDettaglioPanel().mostraParametri(nome, parametri);
	    	} else {
	    		elencoPanel.getDettaglioPanel().pulisci();
	    	}
	    }

	    private void mostraDialogParametroManuale(String stanzaId) {
	    	// ComboBox parametro
	    	JComboBox<DispositivoParameter> comboParametro = new JComboBox<>(DispositivoParameter.values());

	    	// Pannello valore con CardLayout (stesso pattern di ScenarioFormPanel)
	    	CardLayout valorePanelLayout = new CardLayout();
	    	JPanel panelValore = new JPanel(valorePanelLayout);
	    	JTextField txtNumerico = new JTextField(10);
	    	JCheckBox chkBooleano = new JCheckBox("Attivo");
	    	JComboBox<String> comboEnum = new JComboBox<>();
	    	panelValore.add(txtNumerico, "NUMERIC");
	    	panelValore.add(chkBooleano, "BOOLEAN");
	    	panelValore.add(comboEnum, "ENUM");

	    	// Listener per aggiornare il campo valore in base al parametro selezionato
	    	comboParametro.addActionListener(e -> {
	    		DispositivoParameter param = (DispositivoParameter) comboParametro.getSelectedItem();
	    		if (param == null) return;
	    		switch (param.getType()) {
	    			case NUMERIC:
	    				valorePanelLayout.show(panelValore, "NUMERIC");
	    				txtNumerico.setText("");
	    				break;
	    			case BOOLEAN:
	    				valorePanelLayout.show(panelValore, "BOOLEAN");
	    				chkBooleano.setSelected(false);
	    				break;
	    			case ENUM:
	    				valorePanelLayout.show(panelValore, "ENUM");
	    				comboEnum.removeAllItems();
	    				if (param.getAllowedValues() != null) {
	    					for (String val : param.getAllowedValues()) {
	    						comboEnum.addItem(val);
	    					}
	    				}
	    				break;
	    		}
	    	});
	    	// Inizializza il pannello valore
	    	comboParametro.setSelectedIndex(0);

	    	// Costruisci il pannello del dialog
	    	JPanel panel = new JPanel(new GridBagLayout());
	    	GridBagConstraints gbc = new GridBagConstraints();
	    	gbc.insets = new Insets(5, 5, 5, 5);
	    	gbc.fill = GridBagConstraints.HORIZONTAL;

	    	gbc.gridx = 0; gbc.gridy = 0;
	    	panel.add(new JLabel("Parametro:"), gbc);
	    	gbc.gridx = 1;
	    	panel.add(comboParametro, gbc);

	    	gbc.gridx = 0; gbc.gridy = 1;
	    	panel.add(new JLabel("Valore:"), gbc);
	    	gbc.gridx = 1;
	    	panel.add(panelValore, gbc);

	    	int risultato = JOptionPane.showConfirmDialog(null, panel,
	    			"Imposta Parametro Manuale", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

	    	if (risultato != JOptionPane.OK_OPTION) return;

	    	// Leggi il valore inserito
	    	DispositivoParameter paramScelto = (DispositivoParameter) comboParametro.getSelectedItem();
	    	if (paramScelto == null) return;

	    	IParametroValue valore;
	    	switch (paramScelto.getType()) {
	    		case NUMERIC:
	    			String txt = txtNumerico.getText().trim();
	    			if (txt.isEmpty()) {
	    				JOptionPane.showMessageDialog(null, "Inserisci un valore numerico", "Errore", JOptionPane.ERROR_MESSAGE);
	    				return;
	    			}
	    			try {
	    				Double.parseDouble(txt);
	    			} catch (NumberFormatException e) {
	    				JOptionPane.showMessageDialog(null, "Valore numerico non valido", "Errore", JOptionPane.ERROR_MESSAGE);
	    				return;
	    			}
	    			valore = ParametroValueFactory.create(paramScelto, txt);
	    			break;
	    		case BOOLEAN:
	    			valore = ParametroValueFactory.create(paramScelto, chkBooleano.isSelected());
	    			break;
	    		case ENUM:
	    			String valEnum = (String) comboEnum.getSelectedItem();
	    			if (valEnum == null) {
	    				JOptionPane.showMessageDialog(null, "Seleziona un valore", "Errore", JOptionPane.ERROR_MESSAGE);
	    				return;
	    			}
	    			valore = ParametroValueFactory.create(paramScelto, valEnum);
	    			break;
	    		default:
	    			return;
	    	}

	    	// Invoca il core
	    	boolean successo = parametroManager.impostaParametro(stanzaId, paramScelto, valore);
	    	if (successo) {
	    		JOptionPane.showMessageDialog(null, "Parametro impostato correttamente");
	    	} else {
	    		JOptionPane.showMessageDialog(null,
	    				"Impossibile impostare il parametro.\nVerifica che la stanza abbia un attuatore attivo per questo parametro.",
	    				"Errore", JOptionPane.ERROR_MESSAGE);
	    	}
	    }
}


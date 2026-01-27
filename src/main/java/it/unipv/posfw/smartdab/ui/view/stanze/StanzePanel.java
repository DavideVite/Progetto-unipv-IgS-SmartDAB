package it.unipv.posfw.smartdab.ui.view.stanze;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import it.unipv.posfw.smartdab.ui.controller.StanzeAction;

public class StanzePanel extends JPanel {	
	private DefaultTableModel modello;  //variabile di istanza
	private JTable tabella;
	private StanzeAction controller;
	
	public StanzePanel(StanzeAction controller) {
		this.controller = controller;
		
		setLayout(new BorderLayout(10,10));
		
		JLabel titolo = new JLabel("Elenco Stanze (clicca una riga per modificare o eliminare la stanza)", SwingConstants.CENTER);
		add(titolo, BorderLayout.NORTH);	    
		
		String[] colonne = {"id", "nome", "mq"};
        
		modello = new DefaultTableModel(colonne, 0);
		tabella = new JTable(modello);
		
		add(new JScrollPane(tabella), BorderLayout.CENTER);
		
		setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		tabella.setRowHeight(30);
		tabella.setShowVerticalLines(false);
		
		JPanel pnlSud = new JPanel();
		JButton btnNuovaStanza = new JButton("Aggiungi Nuova Stanza");
		pnlSud.add(btnNuovaStanza);
		add(pnlSud, BorderLayout.SOUTH);
		
		btnNuovaStanza.addActionListener(e -> {
			if (controller != null) {
				controller.mostraFormInserimento();
			} else {
				System.out.println("Errore: Il controller è null");
			}
		});
		
		tabella.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 1) {
					int riga = tabella.getSelectedRow();
					if (riga != -1) {
		                // Recuperiamo i dati dalla riga selezionata
		                String id = tabella.getValueAt(riga, 0).toString();
		                String nome = tabella.getValueAt(riga, 1).toString();
		                String mq = tabella.getValueAt(riga, 2).toString();
		                
		                controller.gestisceSelezioneStanza(id, nome, mq);
				}
			}
		  }
		});
	}
	
	public void aggiungiRigaTabella(String id, String nome, double mq) {
		modello.addRow(new Object[] {id, nome, mq});
	}
	
	public void rimuoviRigaTabella(int riga) {
		if (riga != -1) {
			modello.removeRow(riga);
		}
	}
	public void aggiornaRigaTabella(int riga, String nome, double mq) {
		modello.setValueAt(nome, riga, 1);
		modello.setValueAt(mq, riga, 2);		
	}
	
	public JTable getTabella() {
		return tabella;
	}
}

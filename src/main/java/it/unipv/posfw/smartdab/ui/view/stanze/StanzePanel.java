package it.unipv.posfw.smartdab.ui.view.stanze;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import it.unipv.posfw.smartdab.ui.controller.StanzeController;

public class StanzePanel extends JPanel {
	private DefaultTableModel modello;  //variabile di istanza
	private JTable tabella;
	private StanzeController controller;
	private StanzeDettaglioPanel dettaglioPanel;

	// Costruttore vuoto per compatibilità con MainPanel
	public StanzePanel() {
		this(null);
	}

	public StanzePanel(StanzeController controller) {
		this.controller = controller;

		setLayout(new BorderLayout(10,10));

		JLabel titolo = new JLabel("Elenco Stanze (clicca per dettagli, doppio click per modificare)", SwingConstants.CENTER);
		add(titolo, BorderLayout.NORTH);

		String[] colonne = {"id", "nome", "mq"};

		modello = new DefaultTableModel(colonne, 0) {
		    @Override
		    public boolean isCellEditable(int row, int column) {
		        return false; // Tutte le celle diventano non editabili col click
		    }
		};
		tabella = new JTable(modello);

		tabella.setRowHeight(30);
		tabella.setShowVerticalLines(false);

		// Pannello dettagli parametri
		dettaglioPanel = new StanzeDettaglioPanel();

		// SplitPane: tabella sopra, dettagli sotto
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				new JScrollPane(tabella), dettaglioPanel);
		splitPane.setResizeWeight(0.6);
		splitPane.setDividerLocation(200);
		add(splitPane, BorderLayout.CENTER);

		setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		JPanel pnlSud = new JPanel();
		JButton btnNuovaStanza = new JButton("Aggiungi Nuova Stanza");
		pnlSud.add(btnNuovaStanza);
		add(pnlSud, BorderLayout.SOUTH);

		btnNuovaStanza.addActionListener(e -> {
			if (this.controller != null) {
				this.controller.mostraFormInserimento();
			}
		});

		tabella.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int riga = tabella.getSelectedRow();
				if (riga == -1) return;

				String id = tabella.getValueAt(riga, 0).toString();
				String nome = tabella.getValueAt(riga, 1).toString();
				String mq = tabella.getValueAt(riga, 2).toString();

				if (e.getClickCount() == 1) {
					// Single click: mostra dettagli parametri
					if (StanzePanel.this.controller != null) {
						StanzePanel.this.controller.mostraDettagliStanza(id, nome);
					}
				} else if (e.getClickCount() == 2) {
					// Double click: modifica/elimina/imposta parametro
					if (StanzePanel.this.controller != null) {
						StanzePanel.this.controller.gestisceSelezioneStanza(id, nome, mq);
					}
				}
			}
		});
	}

	public void setController(StanzeController controller) {
		this.controller = controller;
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

	public StanzeDettaglioPanel getDettaglioPanel() {
		return dettaglioPanel;
	}
}

package it.unipv.posfw.smartdab.ui.view.stanze;

import java.awt.*;
import javax.swing.*;

import it.unipv.posfw.smartdab.ui.controller.StanzeController;

public class StanzeFormPanel extends JPanel{
	private JTextField txtNome;
	private JTextField txtMq;
	private JButton btnSalva;
	private JButton btnElimina;
	private JButton btnAnnulla;
	private JLabel lblTitolo;

	private StanzeController controller;

	public StanzeFormPanel(StanzeController controller) {
		this.controller = controller;
		
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10,10,10,10);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		lblTitolo = new JLabel("Nuova Stanza", SwingConstants.CENTER);
		gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
		add(lblTitolo, gbc);
		
		//campo nome
		gbc.gridwidth = 1; gbc.gridy = 1; gbc.gridx = 0;
		add(new JLabel("Nome:"), gbc);
		txtNome = new JTextField(15);
		gbc.gridx = 1;
		add(txtNome, gbc);
		
		//campo mq
		gbc.gridy = 2; gbc.gridx = 0;
		add(new JLabel("Metri Quadri:"), gbc);
		txtMq = new JTextField(10);
		gbc.gridx = 1;
		add(txtMq, gbc);
		
		//pannello bottoni
		JPanel pnlBottoni = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
		btnSalva = new JButton("Salva");
		btnElimina = new JButton("Elimina Stanza");
		btnAnnulla = new JButton("Annulla");
		
		pnlBottoni.add(btnSalva);
		pnlBottoni.add(btnElimina);
		pnlBottoni.add(btnAnnulla);
		
		gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
		add(pnlBottoni, gbc);
		
		btnSalva.addActionListener(e -> controller.salvaStanza());
		btnElimina.addActionListener(e -> controller.eliminaStanza());
		btnAnnulla.addActionListener(e -> controller.annullaStanza());
		
		setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
	}
	
	//metodi per il controller
	public void preparaPerInserimento () {
		lblTitolo.setText("Aggiungi Nuova Stanza");
		txtNome.setText("");
		txtMq.setText("");
		btnElimina.setVisible(false);
		btnSalva.setText("Salva");
	}
	
	public void preparaPerModifica(String nome, String mq) {
		lblTitolo.setText("Modifica Stanza " + nome);
		txtNome.setText(nome);
		txtMq.setText(mq);
		btnElimina.setVisible(false);
		btnSalva.setText("Aggiorna");
	}
	
	public String getNome() {
		return txtNome.getText();
	}
	
	public String getMq() {
		return txtMq.getText();
	}
	
	public String getId() {
		return txtMq.getText();
	}
}

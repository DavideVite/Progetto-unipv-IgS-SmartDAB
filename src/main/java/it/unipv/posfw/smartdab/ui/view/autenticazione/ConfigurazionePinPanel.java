package it.unipv.posfw.smartdab.ui.view.autenticazione;


import java.awt.*;
import javax.swing.*;

import it.unipv.posfw.smartdab.ui.controller.AutenticazioneController;

public class ConfigurazionePinPanel extends JPanel{
	
	private JTextField txtPin;
	private JButton btnConferma;
	private AutenticazioneController controller;
	
	public ConfigurazionePinPanel (AutenticazioneController controller) {
		this.controller = controller;
		
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10,10,10,10);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		JLabel titolo = new JLabel("Configurazione PIN", SwingConstants.CENTER);
		gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
		add(titolo, gbc);
		
		JLabel istruzioni = new JLabel("Inserisci un PIN numerico di 5 cifre:", SwingConstants.CENTER);
		gbc.gridy = 1;
		add(istruzioni, gbc);
		
		txtPin = new JTextField(10);
		txtPin.setHorizontalAlignment(JTextField.CENTER);
		gbc.gridy = 2;
		add(txtPin, gbc);
		
		btnConferma = new JButton("Configura PIN e accedi");
		btnConferma.setPreferredSize(new Dimension(200, 40));
		gbc.gridy = 3;
		add(btnConferma, gbc);
		
		btnConferma.addActionListener(e -> {
			String pinInserito = txtPin.getText();
			controller.configuraNuovoPin(pinInserito);
		});
	}
	public void svuotaCampo() {
		txtPin.setText("");
	}

}

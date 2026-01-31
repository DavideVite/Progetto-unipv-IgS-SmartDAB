package it.unipv.posfw.smartdab.ui.view.autenticazione;

import java.awt.*;
import javax.swing.*;

import it.unipv.posfw.smartdab.ui.controller.AutenticazioneController;

public class LoginProduttorePanel extends JPanel{
	
	private JPasswordField txtPassword; //campo di testo oscurato per password
	private JButton btnAccedi;
	private AutenticazioneController controller;
	
	public LoginProduttorePanel(AutenticazioneController controller) {
		this.controller = controller;
		
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10,10,10,10);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		JLabel titolo = new JLabel("Configurazione del sistema", SwingConstants.CENTER);
		gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
		add(titolo, gbc);
		
		gbc.gridy = 1; gbc.gridwidth = 1;
		add(new JLabel("Password produttore:"), gbc);
		
		txtPassword = new JPasswordField(15);
		gbc.gridx = 1;
		add(txtPassword, gbc);
		
		btnAccedi = new JButton("Accedi");
		gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
		add(btnAccedi, gbc);
		
		btnAccedi.addActionListener(e -> {
			String pass = new String(txtPassword.getPassword());
		    controller.verificaPasswordProduttore(pass);
		});
		}
	
	public void svuotaCampo() {
		txtPassword.setText("");
	}

}

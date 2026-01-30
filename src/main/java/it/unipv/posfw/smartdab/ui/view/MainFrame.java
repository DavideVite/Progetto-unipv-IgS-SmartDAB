package it.unipv.posfw.smartdab.ui.view;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import it.unipv.posfw.smartdab.core.service.GestoreStanze;

public class MainFrame extends JFrame {

    private MainPanel mainPanel;

    public MainFrame(GestoreStanze gestoreStanze) {
        super("SmartDAB - Sistema Domotico");
        initComponents(gestoreStanze);
    }

    private void initComponents(GestoreStanze gestoreStanze) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        mainPanel = new MainPanel(gestoreStanze);
        add(mainPanel, BorderLayout.CENTER);
    }

    public MainPanel getMainPanel() {
        return mainPanel;
    }
}

package it.unipv.posfw.smartdab.ui.view;

import java.awt.BorderLayout;

import javax.swing.JFrame;

import it.unipv.posfw.smartdab.core.service.GestoreStanze;
import it.unipv.posfw.smartdab.core.service.ParametroManager;

public class MainFrame extends JFrame {

    private MainPanel mainPanel;

    public MainFrame(GestoreStanze gestoreStanze, ParametroManager parametroManager) {
        super("SmartDAB - Sistema Domotico");
        initComponents(gestoreStanze, parametroManager);
    }

    private void initComponents(GestoreStanze gestoreStanze, ParametroManager parametroManager) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        mainPanel = new MainPanel(gestoreStanze, parametroManager);
        add(mainPanel, BorderLayout.CENTER);
    }

    public MainPanel getMainPanel() {
        return mainPanel;
    }
}

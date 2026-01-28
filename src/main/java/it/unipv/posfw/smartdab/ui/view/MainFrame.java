package it.unipv.posfw.smartdab.ui.view;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private MainPanel mainPanel;

    public MainFrame() {
        super("SmartDAB - Sistema Domotico");
        initComponents();
    }

    private void initComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        mainPanel = new MainPanel();
        add(mainPanel, BorderLayout.CENTER);
    }

    public MainPanel getMainPanel() {
        return mainPanel;
    }
}

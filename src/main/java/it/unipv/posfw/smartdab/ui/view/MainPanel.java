package it.unipv.posfw.smartdab.ui.view;

import it.unipv.posfw.smartdab.ui.view.scenari.ScenariPanel;
import it.unipv.posfw.smartdab.ui.view.stanze.StanzePanel;

import javax.swing.*;
import java.awt.*;

public class MainPanel extends JPanel {

    private JTabbedPane tabbedPane;
    private StanzePanel stanzePanel;
    private ScenariPanel scenariPanel;

    public MainPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        tabbedPane = new JTabbedPane();
        stanzePanel = new StanzePanel();
        scenariPanel = new ScenariPanel();

        tabbedPane.addTab("Stanze", stanzePanel);
        tabbedPane.addTab("Scenari", scenariPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    public StanzePanel getStanzePanel() {
        return stanzePanel;
    }

    public ScenariPanel getScenariPanel() {
        return scenariPanel;
    }
}

package it.unipv.posfw.smartdab.ui.view;

import it.unipv.posfw.smartdab.ui.view.dispositivi.DispositivoPanel;
import it.unipv.posfw.smartdab.ui.view.scenari.ScenariPanel;
import it.unipv.posfw.smartdab.ui.view.stanze.StanzePanel;

import javax.swing.*;
import java.awt.*;

public class MainPanel extends JPanel {

    private JTabbedPane tabbedPane;
    private StanzePanel stanzePanel;
    private ScenariPanel scenariPanel;
    private DispositivoPanel dispositivoPanel;

    public MainPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        tabbedPane = new JTabbedPane();
        stanzePanel = new StanzePanel();
        scenariPanel = new ScenariPanel();
        dispositivoPanel = new DispositivoPanel();

        tabbedPane.addTab("Stanze", stanzePanel);
        tabbedPane.addTab("Scenari", scenariPanel);
        tabbedPane.addTab("Dispositivi", dispositivoPanel);

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

    public DispositivoPanel getDispositivoPanel() {
        return dispositivoPanel;
    }
}

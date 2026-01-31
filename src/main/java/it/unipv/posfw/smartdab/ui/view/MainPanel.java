package it.unipv.posfw.smartdab.ui.view;

import it.unipv.posfw.smartdab.ui.view.dispositivi.DispositivoPanel;
import it.unipv.posfw.smartdab.ui.view.scenari.ScenariPanel;
import it.unipv.posfw.smartdab.ui.view.stanze.StanzeFormPanel;
import it.unipv.posfw.smartdab.ui.view.stanze.StanzePanel;

import javax.swing.*;
import java.awt.*;

public class MainPanel extends JPanel {

    private JTabbedPane tabbedPane;
    private JPanel stanzeContainer;
    private CardLayout stanzeLayout;
    private StanzePanel stanzePanel;
    private StanzeFormPanel stanzeFormPanel;
    private ScenariPanel scenariPanel;
    private DispositivoPanel dispositivoPanel;

    public MainPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        tabbedPane = new JTabbedPane();

        // Crea il container con CardLayout per le stanze
        stanzeLayout = new CardLayout();
        stanzeContainer = new JPanel(stanzeLayout);

        // Crea i pannelli (senza controller, verranno collegati dopo)
        stanzePanel = new StanzePanel();
        stanzeFormPanel = new StanzeFormPanel();

        // Aggiungi i pannelli al container con CardLayout
        stanzeContainer.add(stanzePanel, "LISTA_STANZE");
        stanzeContainer.add(stanzeFormPanel, "FORM_STANZA");

        scenariPanel = new ScenariPanel();
        dispositivoPanel = new DispositivoPanel();

        tabbedPane.addTab("Stanze", stanzeContainer);
        tabbedPane.addTab("Scenari", scenariPanel);
        tabbedPane.addTab("Dispositivi", dispositivoPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    public JPanel getStanzeContainer() {
        return stanzeContainer;
    }

    public CardLayout getStanzeLayout() {
        return stanzeLayout;
    }

    public StanzePanel getStanzePanel() {
        return stanzePanel;
    }

    public ScenariPanel getScenariPanel() {
        return scenariPanel;
    }

    public StanzeFormPanel getStanzeFormPanel() {
        return stanzeFormPanel;
    }

    public DispositivoPanel getDispositivoPanel() {
        return dispositivoPanel;
    }
}

package it.unipv.posfw.smartdab.ui.view;

import it.unipv.posfw.smartdab.core.service.GestoreStanze;
import it.unipv.posfw.smartdab.core.service.ParametroManager;
import it.unipv.posfw.smartdab.ui.controller.StanzeController;
import it.unipv.posfw.smartdab.ui.view.dispositivi.DispositivoPanel;
import it.unipv.posfw.smartdab.ui.view.scenari.ScenariPanel;
import it.unipv.posfw.smartdab.ui.view.stanze.StanzeFormPanel;
import it.unipv.posfw.smartdab.ui.view.stanze.StanzePanel;

import javax.swing.*;
import java.awt.*;

public class MainPanel extends JPanel {

    private JTabbedPane tabbedPane;
    private JPanel stanzeContainer;
    private StanzePanel stanzePanel;
    private ScenariPanel scenariPanel;
    private DispositivoPanel dispositivoPanel;
    private StanzeController stanzeController;

    public MainPanel(GestoreStanze gestoreStanze, ParametroManager parametroManager) {
        initComponents(gestoreStanze, parametroManager);
    }

    private void initComponents(GestoreStanze gestoreStanze, ParametroManager parametroManager) {
        setLayout(new BorderLayout());

        tabbedPane = new JTabbedPane();

        // Crea il container con CardLayout per le stanze
        CardLayout stanzeLayout = new CardLayout();
        stanzeContainer = new JPanel(stanzeLayout);

        // Crea il controller per le stanze
        stanzeController = new StanzeController(stanzeContainer, stanzeLayout, gestoreStanze, parametroManager);

        // Crea i pannelli delle stanze
        stanzePanel = new StanzePanel(stanzeController);
        StanzeFormPanel stanzeFormPanel = new StanzeFormPanel(stanzeController);

        // Collega le view al controller
        stanzeController.setViews(stanzePanel, stanzeFormPanel);

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

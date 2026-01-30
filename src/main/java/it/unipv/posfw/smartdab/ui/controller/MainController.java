package it.unipv.posfw.smartdab.ui.controller;

import it.unipv.posfw.smartdab.core.domain.model.casa.Casa;
import it.unipv.posfw.smartdab.core.service.GestoreStanze;
import it.unipv.posfw.smartdab.core.service.ParametroManager;
import it.unipv.posfw.smartdab.core.service.ScenarioManager;
import it.unipv.posfw.smartdab.infrastructure.messaging.EventBus;
import it.unipv.posfw.smartdab.ui.view.MainFrame;
import it.unipv.posfw.smartdab.ui.view.MainPanel;

import javax.swing.*;

public class MainController {

    private MainFrame mainFrame;
    private MainPanel mainPanel;

    // Services
    private Casa casa;
    private GestoreStanze gestoreStanze;
    private ScenarioManager scenarioManager;
    private ParametroManager parametroManager;

    // Sub-controllers
    private ScenariController scenariController;
    private DispositivoController dispositivoController;

    public MainController() {
        inizializzaModel();
        inizializzaView();
        inizializzaController();
    }

    private void inizializzaModel() {
        casa = new Casa();
        gestoreStanze = new GestoreStanze(casa);
        scenarioManager = new ScenarioManager();
        parametroManager = new ParametroManager(gestoreStanze, EventBus.getInstance());
    }

    private void inizializzaView() {
        mainFrame = new MainFrame(gestoreStanze, parametroManager);
        mainPanel = mainFrame.getMainPanel();
    }

    private void inizializzaController() {
        // Controller per Scenari
        // FIX: Aggiunto gestoreStanze per permettere a ScenariController di ottenere
        // la lista delle stanze disponibili per il form di creazione scenari
        scenariController = new ScenariController(
            mainPanel.getScenariPanel(),
            scenarioManager,
            parametroManager,
            gestoreStanze
        );

        // Controller per Dispositivi
        dispositivoController = new DispositivoController(
            mainPanel,
            gestoreStanze
        );

        // Listener per cambio tab
        mainPanel.getTabbedPane().addChangeListener(e -> {
            int selectedIndex = mainPanel.getTabbedPane().getSelectedIndex();
            onTabChanged(selectedIndex);
        });
    }

    private void onTabChanged(int tabIndex) {
        switch (tabIndex) {
            case 0: // Stanze
                break;
            case 1: // Scenari
                scenariController.aggiornaTabella();
                break;
            case 2: // Dispositivi
                if (dispositivoController != null) {
                    dispositivoController.aggiornaVista();
                }
                break;
        }
    }

    public void mostraApplicazione() {
        mainFrame.setVisible(true);
    }

    // Getters per accesso ai controller
    public ScenariController getScenariController() {
        return scenariController;
    }

    public DispositivoController getDispositivoController() {
        return dispositivoController;
    }

    public MainFrame getMainFrame() {
        return mainFrame;
    }

    public Casa getCasa() {
        return casa;
    }

    public GestoreStanze getGestoreStanze() {
        return gestoreStanze;
    }

    public ScenarioManager getScenarioManager() {
        return scenarioManager;
    }
}

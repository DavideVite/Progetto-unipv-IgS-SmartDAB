package it.unipv.posfw.smartdab.ui.controller;

import it.unipv.posfw.smartdab.core.domain.model.casa.Casa;
import it.unipv.posfw.smartdab.core.service.DispositiviManager;
import it.unipv.posfw.smartdab.core.service.DispositivoLoader;
import it.unipv.posfw.smartdab.core.service.GestoreStanze;
import it.unipv.posfw.smartdab.core.service.ParametroManager;
import it.unipv.posfw.smartdab.core.service.ScenarioManager;
import it.unipv.posfw.smartdab.infrastructure.messaging.EventBus;
import it.unipv.posfw.smartdab.ui.view.MainFrame;
import it.unipv.posfw.smartdab.ui.view.MainPanel;
import it.unipv.posfw.smartdab.ui.view.stanze.StanzeFormPanel;
import it.unipv.posfw.smartdab.ui.view.stanze.StanzePanel;

public class MainController {

    private MainFrame mainFrame;
    private MainPanel mainPanel;

    // Services
    private Casa casa;
    private GestoreStanze gestoreStanze;
    private ScenarioManager scenarioManager;
    private ParametroManager parametroManager;
    private DispositiviManager dispositiviManager;
    private DispositivoLoader dispositivoLoader;

    // Sub-controllers
    private StanzeController stanzeController;
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
        dispositiviManager = new DispositiviManager();

        EventBus eventBus = EventBus.getInstance(dispositiviManager);
        parametroManager = new ParametroManager(gestoreStanze, eventBus);

        // Carica i dispositivi POJO come oggetti dominio, collegandoli a Stanze e EventBus
        dispositivoLoader = new DispositivoLoader(gestoreStanze, eventBus);
        dispositivoLoader.caricaTutti(dispositiviManager.getDispositivi());
    }

    private void inizializzaView() {
        mainFrame = new MainFrame();
        mainPanel = mainFrame.getMainPanel();
    }

    private void inizializzaController() {
        // Controller per Stanze
        StanzePanel stanzePanel = mainPanel.getStanzePanel();
        StanzeFormPanel stanzeFormPanel = mainPanel.getStanzeFormPanel();

        stanzeController = new StanzeController(
            mainPanel.getStanzeContainer(),
            mainPanel.getStanzeLayout(),
            gestoreStanze,
            parametroManager
        );

        // Collega controller alle view
        stanzePanel.setController(stanzeController);
        stanzeFormPanel.setController(stanzeController);
        stanzeController.setViews(stanzePanel, stanzeFormPanel);

        // Controller per Scenari
        scenariController = new ScenariController(
            mainPanel.getScenariPanel(),
            scenarioManager,
            parametroManager,
            gestoreStanze
        );

        // Controller per Dispositivi
        dispositivoController = new DispositivoController(
            mainPanel,
            gestoreStanze,
            dispositiviManager,
            dispositivoLoader
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
    public StanzeController getStanzeController() {
        return stanzeController;
    }

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

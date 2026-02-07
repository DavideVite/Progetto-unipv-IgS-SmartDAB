package it.unipv.posfw.smartdab.ui.controller;

import it.unipv.posfw.smartdab.core.domain.model.casa.Casa;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.DispositiviBootstrap;
import it.unipv.posfw.smartdab.core.port.communication.ICommandSender;
import it.unipv.posfw.smartdab.core.port.persistence.IScenarioRepository;
import it.unipv.posfw.smartdab.core.service.DispositiviManager;
import it.unipv.posfw.smartdab.core.service.GestoreStanze;
import it.unipv.posfw.smartdab.core.service.ParametroManager;
import it.unipv.posfw.smartdab.core.service.ScenarioManager;
import it.unipv.posfw.smartdab.core.service.ScenariPredefinitInitializer;
import it.unipv.posfw.smartdab.infrastructure.messaging.EventBus;
import it.unipv.posfw.smartdab.infrastructure.messaging.adapter.CommandSenderAdapter;
import it.unipv.posfw.smartdab.infrastructure.persistence.adapter.ScenarioRepositoryAdapter;
import it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao.ScenarioDAO;
import it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao.ScenarioDAOImpl;
import it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao.StanzaDAO;
import it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao.StanzaDAOImpl;
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

    // Sub-controllers
    private StanzeController stanzeController;
    private ScenariController scenariController;
    private DispositivoController dispositivoController;

    public MainController() {
        inizializzaModel();
        inizializzaView();
        inizializzaController();
    }

    
    /**
     * COMPOSITION ROOT: Qui vengono create tutte le dipendenze e iniettate nei servizi.
     *
     * Flusso delle dipendenze (Architettura Esagonale):
     * 1. DAOs (infrastructure) - accesso dati puro
     * 2. Adapters (infrastructure) - implementano Output Ports del core
     * 3. Services (core) - ricevono Output Ports, non implementazioni concrete
     */
    private void inizializzaModel() {
        casa = new Casa();
        gestoreStanze = new GestoreStanze(casa);

        // ===== LAYER INFRASTRUCTURE: DAOs =====
        ScenarioDAO scenarioDAO = new ScenarioDAOImpl();
        StanzaDAO stanzaDAO = new StanzaDAOImpl();

        // ===== LAYER INFRASTRUCTURE: Adapters (Output Port implementations) =====
        IScenarioRepository scenarioRepository = new ScenarioRepositoryAdapter(scenarioDAO);

        // ===== LAYER CORE: Services =====
        scenarioManager = new ScenarioManager(scenarioRepository);

        // Inizializzazione scenari predefiniti (SRP: separato da ScenarioManager)
        ScenariPredefinitInitializer scenariInit = new ScenariPredefinitInitializer(scenarioManager);
        scenariInit.inizializza(stanzaDAO.readAllStanze());

        // DispositiviManager e EventBus
        dispositiviManager = new DispositiviManager();
        EventBus eventBus = EventBus.getInstance(dispositiviManager);
        DispositiviBootstrap dboot = new DispositiviBootstrap(dispositiviManager, eventBus);
        dboot.removeAllDispositivi();
        dboot.initDispositiviDb(casa.getStanze().iterator().next());

        // CommandSender Adapter per ParametroManager
        ICommandSender commandSender = new CommandSenderAdapter(eventBus);
        parametroManager = new ParametroManager(gestoreStanze, commandSender);
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
            dispositiviManager
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

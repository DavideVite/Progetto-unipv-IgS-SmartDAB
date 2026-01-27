package it.unipv.posfw.smartdab.ui;

import it.unipv.posfw.smartdab.core.domain.model.casa.Casa;
import it.unipv.posfw.smartdab.core.service.GestoreStanze;
import it.unipv.posfw.smartdab.core.service.ParametroManager;
import it.unipv.posfw.smartdab.core.service.ScenarioManager;
import it.unipv.posfw.smartdab.infrastructure.messaging.EventBus;
import it.unipv.posfw.smartdab.ui.controller.ScenariAction;
import it.unipv.posfw.smartdab.ui.controller.StanzeAction;
import it.unipv.posfw.smartdab.ui.view.MainFrame;

import javax.swing.*;

public class SmartDABApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 1. Inizializza Model
            Casa casa = new Casa();
            GestoreStanze gestoreStanze = new GestoreStanze(casa);
            ScenarioManager scenarioManager = new ScenarioManager();
            ParametroManager parametroManager = new ParametroManager(gestoreStanze, EventBus.getInstance());

            // 2. Crea View
            MainFrame frame = new MainFrame();

            // 3. Crea Controller
            new StanzeAction(frame.getMainPanel().getStanzePanel(), gestoreStanze);
            new ScenariAction(frame.getMainPanel().getScenariPanel(), scenarioManager, parametroManager);

            // 4. Mostra finestra
            frame.setVisible(true);
        });
    }
}

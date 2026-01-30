package it.unipv.posfw.smartdab.ui;

import it.unipv.posfw.smartdab.core.domain.model.casa.Casa;
import it.unipv.posfw.smartdab.core.service.GestoreStanze;
import it.unipv.posfw.smartdab.core.service.ParametroManager;
import it.unipv.posfw.smartdab.core.service.ScenarioManager;
import it.unipv.posfw.smartdab.infrastructure.messaging.EventBus;
import it.unipv.posfw.smartdab.ui.controller.AutenticazioneController;
import it.unipv.posfw.smartdab.ui.controller.ScenariController;
import it.unipv.posfw.smartdab.ui.view.MainFrame;
import it.unipv.posfw.smartdab.ui.view.autenticazione.ConfigurazionePinPanel;
import it.unipv.posfw.smartdab.ui.view.autenticazione.LoginProduttorePanel;

import java.awt.CardLayout;

import javax.swing.*;

public class SmartDABApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
        	
        	//finestra di login
        	JFrame autFrame = new JFrame("Accesso");
            CardLayout cl = new CardLayout();
            JPanel container = new JPanel(cl);

            AutenticazioneController autController = new AutenticazioneController(container, cl);

            //cosa fare dopo che il PIN è stato inserito correttamente
            autController.setOnSuccess(() -> {
                autFrame.dispose(); // chiude la finestrella di login
                avviaApplicazionePrincipale(); // apre il MainFrame con la lista di stanze
            });

            //aggiunge i pannelli assegnando un nome
            container.add(new LoginProduttorePanel(autController), "LOGIN");
            container.add(new ConfigurazionePinPanel(autController), "PIN_SETUP");

            //assemblaggio del frame
            autFrame.add(container);
            autFrame.pack();
            autFrame.setSize(400, 300);
            autFrame.setLocationRelativeTo(null);
            autFrame.setVisible(true);
        });
    }

    private static void avviaApplicazionePrincipale() {  		
            // 1. Inizializza Model/Service
            Casa casa = new Casa();
            GestoreStanze gestoreStanze = new GestoreStanze(casa);
            ScenarioManager scenarioManager = new ScenarioManager();
            ParametroManager parametroManager = new ParametroManager(gestoreStanze, EventBus.getInstance());

            // 2. Crea View (passa GestoreStanze per StanzeController)
            MainFrame frame = new MainFrame(gestoreStanze);

            // 3. Crea Controller per Scenari
            new ScenariController(frame.getMainPanel().getScenariPanel(), scenarioManager, parametroManager, gestoreStanze);

            // 4. Mostra finestra
            frame.setVisible(true);
      }
}

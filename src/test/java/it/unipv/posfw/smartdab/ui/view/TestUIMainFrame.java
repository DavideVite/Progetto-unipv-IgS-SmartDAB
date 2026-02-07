package it.unipv.posfw.smartdab.ui.view;

import it.unipv.posfw.smartdab.ui.controller.StanzeController;
import it.unipv.posfw.smartdab.ui.view.scenari.ScenariPanel;
import it.unipv.posfw.smartdab.ui.view.stanze.StanzePanel;

import javax.swing.*;
import java.awt.*;

/**
 * Test interattivo per MainFrame e MainPanel.
 * Crea manualmente la struttura dell'interfaccia senza servizi reali.
 */
public class TestUIMainFrame {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Crea la finestra principale
            JFrame frame = new JFrame("SmartDAB - Sistema Domotico (Test)");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 600);
            frame.setLocationRelativeTo(null);

            // Crea il contenitore principale con CardLayout per StanzePanel
            CardLayout cardLayout = new CardLayout();
            JPanel stanzeContainer = new JPanel(cardLayout);

            // Crea il controller per StanzePanel (senza servizi per il test)
            StanzeController stanzeController = new StanzeController(stanzeContainer, cardLayout, null);

            // Crea i pannelli
            StanzePanel stanzePanel = new StanzePanel(stanzeController);
            ScenariPanel scenariPanel = new ScenariPanel();

            // Configura il controller con le view
            stanzeController.setViews(stanzePanel, null); // form può essere null per il test base

            // Aggiungi StanzePanel al container
            stanzeContainer.add(stanzePanel, "LISTA_STANZE");

            // Aggiungi dati di esempio alle stanze
            stanzePanel.aggiungiRigaTabella("1", "Soggiorno", 25.5);
            stanzePanel.aggiungiRigaTabella("2", "Camera da letto", 18.0);
            stanzePanel.aggiungiRigaTabella("3", "Cucina", 12.5);
            stanzePanel.aggiungiRigaTabella("4", "Bagno", 8.0);

            // Crea il TabbedPane
            JTabbedPane tabbedPane = new JTabbedPane();
            tabbedPane.addTab("Stanze", stanzeContainer);
            tabbedPane.addTab("Scenari", scenariPanel);

            // Aggiungi alla finestra
            frame.add(tabbedPane, BorderLayout.CENTER);

            // Verifica
            System.out.println("=== Test MainFrame/MainPanel avviato ===");
            System.out.println("Numero di tab: " + tabbedPane.getTabCount());
            System.out.println("Tab 0: " + tabbedPane.getTitleAt(0));
            System.out.println("Tab 1: " + tabbedPane.getTitleAt(1));
            System.out.println("StanzePanel: OK");
            System.out.println("ScenariPanel: OK");
            System.out.println("\nPuoi interagire con l'interfaccia:");
            System.out.println("- Tab 'Stanze': visualizza lista stanze con dati di esempio");
            System.out.println("- Tab 'Scenari': visualizza pannello scenari (vuoto)");
            System.out.println("- Clicca su una stanza per vedere i dettagli");
            System.out.println("- Doppio click su una stanza per modificarla");
            System.out.println("- Clicca 'Aggiungi Nuova Stanza' per testare il bottone");

            // Mostra la finestra
            frame.setVisible(true);
        });
    }
}

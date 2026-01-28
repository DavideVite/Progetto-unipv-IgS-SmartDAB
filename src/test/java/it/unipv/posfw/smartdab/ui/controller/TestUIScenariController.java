package it.unipv.posfw.smartdab.ui.controller;

import it.unipv.posfw.smartdab.core.domain.enums.EnumScenarioType;
import it.unipv.posfw.smartdab.core.domain.model.casa.Casa;
import it.unipv.posfw.smartdab.core.service.GestoreStanze;
import it.unipv.posfw.smartdab.core.service.ParametroManager;
import it.unipv.posfw.smartdab.core.service.ScenarioManager;
import it.unipv.posfw.smartdab.ui.view.scenari.ScenariPanel;

import javax.swing.*;

/**
 * Test interattivo per ScenariController.
 * Crea un'interfaccia con scenari di esempio per testare le funzionalita:
 * - Visualizzazione lista scenari
 * - Selezione e visualizzazione dettaglio
 * - Attivazione/disattivazione tramite checkbox
 * - Creazione nuovo scenario
 * - Eliminazione scenario
 */
public class TestUIScenariController {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Crea la finestra di test
            JFrame frame = new JFrame("Test ScenariController");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 600);

            // Crea il pannello scenari
            ScenariPanel scenariPanel = new ScenariPanel();

            // Crea il manager con alcuni scenari di esempio
            ScenarioManager scenarioManager = creaScenariDiEsempio();

            // Crea un mock semplice per ParametroManager (null-safe per il test UI)
            ParametroManager parametroManager = null; // In un test reale useresti un mock

            // Crea GestoreStanze con una Casa vuota per il test UI
            Casa casa = new Casa();
            GestoreStanze gestoreStanze = new GestoreStanze(casa);

            // Crea il controller con tutti e 4 i parametri richiesti
            ScenariController controller = new ScenariController(scenariPanel, scenarioManager, parametroManager, gestoreStanze);

            // Aggiungi il pannello alla finestra
            frame.add(scenariPanel);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            // Stampa istruzioni
            System.out.println("\n=== Test ScenariController avviato ===");
            System.out.println("Puoi interagire con l'interfaccia:");
            System.out.println("- Clicca su una riga della tabella per vedere il dettaglio a destra");
            System.out.println("- Usa la checkbox 'Attivo' per attivare/disattivare uno scenario");
            System.out.println("- Clicca 'Nuovo' per creare un nuovo scenario");
            System.out.println("- Seleziona uno scenario e clicca 'Elimina' per rimuoverlo");
            System.out.println("\nNOTA: L'attivazione scenario non applica le configurazioni");
            System.out.println("      perche' ParametroManager non e' configurato in questo test.");
        });
    }

    /**
     * Crea alcuni scenari di esempio per il test
     */
    private static ScenarioManager creaScenariDiEsempio() {
        ScenarioManager manager = new ScenarioManager();

        // Scenario 1: Mattina
        manager.creaScenario("Mattina", EnumScenarioType.PREDEFINITO);

        // Scenario 2: Sera
        manager.creaScenario("Sera", EnumScenarioType.PREDEFINITO);

        // Scenario 3: Risparmio Energetico (personalizzato)
        manager.creaScenario("Risparmio Energetico", EnumScenarioType.PERSONALIZZATO);

        // Scenario 4: Vacanza (personalizzato)
        manager.creaScenario("Vacanza", EnumScenarioType.PERSONALIZZATO);

        System.out.println("Creati " + manager.getNumeroScenari() + " scenari di esempio:");
        manager.getTuttiScenari().forEach(s ->
            System.out.println("  - " + s.getNome() + " (" + s.getTipo_scenario() + ")")
        );

        return manager;
    }
}

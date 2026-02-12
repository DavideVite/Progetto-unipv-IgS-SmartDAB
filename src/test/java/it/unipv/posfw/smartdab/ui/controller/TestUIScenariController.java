package it.unipv.posfw.smartdab.ui.controller;

import it.unipv.posfw.smartdab.core.domain.enums.EnumScenarioType;
import it.unipv.posfw.smartdab.core.domain.model.casa.Casa;
import it.unipv.posfw.smartdab.core.domain.model.scenario.Scenario;
import it.unipv.posfw.smartdab.core.port.persistence.IScenarioRepository;
import it.unipv.posfw.smartdab.core.service.GestoreStanze;
import it.unipv.posfw.smartdab.core.service.ScenarioManager;
import it.unipv.posfw.smartdab.ui.view.scenari.ScenariPanel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

            // Crea GestoreStanze con una Casa vuota per il test UI
            Casa casa = new Casa();
            GestoreStanze gestoreStanze = new GestoreStanze(casa);

            // Crea il manager con alcuni scenari di esempio
            ScenarioManager scenarioManager = creaScenariDiEsempio(gestoreStanze);

            // Crea il controller
            ScenariController controller = new ScenariController(scenariPanel, scenarioManager, gestoreStanze);

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
    private static ScenarioManager creaScenariDiEsempio(GestoreStanze gestoreStanze) {
        // Crea un mock repository in-memory per i test
        // ParametroManager con mock per il test (non invia comandi reali)
        ScenarioManager manager = new ScenarioManager(new MockScenarioRepository(), null);

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

    /**
     * Mock in-memory del repository scenari per i test UI
     */
    private static class MockScenarioRepository implements IScenarioRepository {
        private final List<Scenario> scenari = new ArrayList<>();

        @Override
        public void save(Scenario scenario) {
            scenari.add(scenario);
        }

        @Override
        public void update(Scenario scenario) {
            // Mock: non fa nulla
        }

        @Override
        public boolean delete(String id) {
            return scenari.removeIf(s -> s.getId().equals(id));
        }

        @Override
        public Optional<Scenario> findById(String id) {
            return scenari.stream().filter(s -> s.getId().equals(id)).findFirst();
        }

        @Override
        public Optional<Scenario> findByNome(String nome) {
            return scenari.stream().filter(s -> s.getNome().equals(nome)).findFirst();
        }

        @Override
        public List<Scenario> findAll() {
            return new ArrayList<>(scenari);
        }

        @Override
        public List<Scenario> findByTipo(EnumScenarioType tipo) {
            return scenari.stream().filter(s -> s.getTipo_scenario() == tipo).toList();
        }

        @Override
        public List<Scenario> findByActive(boolean active) {
            return scenari.stream().filter(s -> s.isActive() == active).toList();
        }

        @Override
        public boolean existsByNome(String nome) {
            return scenari.stream().anyMatch(s -> s.getNome().equals(nome));
        }

        @Override
        public int count() {
            return scenari.size();
        }
    }
}

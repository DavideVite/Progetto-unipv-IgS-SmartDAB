package it.unipv.posfw.smartdab.ui.view.scenari;

import it.unipv.posfw.smartdab.core.domain.model.scenario.Scenario;
import it.unipv.posfw.smartdab.core.domain.model.scenario.ScenarioStanzaConfig;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ScenarioDetailPanel extends JPanel {

    private JLabel lblNomeScenario;
    private JLabel lblTipo;
    private JLabel lblStato;
    private JTable tabellaConfig;
    private DefaultTableModel configTableModel;

    public ScenarioDetailPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Dettaglio Scenario"));

        // Info scenario
        JPanel infoPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        infoPanel.add(new JLabel("Nome:"));
        lblNomeScenario = new JLabel("-");
        infoPanel.add(lblNomeScenario);

        infoPanel.add(new JLabel("Tipo:"));
        lblTipo = new JLabel("-");
        infoPanel.add(lblTipo);

        infoPanel.add(new JLabel("Stato:"));
        lblStato = new JLabel("-");
        infoPanel.add(lblStato);

        add(infoPanel, BorderLayout.NORTH);

        // Tabella configurazioni
        String[] colonne = {"Stanza", "Parametro", "Valore"};
        configTableModel = new DefaultTableModel(colonne, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabellaConfig = new JTable(configTableModel);
        add(new JScrollPane(tabellaConfig), BorderLayout.CENTER);
    }

    public void mostraScenario(Scenario scenario) {
        if (scenario == null) {
            pulisci();
            return;
        }

        lblNomeScenario.setText(scenario.getNome());
        lblTipo.setText(scenario.getTipo_scenario().toString());
        lblStato.setText(scenario.isActive() ? "Attivo" : "Disattivo");

        configTableModel.setRowCount(0);
        for (ScenarioStanzaConfig config : scenario.getConfigurazioni()) {
            configTableModel.addRow(new Object[]{
                config.getStanzaId(),
                config.getTipo_parametro().toString(),
                config.getParametro().getDisplayString()
            });
        }
    }

    public void pulisci() {
        lblNomeScenario.setText("-");
        lblTipo.setText("-");
        lblStato.setText("-");
        configTableModel.setRowCount(0);
    }
}

package it.unipv.posfw.smartdab.ui.controller;

import it.unipv.posfw.smartdab.core.domain.model.scenario.Scenario;
import it.unipv.posfw.smartdab.core.service.ParametroManager;
import it.unipv.posfw.smartdab.core.service.ScenarioManager;
import it.unipv.posfw.smartdab.ui.view.scenari.ScenariPanel;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.util.ArrayList;
import java.util.List;

public class ScenariAction {

    private ScenariPanel panel;
    private ScenarioManager scenarioManager;
    private ParametroManager parametroManager;
    private List<Scenario> scenariList;

    public ScenariAction(ScenariPanel panel, ScenarioManager scenarioManager, ParametroManager parametroManager) {
        this.panel = panel;
        this.scenarioManager = scenarioManager;
        this.parametroManager = parametroManager;
        this.scenariList = new ArrayList<>();
        addListeners();
        aggiornaTabella();
    }

    private void addListeners() {
        // Selezione riga -> mostra dettaglio
        panel.getTabellaScenari().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = panel.getTabellaScenari().getSelectedRow();
                if (row >= 0 && row < scenariList.size()) {
                    panel.getDetailPanel().mostraScenario(scenariList.get(row));
                } else {
                    panel.getDetailPanel().pulisci();
                }
            }
        });

        // Checkbox attivo/disattivo
        panel.getTableModel().addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == 0) {
                int row = e.getFirstRow();
                if (row >= 0 && row < scenariList.size()) {
                    Boolean attivo = (Boolean) panel.getTableModel().getValueAt(row, 0);
                    Scenario scenario = scenariList.get(row);
                    if (attivo) {
                        scenarioManager.attivaScenario(scenario.getNome(), parametroManager);
                    } else {
                        scenarioManager.disattivaScenario(scenario.getNome());
                    }
                    aggiornaDettaglio(row);
                }
            }
        });

        // Bottone Nuovo
        panel.getBtnNuovo().addActionListener(e -> {
            String nome = JOptionPane.showInputDialog(panel, "Nome nuovo scenario:");
            if (nome != null && !nome.trim().isEmpty()) {
                try {
                    scenarioManager.creaScenario(nome.trim());
                    aggiornaTabella();
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(panel, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Bottone Elimina
        panel.getBtnElimina().addActionListener(e -> {
            int row = panel.getTabellaScenari().getSelectedRow();
            if (row >= 0 && row < scenariList.size()) {
                Scenario scenario = scenariList.get(row);
                int confirm = JOptionPane.showConfirmDialog(panel,
                    "Eliminare lo scenario '" + scenario.getNome() + "'?",
                    "Conferma", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    scenarioManager.eliminaScenario(scenario.getNome());
                    aggiornaTabella();
                    panel.getDetailPanel().pulisci();
                }
            }
        });
    }

    public void aggiornaTabella() {
        scenariList.clear();
        scenariList.addAll(scenarioManager.getTuttiScenari());

        panel.getTableModel().setRowCount(0);
        for (Scenario s : scenariList) {
            panel.getTableModel().addRow(new Object[]{
                s.isActive(),
                s.getNome(),
                s.getTipo_scenario().toString()
            });
        }
    }

    private void aggiornaDettaglio(int row) {
        if (row >= 0 && row < scenariList.size()) {
            panel.getDetailPanel().mostraScenario(scenariList.get(row));
        }
    }
}

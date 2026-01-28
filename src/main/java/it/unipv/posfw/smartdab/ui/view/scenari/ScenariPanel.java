package it.unipv.posfw.smartdab.ui.view.scenari;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ScenariPanel extends JPanel {

    private JTable tabellaScenari;
    private DefaultTableModel tableModel;
    private JButton btnNuovo;
    private JButton btnElimina;
    private ScenarioDetailPanel detailPanel;

    public ScenariPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Titolo
        JLabel titolo = new JLabel("Gestione Scenari");
        titolo.setFont(titolo.getFont().deriveFont(Font.BOLD, 16f));
        add(titolo, BorderLayout.NORTH);

        // Split: lista a sinistra, dettaglio a destra
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.5);

        // Pannello sinistro: tabella + bottoni
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));

        // Tabella scenari con colonne: Attivo (checkbox), Nome, Tipo
        String[] colonne = {"Attivo", "Nome", "Tipo"};
        tableModel = new DefaultTableModel(colonne, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 0) return Boolean.class;
                return String.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0; // Solo checkbox editabile
            }
        };

        tabellaScenari = new JTable(tableModel);
        tabellaScenari.getColumnModel().getColumn(0).setMaxWidth(60);
        tabellaScenari.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        leftPanel.add(new JScrollPane(tabellaScenari), BorderLayout.CENTER);

        // Bottoni
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnNuovo = new JButton("Nuovo");
        btnElimina = new JButton("Elimina");
        btnPanel.add(btnNuovo);
        btnPanel.add(btnElimina);
        leftPanel.add(btnPanel, BorderLayout.SOUTH);

        splitPane.setLeftComponent(leftPanel);

        // Pannello destro: dettaglio scenario
        detailPanel = new ScenarioDetailPanel();
        splitPane.setRightComponent(detailPanel);

        add(splitPane, BorderLayout.CENTER);
    }

    public JTable getTabellaScenari() {
        return tabellaScenari;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JButton getBtnNuovo() {
        return btnNuovo;
    }

    public JButton getBtnElimina() {
        return btnElimina;
    }

    public ScenarioDetailPanel getDetailPanel() {
        return detailPanel;
    }
}

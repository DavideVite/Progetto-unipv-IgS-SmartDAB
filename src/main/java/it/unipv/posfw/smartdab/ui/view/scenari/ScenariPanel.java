package it.unipv.posfw.smartdab.ui.view.scenari;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

public class ScenariPanel extends JPanel {

    private JTable tabellaScenari;
    private DefaultTableModel tableModel;
    private JButton btnNuovo;
    private JButton btnModifica;
    private JButton btnElimina;
    private ScenarioDetailPanel detailPanel;

    public ScenariPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titolo = new JLabel("Gestione Scenari");
        titolo.setFont(titolo.getFont().deriveFont(Font.BOLD, 16f));
        add(titolo, BorderLayout.NORTH);

        // Split: lista a sinistra, dettaglio a destra
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.5);
        
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
        btnModifica = new JButton("Modifica");
        btnElimina = new JButton("Elimina");
        btnPanel.add(btnNuovo);
        btnPanel.add(btnModifica);
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

    public JButton getBtnModifica() {
        return btnModifica;
    }

    public JButton getBtnElimina() {
        return btnElimina;
    }

    public ScenarioDetailPanel getDetailPanel() {
        return detailPanel;
    }
}

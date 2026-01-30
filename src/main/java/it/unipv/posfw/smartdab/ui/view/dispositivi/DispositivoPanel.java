package it.unipv.posfw.smartdab.ui.view.dispositivi;

import it.unipv.posfw.smartdab.core.beans.DispositivoPOJO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class DispositivoPanel extends JPanel {

    private JTabbedPane tabbedPane;

    // Tab vista globale
    private JTable tabellaGlobale;
    private DefaultTableModel modelloGlobale;
    private JComboBox<String> comboFiltroStanza;
    private JButton btnNuovo;
    private JButton btnElimina;

    // Tab vista per stanza
    private JComboBox<String> comboSelezioneStanza;
    private JTable tabellaPerStanza;
    private DefaultTableModel modelloPerStanza;

    // Listener per eventi
    private DispositivoPanelListener listener;

    public DispositivoPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Titolo
        JLabel titolo = new JLabel("Gestione Dispositivi");
        titolo.setFont(titolo.getFont().deriveFont(Font.BOLD, 16f));
        add(titolo, BorderLayout.NORTH);

        // TabbedPane per le due viste
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Vista Globale", creaVistaGlobale());
        tabbedPane.addTab("Per Stanza", creaVistaPerStanza());

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel creaVistaGlobale() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        // Pannello filtro
        JPanel filtroPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filtroPanel.add(new JLabel("Filtra per stanza:"));
        comboFiltroStanza = new JComboBox<>();
        comboFiltroStanza.addItem("Tutte");
        filtroPanel.add(comboFiltroStanza);
        panel.add(filtroPanel, BorderLayout.NORTH);

        // Tabella dispositivi
        String[] colonne = {"ID", "Stanza", "Tipo", "Parametro", "Stato", "Attivo", "Modello"};
        modelloGlobale = new DefaultTableModel(colonne, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 5) return Boolean.class;
                return String.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabellaGlobale = new JTable(modelloGlobale);
        tabellaGlobale.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabellaGlobale.setRowHeight(25);
        panel.add(new JScrollPane(tabellaGlobale), BorderLayout.CENTER);

        // Bottoni
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnNuovo = new JButton("Nuovo Dispositivo");
        btnElimina = new JButton("Elimina");
        btnPanel.add(btnNuovo);
        btnPanel.add(btnElimina);
        panel.add(btnPanel, BorderLayout.SOUTH);

        // Listener filtro
        comboFiltroStanza.addActionListener(e -> {
            if (listener != null) {
                String stanza = (String) comboFiltroStanza.getSelectedItem();
                listener.onFiltroStanzaChanged(stanza);
            }
        });

        // Listener bottoni
        btnNuovo.addActionListener(e -> {
            if (listener != null) {
                listener.onNuovoDispositivo();
            }
        });

        btnElimina.addActionListener(e -> {
            int row = tabellaGlobale.getSelectedRow();
            if (row >= 0 && listener != null) {
                String id = (String) modelloGlobale.getValueAt(row, 0);
                listener.onEliminaDispositivo(id);
            }
        });

        // Doppio click per modifica
        tabellaGlobale.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tabellaGlobale.getSelectedRow();
                    if (row >= 0 && listener != null) {
                        String id = (String) modelloGlobale.getValueAt(row, 0);
                        listener.onModificaDispositivo(id);
                    }
                }
            }
        });

        return panel;
    }

    private JPanel creaVistaPerStanza() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        // Selezione stanza
        JPanel selezionePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selezionePanel.add(new JLabel("Seleziona stanza:"));
        comboSelezioneStanza = new JComboBox<>();
        selezionePanel.add(comboSelezioneStanza);
        panel.add(selezionePanel, BorderLayout.NORTH);

        // Tabella dispositivi stanza
        String[] colonne = {"ID", "Tipo", "Parametro", "Stato", "Attivo", "Modello"};
        modelloPerStanza = new DefaultTableModel(colonne, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                if (column == 4) return Boolean.class;
                return String.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabellaPerStanza = new JTable(modelloPerStanza);
        tabellaPerStanza.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabellaPerStanza.setRowHeight(25);
        panel.add(new JScrollPane(tabellaPerStanza), BorderLayout.CENTER);

        // Listener selezione stanza
        comboSelezioneStanza.addActionListener(e -> {
            if (listener != null) {
                String stanza = (String) comboSelezioneStanza.getSelectedItem();
                listener.onStanzaSelezionata(stanza);
            }
        });

        return panel;
    }

    // Metodi per aggiornare le tabelle
    public void aggiornaListaGlobale(List<DispositivoPOJO> dispositivi) {
        modelloGlobale.setRowCount(0);
        for (DispositivoPOJO d : dispositivi) {
            modelloGlobale.addRow(new Object[]{
                d.getId(),
                d.getStanza(),
                d.getTipo(),
                d.getParametro().toString(),
                d.getStato().toString(),
                d.isAttivo(),
            });
        }
    }

    public void aggiornaListaPerStanza(List<DispositivoPOJO> dispositivi) {
        modelloPerStanza.setRowCount(0);
        for (DispositivoPOJO d : dispositivi) {
            modelloPerStanza.addRow(new Object[]{
                d.getId(),
                d.getTipo(),
                d.getParametro().toString(),
                d.getStato().toString(),
                d.isAttivo(),
            });
        }
    }

    public void aggiornaListaStanze(List<String> stanze) {
        comboFiltroStanza.removeAllItems();
        comboFiltroStanza.addItem("Tutte");
        comboSelezioneStanza.removeAllItems();

        for (String stanza : stanze) {
            comboFiltroStanza.addItem(stanza);
            comboSelezioneStanza.addItem(stanza);
        }
    }

    public void setListener(DispositivoPanelListener listener) {
        this.listener = listener;
    }

    public JTable getTabellaGlobale() {
        return tabellaGlobale;
    }

    public DefaultTableModel getModelloGlobale() {
        return modelloGlobale;
    }

    public JButton getBtnNuovo() {
        return btnNuovo;
    }

    public JButton getBtnElimina() {
        return btnElimina;
    }

    // Interface per gli eventi
    public interface DispositivoPanelListener {
        void onNuovoDispositivo();
        void onModificaDispositivo(String id);
        void onEliminaDispositivo(String id);
        void onFiltroStanzaChanged(String stanza);
        void onStanzaSelezionata(String stanza);
    }
}

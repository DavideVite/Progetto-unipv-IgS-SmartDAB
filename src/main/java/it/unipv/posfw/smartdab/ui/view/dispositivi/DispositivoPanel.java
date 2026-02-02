package it.unipv.posfw.smartdab.ui.view.dispositivi;

import it.unipv.posfw.smartdab.core.beans.DispositivoPOJO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class DispositivoPanel extends JPanel {

    // Vista globale
    private JTable tabellaGlobale;
    private DefaultTableModel modelloGlobale;
    private JComboBox<String> comboFiltroStanza;
    private JButton btnNuovo;
    private JButton btnElimina;

    // Mappa ID stanza -> Nome stanza per visualizzazione
    private Map<String, String> mappaIdToNome = new HashMap<>();

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

        // Vista globale con filtro per stanza
        add(creaVistaGlobale(), BorderLayout.CENTER);
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

    public void aggiornaMappaStanze(Map<String, String> idToNome) {
        this.mappaIdToNome.clear();
        this.mappaIdToNome.putAll(idToNome);
    }

    // Metodi per aggiornare le tabelle
    public void aggiornaListaGlobale(List<DispositivoPOJO> dispositivi) {
        modelloGlobale.setRowCount(0);
        for (DispositivoPOJO d : dispositivi) {
            String stanzaDisplay = mappaIdToNome.getOrDefault(d.getStanza(), d.getStanza());
            modelloGlobale.addRow(new Object[]{
                d.getId(),
                stanzaDisplay,
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

        for (String stanza : stanze) {
            comboFiltroStanza.addItem(stanza);
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
    }
}

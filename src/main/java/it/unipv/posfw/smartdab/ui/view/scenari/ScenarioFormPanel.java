package it.unipv.posfw.smartdab.ui.view.scenari;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.enums.EnumScenarioType;
import it.unipv.posfw.smartdab.core.domain.enums.ParameterType;
import it.unipv.posfw.smartdab.core.domain.model.parametro.IParametroValue;
import it.unipv.posfw.smartdab.core.domain.model.scenario.Scenario;
import it.unipv.posfw.smartdab.core.domain.model.scenario.StanzaConfig;
import it.unipv.posfw.smartdab.factory.ParametroValueFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScenarioFormPanel extends JPanel {

    // Dati scenario
    private JLabel lblTitolo;
    private JTextField txtNome;
    private JComboBox<EnumScenarioType> comboTipo;

    // Tabella configurazioni
    private JTable tabellaConfig;
    private DefaultTableModel modelloConfig;
    private JButton btnAggiungiConfig;
    private JButton btnRimuoviConfig;

    // Pannello aggiunta configurazione
    private JComboBox<String> comboStanza;
    private JComboBox<DispositivoParameter> comboParametro;
    private JPanel panelValore;
    private JTextField txtValoreNumerico;
    private JCheckBox chkValoreBooleano;
    private JComboBox<String> comboValoreEnum;
    private CardLayout valorePanelLayout;

    // Bottoni
    private JButton btnSalva;
    private JButton btnAnnulla;

    private ScenarioFormListener listener;
    private boolean isModifica = false;
    private Scenario scenarioCorrente;

    // Lista temporanea delle configurazioni
    private List<StanzaConfig> configurazioniTemp;

    // Mappa nome stanza -> ID stanza per il corretto salvataggio nel DB
    private Map<String, String> mappaStanzaNomeToId;

    public ScenarioFormPanel() {
        this.configurazioniTemp = new ArrayList<>();
        this.mappaStanzaNomeToId = new HashMap<>();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Pannello superiore: dati scenario
        JPanel panelDatiScenario = creaPanelDatiScenario();
        add(panelDatiScenario, BorderLayout.NORTH);

        // Pannello centrale: configurazioni
        JPanel panelConfigurazioni = creaPanelConfigurazioni();
        add(panelConfigurazioni, BorderLayout.CENTER);

        // Pannello inferiore: bottoni
        JPanel panelBottoni = creaPanelBottoni();
        add(panelBottoni, BorderLayout.SOUTH);
    }

    private JPanel creaPanelDatiScenario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Dati Scenario"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Titolo
        lblTitolo = new JLabel("Nuovo Scenario", SwingConstants.CENTER);
        lblTitolo.setFont(lblTitolo.getFont().deriveFont(Font.BOLD, 14f));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(lblTitolo, gbc);

        // Nome
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        panel.add(new JLabel("Nome:"), gbc);
        txtNome = new JTextField(20);
        gbc.gridx = 1;
        panel.add(txtNome, gbc);

        // Tipo
        gbc.gridy = 2;
        gbc.gridx = 0;
        panel.add(new JLabel("Tipo:"), gbc);
        comboTipo = new JComboBox<>(EnumScenarioType.values());
        gbc.gridx = 1;
        panel.add(comboTipo, gbc);

        return panel;
    }

    private JPanel creaPanelConfigurazioni() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Configurazioni Stanze"));

        // Tabella configurazioni
        String[] colonne = {"Stanza", "Parametro", "Valore"};
        modelloConfig = new DefaultTableModel(colonne, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabellaConfig = new JTable(modelloConfig);
        tabellaConfig.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(tabellaConfig), BorderLayout.CENTER);

        // Pannello aggiunta configurazione
        JPanel panelAggiungi = creaPanelAggiungiConfig();
        panel.add(panelAggiungi, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel creaPanelAggiungiConfig() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Aggiungi Configurazione"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Stanza
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Stanza:"), gbc);
        comboStanza = new JComboBox<>();
        gbc.gridx = 1;
        panel.add(comboStanza, gbc);

        // Parametro
        gbc.gridx = 2;
        panel.add(new JLabel("Parametro:"), gbc);
        comboParametro = new JComboBox<>(DispositivoParameter.values());
        gbc.gridx = 3;
        panel.add(comboParametro, gbc);

        // Pannello valore con CardLayout
        gbc.gridx = 4;
        panel.add(new JLabel("Valore:"), gbc);

        valorePanelLayout = new CardLayout();
        panelValore = new JPanel(valorePanelLayout);

        // Card per valore numerico
        txtValoreNumerico = new JTextField(8);
        panelValore.add(txtValoreNumerico, "NUMERIC");

        // Card per valore booleano
        chkValoreBooleano = new JCheckBox("Attivo");
        panelValore.add(chkValoreBooleano, "BOOLEAN");

        // Card per valore enum
        comboValoreEnum = new JComboBox<>();
        panelValore.add(comboValoreEnum, "ENUM");

        gbc.gridx = 5;
        panel.add(panelValore, gbc);

        // Listener per cambio parametro
        comboParametro.addActionListener(e -> aggiornaPanelValore());

        // Bottoni
        btnAggiungiConfig = new JButton("Aggiungi");
        gbc.gridx = 6;
        panel.add(btnAggiungiConfig, gbc);

        btnRimuoviConfig = new JButton("Rimuovi");
        gbc.gridx = 7;
        panel.add(btnRimuoviConfig, gbc);

        // Listener bottoni
        btnAggiungiConfig.addActionListener(e -> aggiungiConfigurazione());
        btnRimuoviConfig.addActionListener(e -> rimuoviConfigurazione());

        // Inizializza panel valore
        aggiornaPanelValore();

        return panel;
    }

    private void aggiornaPanelValore() {
        DispositivoParameter param = (DispositivoParameter) comboParametro.getSelectedItem();
        if (param == null) return;

        ParameterType tipo = param.getType();
        switch (tipo) {
            case NUMERIC:
                valorePanelLayout.show(panelValore, "NUMERIC");
                txtValoreNumerico.setText("");
                break;
            case BOOLEAN:
                valorePanelLayout.show(panelValore, "BOOLEAN");
                chkValoreBooleano.setSelected(false);
                break;
            case ENUM:
                valorePanelLayout.show(panelValore, "ENUM");
                comboValoreEnum.removeAllItems();
                if (param.getAllowedValues() != null) {
                    for (String val : param.getAllowedValues()) {
                        comboValoreEnum.addItem(val);
                    }
                }
                break;
        }
    }

    private void aggiungiConfigurazione() {
        String stanzaNome = (String) comboStanza.getSelectedItem();
        DispositivoParameter param = (DispositivoParameter) comboParametro.getSelectedItem();

        if (stanzaNome == null || param == null) {
            JOptionPane.showMessageDialog(this, "Seleziona stanza e parametro", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Ottieni l'ID della stanza dalla mappa
        String stanzaId = mappaStanzaNomeToId.get(stanzaNome);
        if (stanzaId == null) {
            JOptionPane.showMessageDialog(this, "Errore: ID stanza non trovato", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Ottieni il valore
        String valoreStr;
        ParameterType tipo = param.getType();
        switch (tipo) {
            case NUMERIC:
                valoreStr = txtValoreNumerico.getText().trim();
                if (valoreStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Inserisci un valore numerico", "Errore", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    double val = Double.parseDouble(valoreStr);
                    if (param.getMin() != null && val < param.getMin()) {
                        JOptionPane.showMessageDialog(this, "Valore sotto il minimo consentito (" + param.getMin() + " " + param.getUnit() + ")", "Errore", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if (param.getMax() != null && val > param.getMax()) {
                        JOptionPane.showMessageDialog(this, "Valore sopra il massimo consentito (" + param.getMax() + " " + param.getUnit() + ")", "Errore", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Valore numerico non valido", "Errore", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                break;
            case BOOLEAN:
                valoreStr = String.valueOf(chkValoreBooleano.isSelected());
                break;
            case ENUM:
                valoreStr = (String) comboValoreEnum.getSelectedItem();
                if (valoreStr == null) {
                    JOptionPane.showMessageDialog(this, "Seleziona un valore", "Errore", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                break;
            default:
                valoreStr = "";
        }

        // Crea la configurazione con l'ID della stanza (per il DB)
        IParametroValue parametroValue = ParametroValueFactory.create(param, valoreStr);
        StanzaConfig config = new StanzaConfig(stanzaId, parametroValue, param);
        configurazioniTemp.add(config);

        // Aggiorna tabella (mostra il nome per l'utente, non l'ID)
        modelloConfig.addRow(new Object[]{
            stanzaNome,
            param.toString(),
            parametroValue.getDisplayString()
        });

        // Reset campi
        txtValoreNumerico.setText("");
        chkValoreBooleano.setSelected(false);
    }

    private void rimuoviConfigurazione() {
        int row = tabellaConfig.getSelectedRow();
        if (row >= 0) {
            configurazioniTemp.remove(row);
            modelloConfig.removeRow(row);
        }
    }

    private JPanel creaPanelBottoni() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnSalva = new JButton("Salva");
        btnAnnulla = new JButton("Annulla");
        panel.add(btnSalva);
        panel.add(btnAnnulla);

        btnSalva.addActionListener(e -> salvaScenario());
        btnAnnulla.addActionListener(e -> {
            if (listener != null) listener.onAnnulla();
        });

        return panel;
    }

    private void salvaScenario() {
        String nome = txtNome.getText().trim();
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Il nome è obbligatorio", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        EnumScenarioType tipo = (EnumScenarioType) comboTipo.getSelectedItem();

        if (listener != null) {
            listener.onSalva(nome, tipo, new ArrayList<>(configurazioniTemp));
        }
    }

    public void preparaPerInserimento() {
        isModifica = false;
        scenarioCorrente = null;
        lblTitolo.setText("Nuovo Scenario");
        txtNome.setText("");
        txtNome.setEditable(true);
        comboTipo.setSelectedItem(EnumScenarioType.PERSONALIZZATO);
        configurazioniTemp.clear();
        modelloConfig.setRowCount(0);
        btnSalva.setText("Salva");
    }

    public void preparaPerModifica(Scenario scenario) {
        isModifica = true;
        scenarioCorrente = scenario;
        lblTitolo.setText("Modifica Scenario");
        txtNome.setText(scenario.getNome());
        txtNome.setEditable(false);
        comboTipo.setSelectedItem(scenario.getTipo_scenario());

        // Carica configurazioni esistenti
        configurazioniTemp.clear();
        modelloConfig.setRowCount(0);

        for (StanzaConfig config : scenario) {
            configurazioniTemp.add(config);
            modelloConfig.addRow(new Object[]{
                config.getStanzaId(),
                config.getTipo_parametro().toString(),
                config.getParametro().getDisplayString()
            });
        }

        btnSalva.setText("Aggiorna");
    }

    /**
     * Aggiorna la lista delle stanze disponibili nel form.
     *
     * @param stanzeMap Mappa nome stanza -> ID stanza
     */
    public void aggiornaListaStanze(Map<String, String> stanzeMap) {
        comboStanza.removeAllItems();
        mappaStanzaNomeToId.clear();
        mappaStanzaNomeToId.putAll(stanzeMap);
        for (String nomeStanza : stanzeMap.keySet()) {
            comboStanza.addItem(nomeStanza);
        }
    }

    public void setListener(ScenarioFormListener listener) {
        this.listener = listener;
    }

    public boolean isModifica() {
        return isModifica;
    }

    public Scenario getScenarioCorrente() {
        return scenarioCorrente;
    }

    // Interface per gli eventi
    public interface ScenarioFormListener {
        void onSalva(String nome, EnumScenarioType tipo, List<StanzaConfig> configurazioni);
        void onAnnulla();
    }
}

package it.unipv.posfw.smartdab.ui.view.dispositivi;

import it.unipv.posfw.smartdab.core.beans.DispositivoPOJO;
import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.enums.DispositivoStates;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DispositivoFormPanel extends JPanel {

    private JLabel lblTitolo;
    private JTextField txtId;
    private JComboBox<String> comboStanza;
    private JComboBox<String> comboTipo;
    private JComboBox<DispositivoParameter> comboParametro;
    private JComboBox<DispositivoStates> comboStato;
    private JCheckBox chkAttivo;
    private JTextField txtModello;

    private JButton btnSalva;
    private JButton btnAnnulla;

    private DispositivoFormListener listener;
    private boolean isModifica = false;

    public DispositivoFormPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Titolo
        lblTitolo = new JLabel("Nuovo Dispositivo", SwingConstants.CENTER);
        lblTitolo.setFont(lblTitolo.getFont().deriveFont(Font.BOLD, 16f));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(lblTitolo, gbc);

        // ID
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        add(new JLabel("ID:"), gbc);
        txtId = new JTextField(15);
        gbc.gridx = 1;
        add(txtId, gbc);

        // Stanza
        gbc.gridy = 2;
        gbc.gridx = 0;
        add(new JLabel("Stanza:"), gbc);
        comboStanza = new JComboBox<>();
        gbc.gridx = 1;
        add(comboStanza, gbc);

        // Tipo (Sensore/Attuatore)
        gbc.gridy = 3;
        gbc.gridx = 0;
        add(new JLabel("Tipo:"), gbc);
        comboTipo = new JComboBox<>(new String[]{"SENSORE", "ATTUATORE"});
        gbc.gridx = 1;
        add(comboTipo, gbc);

        // Parametro
        gbc.gridy = 4;
        gbc.gridx = 0;
        add(new JLabel("Parametro:"), gbc);
        comboParametro = new JComboBox<>(DispositivoParameter.values());
        gbc.gridx = 1;
        add(comboParametro, gbc);

        // Stato
        gbc.gridy = 5;
        gbc.gridx = 0;
        add(new JLabel("Stato:"), gbc);
        comboStato = new JComboBox<>(DispositivoStates.values());
        gbc.gridx = 1;
        add(comboStato, gbc);

        // Attivo
        gbc.gridy = 6;
        gbc.gridx = 0;
        add(new JLabel("Attivo:"), gbc);
        chkAttivo = new JCheckBox();
        chkAttivo.setSelected(true);
        gbc.gridx = 1;
        add(chkAttivo, gbc);

        // Modello
        gbc.gridy = 7;
        gbc.gridx = 0;
        add(new JLabel("Modello:"), gbc);
        txtModello = new JTextField(15);
        gbc.gridx = 1;
        add(txtModello, gbc);

        // Bottoni
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnSalva = new JButton("Salva");
        btnAnnulla = new JButton("Annulla");
        btnPanel.add(btnSalva);
        btnPanel.add(btnAnnulla);

        gbc.gridy = 8;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        add(btnPanel, gbc);

        // Listener bottoni
        btnSalva.addActionListener(e -> {
            if (listener != null) {
                if (validaForm()) {
                    listener.onSalva(creaPOJO());
                }
            }
        });

        btnAnnulla.addActionListener(e -> {
            if (listener != null) {
                listener.onAnnulla();
            }
        });
    }

    private boolean validaForm() {
        if (txtId.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "L'ID è obbligatorio", "Errore", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (comboStanza.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Seleziona una stanza", "Errore", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private DispositivoPOJO creaPOJO() {
        return new DispositivoPOJO(
            txtId.getText().trim(),
            (String) comboStanza.getSelectedItem(),
            (DispositivoParameter) comboParametro.getSelectedItem(),
            (String) comboTipo.getSelectedItem(),
            (DispositivoStates) comboStato.getSelectedItem(),
            chkAttivo.isSelected()
        );
    }

    public void preparaPerInserimento() {
        isModifica = false;
        lblTitolo.setText("Nuovo Dispositivo");
        txtId.setText("");
        txtId.setEditable(true);
        comboTipo.setSelectedIndex(0);
        comboParametro.setSelectedIndex(0);
        comboStato.setSelectedItem(DispositivoStates.ALIVE);
        chkAttivo.setSelected(true);
        txtModello.setText("");
        btnSalva.setText("Salva");
    }

    public void preparaPerModifica(DispositivoPOJO dispositivo) {
        isModifica = true;
        lblTitolo.setText("Modifica Dispositivo");
        txtId.setText(dispositivo.getId());
        txtId.setEditable(false);
        comboStanza.setSelectedItem(dispositivo.getStanza());
        comboTipo.setSelectedItem(dispositivo.getTipo());
        comboParametro.setSelectedItem(dispositivo.getParametro());
        comboStato.setSelectedItem(dispositivo.getStato());
        chkAttivo.setSelected(dispositivo.isAttivo());
        txtModello.setText("");
        btnSalva.setText("Aggiorna");
    }

    public void aggiornaListaStanze(List<String> stanze) {
        comboStanza.removeAllItems();
        for (String stanza : stanze) {
            comboStanza.addItem(stanza);
        }
    }

    public void setListener(DispositivoFormListener listener) {
        this.listener = listener;
    }

    public boolean isModifica() {
        return isModifica;
    }

    // Interface per gli eventi
    public interface DispositivoFormListener {
        void onSalva(DispositivoPOJO dispositivo);
        void onAnnulla();
    }
}

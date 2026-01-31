package it.unipv.posfw.smartdab.ui.view.stanze;

import java.awt.BorderLayout;
import java.util.Map;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.enums.ParameterType;

public class StanzeDettaglioPanel extends JPanel {
    private DefaultTableModel modello;
    private JTable tabella;
    private JLabel titoloLabel;

    public StanzeDettaglioPanel() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder("Dettagli Stanza"));

        titoloLabel = new JLabel("Seleziona una stanza per vedere i parametri", SwingConstants.CENTER);
        add(titoloLabel, BorderLayout.NORTH);

        String[] colonne = {"Parametro", "Valore", "Unità"};
        modello = new DefaultTableModel(colonne, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabella = new JTable(modello);
        tabella.setRowHeight(25);
        tabella.setShowVerticalLines(false);

        JScrollPane scrollPane = new JScrollPane(tabella);
        scrollPane.setPreferredSize(new java.awt.Dimension(0, 150));
        add(scrollPane, BorderLayout.CENTER);
    }

    public void mostraParametri(String nomeStanza, Map<String, Double> parametri) {
        modello.setRowCount(0);
        titoloLabel.setText("Parametri di: " + nomeStanza);

        if (parametri == null || parametri.isEmpty()) {
            titoloLabel.setText("Nessun parametro rilevato per: " + nomeStanza);
            return;
        }

        for (Map.Entry<String, Double> entry : parametri.entrySet()) {
            String nomeParametro = entry.getKey();
            double valore = entry.getValue();

            String valoreFormattato;
            String unita = "";

            try {
                DispositivoParameter param = DispositivoParameter.valueOf(nomeParametro);
                if (param.getType() == ParameterType.BOOLEAN) {
                    valoreFormattato = valore == 1.0 ? param.getTrueLabel() : param.getFalseLabel();
                } else if (param.getType() == ParameterType.ENUM) {
                    int idx = (int) valore;
                    if (param.getAllowedValues() != null && idx >= 0 && idx < param.getAllowedValues().size()) {
                        valoreFormattato = param.getAllowedValues().get(idx);
                    } else {
                        valoreFormattato = String.valueOf(valore);
                    }
                } else {
                    if (valore == Math.floor(valore)) {
                        valoreFormattato = String.valueOf((int) valore);
                    } else {
                        valoreFormattato = String.format("%.2f", valore);
                    }
                    unita = param.getUnit() != null ? param.getUnit() : "";
                }
            } catch (IllegalArgumentException e) {
                valoreFormattato = String.valueOf(valore);
            }

            modello.addRow(new Object[]{nomeParametro, valoreFormattato, unita});
        }
    }

    public void pulisci() {
        modello.setRowCount(0);
        titoloLabel.setText("Seleziona una stanza per vedere i parametri");
    }
}

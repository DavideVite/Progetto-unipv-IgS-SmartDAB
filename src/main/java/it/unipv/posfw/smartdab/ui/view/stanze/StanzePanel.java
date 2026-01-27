package it.unipv.posfw.smartdab.ui.view.stanze;

import javax.swing.*;
import java.awt.*;

public class StanzePanel extends JPanel {

    private JList<String> listaStanze;
    private DefaultListModel<String> listModel;

    public StanzePanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel titolo = new JLabel("Gestione Stanze");
        titolo.setFont(titolo.getFont().deriveFont(Font.BOLD, 16f));
        add(titolo, BorderLayout.NORTH);

        listModel = new DefaultListModel<>();
        listaStanze = new JList<>(listModel);
        add(new JScrollPane(listaStanze), BorderLayout.CENTER);
    }

    public JList<String> getListaStanze() {
        return listaStanze;
    }

    public DefaultListModel<String> getListModel() {
        return listModel;
    }
}

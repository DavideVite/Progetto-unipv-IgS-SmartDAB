package it.unipv.posfw.smartdab.ui.controller;

import it.unipv.posfw.smartdab.core.beans.DispositivoPOJO;
import it.unipv.posfw.smartdab.core.domain.model.casa.Stanza;
import it.unipv.posfw.smartdab.core.service.GestoreStanze;
import it.unipv.posfw.smartdab.ui.view.MainPanel;
import it.unipv.posfw.smartdab.ui.view.dispositivi.DispositivoFormPanel;
import it.unipv.posfw.smartdab.ui.view.dispositivi.DispositivoPanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DispositivoController implements
        DispositivoPanel.DispositivoPanelListener,
        DispositivoFormPanel.DispositivoFormListener {

    private MainPanel mainPanel;
    private DispositivoPanel dispositivoPanel;
    private DispositivoFormPanel formPanel;
    private GestoreStanze gestoreStanze;

    // Dati in memoria (simulazione, in attesa di DAO completo)
    private Map<String, DispositivoPOJO> dispositivi;
    private String filtroStanzaCorrente = "Tutte";

    public DispositivoController(MainPanel mainPanel, GestoreStanze gestoreStanze) {
        this.mainPanel = mainPanel;
        this.gestoreStanze = gestoreStanze;
        this.dispositivi = new HashMap<>();

        inizializzaViste();
        aggiornaVista();
    }

    private void inizializzaViste() {
        // Usa il pannello dispositivi dal MainPanel
        dispositivoPanel = mainPanel.getDispositivoPanel();
        dispositivoPanel.setListener(this);

        formPanel = new DispositivoFormPanel();
        formPanel.setListener(this);
    }

    public void aggiornaVista() {
        aggiornaListaStanze();
        aggiornaListaDispositivi();
    }

    private void aggiornaListaStanze() {
        List<String> nomiStanze = new ArrayList<>();
        Set<Stanza> stanze = gestoreStanze.visualizzaStanze();
        if (stanze != null) {
            for (Stanza s : stanze) {
                nomiStanze.add(s.getNome());
            }
        }
        dispositivoPanel.aggiornaListaStanze(nomiStanze);
        formPanel.aggiornaListaStanze(nomiStanze);
    }

    private void aggiornaListaDispositivi() {
        List<DispositivoPOJO> listaFiltrata = new ArrayList<>();

        for (DispositivoPOJO d : dispositivi.values()) {
            if (filtroStanzaCorrente.equals("Tutte") ||
                d.getStanza().equals(filtroStanzaCorrente)) {
                listaFiltrata.add(d);
            }
        }

        dispositivoPanel.aggiornaListaGlobale(listaFiltrata);
    }

    // Implementazione DispositivoPanelListener
    @Override
    public void onNuovoDispositivo() {
        formPanel.preparaPerInserimento();
        mostraFormDialog();
    }

    @Override
    public void onModificaDispositivo(String id) {
        DispositivoPOJO dispositivo = dispositivi.get(id);
        if (dispositivo != null) {
            formPanel.preparaPerModifica(dispositivo);
            mostraFormDialog();
        }
    }

    @Override
    public void onEliminaDispositivo(String id) {
        int conferma = JOptionPane.showConfirmDialog(
            dispositivoPanel,
            "Sei sicuro di voler eliminare questo dispositivo?",
            "Conferma Eliminazione",
            JOptionPane.YES_NO_OPTION
        );

        if (conferma == JOptionPane.YES_OPTION) {
            dispositivi.remove(id);
            aggiornaListaDispositivi();
            JOptionPane.showMessageDialog(dispositivoPanel, "Dispositivo eliminato");
        }
    }

    @Override
    public void onFiltroStanzaChanged(String stanza) {
        this.filtroStanzaCorrente = stanza;
        aggiornaListaDispositivi();
    }

    @Override
    public void onStanzaSelezionata(String stanza) {
        List<DispositivoPOJO> listaStanza = new ArrayList<>();
        for (DispositivoPOJO d : dispositivi.values()) {
            if (d.getStanza().equals(stanza)) {
                listaStanza.add(d);
            }
        }
        dispositivoPanel.aggiornaListaPerStanza(listaStanza);
    }

    // Implementazione DispositivoFormListener
    @Override
    public void onSalva(DispositivoPOJO dispositivo) {
        if (formPanel.isModifica()) {
            dispositivi.put(dispositivo.getId(), dispositivo);
            JOptionPane.showMessageDialog(formPanel, "Dispositivo aggiornato");
        } else {
            if (dispositivi.containsKey(dispositivo.getId())) {
                JOptionPane.showMessageDialog(formPanel,
                    "ID già esistente", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }
            dispositivi.put(dispositivo.getId(), dispositivo);
            JOptionPane.showMessageDialog(formPanel, "Dispositivo creato");
        }
        chiudiFormDialog();
        aggiornaListaDispositivi();
    }

    @Override
    public void onAnnulla() {
        chiudiFormDialog();
    }

    private JDialog formDialog;

    private void mostraFormDialog() {
        Window window = SwingUtilities.getWindowAncestor(dispositivoPanel);
        if (window instanceof Frame) {
            formDialog = new JDialog((Frame) window, "Dispositivo", true);
        } else {
            formDialog = new JDialog((Frame) null, "Dispositivo", true);
        }
        formDialog.setContentPane(formPanel);
        formDialog.pack();
        formDialog.setLocationRelativeTo(dispositivoPanel);
        formDialog.setVisible(true);
    }

    private void chiudiFormDialog() {
        if (formDialog != null) {
            formDialog.dispose();
            formDialog = null;
        }
    }

    public DispositivoPanel getDispositivoPanel() {
        return dispositivoPanel;
    }

    public DispositivoFormPanel getFormPanel() {
        return formPanel;
    }

    // Metodo per aggiungere dispositivi da test o caricamento
    public void aggiungiDispositivo(DispositivoPOJO dispositivo) {
        dispositivi.put(dispositivo.getId(), dispositivo);
    }
}

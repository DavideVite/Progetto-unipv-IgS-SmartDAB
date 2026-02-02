package it.unipv.posfw.smartdab.ui.controller;

import java.awt.Frame;
import java.awt.Window;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import it.unipv.posfw.smartdab.core.beans.DispositivoPOJO;
import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.model.casa.Stanza;
import it.unipv.posfw.smartdab.core.service.DispositiviManager;
import it.unipv.posfw.smartdab.core.service.DispositivoLoader;
import it.unipv.posfw.smartdab.core.service.GestoreStanze;
import it.unipv.posfw.smartdab.ui.view.MainPanel;
import it.unipv.posfw.smartdab.ui.view.dispositivi.DispositivoFormPanel;
import it.unipv.posfw.smartdab.ui.view.dispositivi.DispositivoPanel;

public class DispositivoController implements
        DispositivoPanel.DispositivoPanelListener,
        DispositivoFormPanel.DispositivoFormListener {

    private MainPanel mainPanel;
    private DispositivoPanel dispositivoPanel;
    private DispositivoFormPanel formPanel;
    private GestoreStanze gestoreStanze;
    private DispositiviManager dispositiviManager;
    private DispositivoLoader dispositivoLoader;

    private Map<String, String> mapNomeToId = new HashMap<>();
    private String filtroStanzaCorrente = "Tutte";

    public DispositivoController(MainPanel mainPanel, GestoreStanze gestoreStanze,
                                 DispositiviManager dispositiviManager, DispositivoLoader dispositivoLoader) {
        this.mainPanel = mainPanel;
        this.gestoreStanze = gestoreStanze;
        this.dispositiviManager = dispositiviManager;
        this.dispositivoLoader = dispositivoLoader;

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
        mapNomeToId.clear();
        Set<Stanza> stanze = gestoreStanze.visualizzaStanze();
        if (stanze != null) {
            for (Stanza s : stanze) {
                nomiStanze.add(s.getNome());
                mapNomeToId.put(s.getNome(), s.getId());
            }
        }
        // Aggiorna la mappa ID -> Nome per visualizzazione nomi nelle tabelle
        Map<String, String> mapIdToNome = new HashMap<>();
        for (Map.Entry<String, String> entry : mapNomeToId.entrySet()) {
            mapIdToNome.put(entry.getValue(), entry.getKey());
        }
        dispositivoPanel.aggiornaMappaStanze(mapIdToNome);

        dispositivoPanel.aggiornaListaStanze(nomiStanze);
        formPanel.aggiornaListaStanze(nomiStanze);
    }

    private void aggiornaListaDispositivi() {
        List<DispositivoPOJO> listaFiltrata = new ArrayList<>();
        String filtroId = mapNomeToId.get(filtroStanzaCorrente);

        for (DispositivoPOJO d : dispositiviManager.getDispositivi()) {
            if (filtroStanzaCorrente == null || filtroStanzaCorrente.equals("Tutte") ||
                d.getStanza().equals(filtroId)) {
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
        DispositivoPOJO dispositivo = dispositiviManager.getDispositivoById(id);
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
            dispositiviManager.disableDispositivo(id);
            aggiornaListaDispositivi();
            JOptionPane.showMessageDialog(dispositivoPanel, "Dispositivo eliminato");
        }
    }

    @Override
    public void onFiltroStanzaChanged(String stanza) {
        this.filtroStanzaCorrente = stanza;
        aggiornaListaDispositivi();
    }

    // Implementazione DispositivoFormListener
    @Override
    public void onSalva(DispositivoPOJO dispositivo) {
        // Il form usa il nome della stanza, ma il DB vuole l'ID
        String stanzaId = mapNomeToId.get(dispositivo.getStanza());
        if (stanzaId != null) {
            dispositivo.setStanza(stanzaId);
        }

        if (formPanel.isModifica()) {
            dispositiviManager.aggiungiDispositivo(dispositivo);
            JOptionPane.showMessageDialog(formPanel, "Dispositivo aggiornato");
        } else {
            if (dispositiviManager.getDispositivoById(dispositivo.getId()) != null) {
                JOptionPane.showMessageDialog(formPanel,
                    "ID già esistente", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }
            dispositiviManager.aggiungiDispositivo(dispositivo);
            JOptionPane.showMessageDialog(formPanel, "Dispositivo creato");
        }

        // Crea l'oggetto dominio e collegalo a Stanza + EventBus
        dispositivoLoader.caricaSingolo(dispositivo);

        // Inizializza il parametro nella stanza se non esiste ancora
        String idStanza = dispositivo.getStanza();
        Stanza stanza = gestoreStanze.cercaStanzaPerId(idStanza);
        if (stanza != null) {
            DispositivoParameter param = dispositivo.getParametro();
            if (stanza.getParametri().get(param.name()) == null) {
                double valoreIniziale = param.getMin() != null ? param.getMin() : 0.0;
                stanza.updateParameter(param.name(), valoreIniziale);
            }
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

}

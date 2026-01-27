package it.unipv.posfw.smartdab.ui.controller;

import it.unipv.posfw.smartdab.core.domain.model.casa.Stanza;
import it.unipv.posfw.smartdab.core.service.GestoreStanze;
import it.unipv.posfw.smartdab.ui.view.stanze.StanzePanel;

public class StanzeAction {

    private StanzePanel panel;
    private GestoreStanze gestoreStanze;

    public StanzeAction(StanzePanel panel, GestoreStanze gestoreStanze) {
        this.panel = panel;
        this.gestoreStanze = gestoreStanze;
        addListeners();
        aggiornaLista();
    }

    private void addListeners() {
        // Per ora solo visualizzazione
    }

    public void aggiornaLista() {
        panel.getListModel().clear();
        for (Stanza s : gestoreStanze.visualizzaStanze()) {
            panel.getListModel().addElement(s.getNome());
        }
    }
}

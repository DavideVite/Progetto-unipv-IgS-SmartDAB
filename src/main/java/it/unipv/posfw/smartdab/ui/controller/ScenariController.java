package it.unipv.posfw.smartdab.ui.controller;

import it.unipv.posfw.smartdab.core.domain.enums.EnumScenarioType;
import it.unipv.posfw.smartdab.core.domain.model.casa.Stanza;
import it.unipv.posfw.smartdab.core.domain.model.scenario.Scenario;
import it.unipv.posfw.smartdab.core.domain.model.scenario.StanzaConfig;
import it.unipv.posfw.smartdab.core.port.communication.observer.Observable;
import it.unipv.posfw.smartdab.core.port.communication.observer.Observer;
import it.unipv.posfw.smartdab.core.service.GestoreStanze;
import it.unipv.posfw.smartdab.core.service.ScenarioManager;
import it.unipv.posfw.smartdab.ui.view.scenari.ScenariPanel;
import it.unipv.posfw.smartdab.ui.view.scenari.ScenarioFormPanel;
import it.unipv.posfw.smartdab.core.domain.exception.ScenarioNonModificabileException;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Controller per la gestione degli Scenari.
 *
 * PATTERN MVC + OBSERVER:
 * - Implementa Observer per ricevere notifiche da ScenarioManager
 * - Quando il Model cambia, il Controller aggiorna automaticamente la View
 * - Registrazione: scenarioManager.addObserver(this) nel costruttore
 *
 * INTEGRAZIONE ScenarioFormPanel:
 * - Aggiunto GestoreStanze per ottenere la lista delle stanze disponibili
 * - Aggiunto ScenarioFormPanel per creazione/modifica scenari completi con configurazioni
 * - Il bottone "Nuovo" ora apre il form completo invece di un semplice input dialog
 * - Implementato ScenarioFormListener per gestire salvataggio e annullamento
 */
public class ScenariController implements ScenarioFormPanel.ScenarioFormListener, Observer {

    private ScenariPanel panel;
    private ScenarioManager scenarioManager;
    private GestoreStanze gestoreStanze;
    private List<Scenario> scenariList;

    // FIX: Aggiunto ScenarioFormPanel per creazione/modifica scenari con configurazioni
    private ScenarioFormPanel formPanel;
    private JDialog formDialog;
    private boolean aggiornamentoInCorso = false;

    /**
     * Costruttore aggiornato con GestoreStanze per ottenere lista stanze.
     *
     * @param panel Il pannello principale degli scenari
     * @param scenarioManager Il manager per operazioni CRUD scenari (contiene ParametroManager)
     * @param gestoreStanze Il gestore stanze per ottenere lista stanze disponibili
     */
    public ScenariController(ScenariPanel panel, ScenarioManager scenarioManager,
                            GestoreStanze gestoreStanze) {
        this.panel = panel;
        this.scenarioManager = scenarioManager;
        this.gestoreStanze = gestoreStanze;
        this.scenariList = new ArrayList<>();

        // Inizializza il form panel
        this.formPanel = new ScenarioFormPanel();
        this.formPanel.setListener(this);

        // OBSERVER PATTERN: registra questo controller come observer del model
        // Quando ScenarioManager notifica cambiamenti, update() viene chiamato automaticamente
        this.scenarioManager.addObserver(this);

        addListeners();
        aggiornaTabella();
    }

    // ==================== Observer Pattern ====================

    /**
     * Callback chiamata automaticamente quando ScenarioManager notifica un cambiamento.
     * Aggiorna la tabella degli scenari nella View.
     *
     * SwingUtilities.invokeLater() garantisce che l'aggiornamento della UI avvenga
     * nel thread EDT (Event Dispatch Thread) di Swing. Anche se attualmente il sistema
     * non usa thread separati, e' buona pratica per evitare problemi futuri se
     * le notifiche dovessero arrivare da thread diversi (es. timer, rete, ecc.).
     */
    @Override
    public void update(Observable o, Object arg) {
        SwingUtilities.invokeLater(() -> aggiornaTabella());
    }

    private void addListeners() {
        // Selezione riga -> mostra dettaglio
        panel.getTabellaScenari().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = panel.getTabellaScenari().getSelectedRow();
                if (row >= 0 && row < scenariList.size()) {
                    panel.getDetailPanel().mostraScenario(scenariList.get(row));
                } else {
                    panel.getDetailPanel().pulisci();
                }
            }
        });

        // Checkbox attivo/disattivo
        panel.getTableModel().addTableModelListener(e -> {
            if (aggiornamentoInCorso) return;
            if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == 0) {
                int row = e.getFirstRow();
                if (row >= 0 && row < scenariList.size()) {
                    Boolean attivo = (Boolean) panel.getTableModel().getValueAt(row, 0);
                    Scenario scenario = scenariList.get(row);
                    if (attivo) {
                        scenarioManager.attivaScenario(scenario.getNome());
                    } else {
                        scenarioManager.disattivaScenario(scenario.getNome());
                    }
                    aggiornaDettaglio(row);
                }
            }
        });

      // Bottone Nuovo - apre ScenarioFormPanel per creare scenari con configurazioni
       panel.getBtnNuovo().addActionListener(e-> mostraFormNuovoScenario());

        // Bottone Modifica - apre ScenarioFormPanel per modificare scenario selezionato
        panel.getBtnModifica().addActionListener(e -> {
            int row = panel.getTabellaScenari().getSelectedRow();
            if (row >= 0 && row < scenariList.size()) {
                Scenario scenario = scenariList.get(row);
                mostraFormModificaScenario(scenario);
            } else {
                JOptionPane.showMessageDialog(panel,
                    "Seleziona uno scenario da modificare",
                    "Attenzione", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Bottone Elimina
        panel.getBtnElimina().addActionListener(e -> {
            int row = panel.getTabellaScenari().getSelectedRow();
            if (row >= 0 && row < scenariList.size()) {
                Scenario scenario = scenariList.get(row);
                int confirm = JOptionPane.showConfirmDialog(panel,
                    "Eliminare lo scenario '" + scenario.getNome() + "'?",
                    "Conferma", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    scenarioManager.eliminaScenario(scenario.getNome());
                    // NOTA: aggiornaTabella() non serve piu' qui, viene chiamato
                    // automaticamente via Observer quando ScenarioManager notifica
                    panel.getDetailPanel().pulisci();
                }
            }
        });
    }

    public void aggiornaTabella() {
        aggiornamentoInCorso = true;
        try {
            scenariList.clear();
            scenariList.addAll(scenarioManager.getTuttiScenari());

            panel.getTableModel().setRowCount(0);
            for (Scenario s : scenariList) {
                panel.getTableModel().addRow(new Object[]{
                    s.isActive(),
                    s.getNome(),
                    s.getTipo_scenario().toString()
                });
            }
        } finally {
            aggiornamentoInCorso = false;
        }
    }

    private void aggiornaDettaglio(int row) {
        if (row >= 0 && row < scenariList.size()) {
            panel.getDetailPanel().mostraScenario(scenariList.get(row));
        }
    }

    // ==================== INTEGRAZIONE ScenarioFormPanel ====================

    /**
     * Mostra il form per creare un nuovo scenario.
     * Aggiorna la lista delle stanze disponibili e prepara il form per l'inserimento.
     */
    private void mostraFormNuovoScenario() {
        // Aggiorna lista stanze nel form
        aggiornaListaStanzeForm();

        // Prepara il form per nuovo inserimento
        formPanel.preparaPerInserimento();

        // Mostra il dialog
        mostraFormDialog("Nuovo Scenario");
    }

    /**
     * Mostra il form per modificare uno scenario esistente.
     *
     * @param scenario Lo scenario da modificare
     */
    private void mostraFormModificaScenario(Scenario scenario) {
        // Aggiorna lista stanze nel form
        aggiornaListaStanzeForm();

        // Prepara il form per modifica
        formPanel.preparaPerModifica(scenario);

        // Mostra il dialog
        mostraFormDialog("Modifica Scenario");
    }

    /**
     * Aggiorna la lista delle stanze disponibili nel form.
     * Passa una mappa nome -> ID per permettere il corretto salvataggio nel DB.
     * Visualizza stanza RESTITUISCE le stanze. 
     */
    private void aggiornaListaStanzeForm() {
        Map<String, String> stanzeMap = new HashMap<>();
        Set<Stanza> stanze = gestoreStanze.visualizzaStanze();
        if (stanze != null) {
            for (Stanza s : stanze) {
                stanzeMap.put(s.getNome(), s.getId());
            }
        }
        formPanel.aggiornaListaStanze(stanzeMap);
    }

    /**
     * Crea e mostra il JDialog contenente il form.
     *
     * @param titolo Il titolo del dialog
     */
    private void mostraFormDialog(String titolo) {
        Window window = SwingUtilities.getWindowAncestor(panel);
        formDialog = new JDialog(window, titolo, Dialog.ModalityType.APPLICATION_MODAL);    
        formDialog.setContentPane(formPanel);
        formDialog.pack();
        formDialog.setMinimumSize(new Dimension(600, 500));
        formDialog.setLocationRelativeTo(panel);
        formDialog.setVisible(true);
    }

    /**
     * Chiude il dialog del form.
     */
    private void chiudiFormDialog() {
        if (formDialog != null) {
            formDialog.dispose();
            formDialog = null;
        }
    }

    // ==================== Implementazione ScenarioFormListener ====================

    /**
     * Callback chiamata quando l'utente salva lo scenario dal form.
     * Crea lo scenario e aggiunge tutte le configurazioni.
     *
     * FIX: Questo metodo integra ScenarioFormPanel permettendo di creare
     * scenari completi con StanzaConfig invece di scenari vuoti.
     *
     * @param nome Il nome dello scenario
     * @param tipo Il tipo (PREDEFINITO/PERSONALIZZATO)
     * @param configurazioni La lista delle configurazioni stanza
     */
    @Override
    public void onSalva(String nome, EnumScenarioType tipo, List<StanzaConfig> configurazioni) {
        try {
            if (formPanel.isModifica()) {
                // Modifica scenario esistente: rimuovi vecchie config e aggiungi nuove
                Scenario scenario = formPanel.getScenarioCorrente();
                // Rimuovi tutte le configurazioni esistenti
                List<StanzaConfig> configEsistenti = new ArrayList<>(scenario.getConfigurazioni());
                for (StanzaConfig config : configEsistenti) {
                    scenarioManager.rimuoviConfigurazione(scenario.getNome(), config);
                }
                // Aggiungi le nuove configurazioni
                for (StanzaConfig config : configurazioni) {
                    scenarioManager.aggiungiConfigurazione(scenario.getNome(), config);
                }
                JOptionPane.showMessageDialog(formPanel, "Scenario aggiornato con successo!");
            } else {
                // Creazione nuovo scenario
                Scenario nuovoScenario = scenarioManager.creaScenario(nome, tipo);

                // Aggiungi tutte le configurazioni
                for (StanzaConfig config : configurazioni) {
                    scenarioManager.aggiungiConfigurazione(nome, config);
                }

                JOptionPane.showMessageDialog(formPanel,
                    "Scenario '" + nome + "' creato con " + configurazioni.size() + " configurazioni!");
            }

            chiudiFormDialog();
            // NOTA: aggiornaTabella() non serve piu' qui, viene chiamato
            // automaticamente via Observer quando ScenarioManager notifica

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(formPanel, ex.getMessage(), "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Callback chiamata quando l'utente annulla l'operazione dal form.
     */
    @Override
    public void onAnnulla() {
        chiudiFormDialog();
    }
}

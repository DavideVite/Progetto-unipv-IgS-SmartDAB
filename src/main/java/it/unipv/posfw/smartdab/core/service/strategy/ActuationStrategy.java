package it.unipv.posfw.smartdab.core.service.strategy;

import java.util.List;

import it.unipv.posfw.smartdab.adapter.facade.AttuatoreFacade;
import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.model.parametro.IParametroValue;

/**
 * Strategy per la distribuzione dei comandi agli attuatori.
 * Permette di variare l'algoritmo di selezione/distribuzione senza modificare ParametroManager.
 *
 * Applicazione del Design Pattern Strategy (GoF) e dei principi:
 * - Open/Closed: nuove strategie senza modificare codice esistente
 * - Protected Variations: isola la logica di distribuzione
 * - Dependency Inversion: ParametroManager dipende dall'astrazione, non dalle implementazioni
 *
 * STRATEGIE IMPLEMENTABILI CON ATTRIBUTI ATTUALI (id, stanza, parametro, tipo, stato, attivo):
 * - DirectMatchStrategy: primo attuatore idoneo
 * - BroadcastStrategy: tutti gli attuatori idonei
 * - FirstActiveStrategy: primo attuatore attivo (placeholder per logiche future)
 *
 * STRATEGIE FUTURE (richiedono nuovi attributi):
 * - LoadBalancingStrategy: richiede attributo "carico" o "potenzaMax" sui dispositivi
 * - EcoEfficiencyStrategy: richiede attributo "consumoNominale" o "energyEfficiency"
 * - PriorityStrategy: richiede attributo "priorita" sui dispositivi
 */
public interface ActuationStrategy {

    /**
     * Seleziona gli attuatori a cui inviare il comando e calcola il valore per ciascuno.
     *
     * @param attuatoriCandidati Lista di attuatori che supportano il parametro richiesto
     * @param tipoParametro Il tipo di parametro da impostare
     * @param valoreRichiesto Il valore target richiesto dall'utente
     * @return Lista di risultati contenenti attuatore e valore da applicare
     */
    List<ActuationResult> distribuisci(
        List<AttuatoreFacade> attuatoriCandidati,
        DispositivoParameter tipoParametro,
        IParametroValue valoreRichiesto
    );

    /**
     * Risultato della distribuzione: associa un attuatore al valore da applicare.
     * Utile per strategie come LoadBalancing dove ogni attuatore riceve un valore diverso.
     */
    public static class ActuationResult {
        private final AttuatoreFacade attuatore;
        private final IParametroValue valore;

        public ActuationResult(AttuatoreFacade attuatore, IParametroValue valore) {
            this.attuatore = attuatore;
            this.valore = valore;
        }

        public AttuatoreFacade getAttuatore() {
            return attuatore;
        }

        public IParametroValue getValore() {
            return valore;
        }
    }
}

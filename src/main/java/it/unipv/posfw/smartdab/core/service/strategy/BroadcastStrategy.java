package it.unipv.posfw.smartdab.core.service.strategy;

import java.util.ArrayList;
import java.util.List;

import it.unipv.posfw.smartdab.adapter.facade.AttuatoreFacade;
import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.model.parametro.IParametroValue;

/**
 * Strategia broadcast: invia lo stesso comando a TUTTI gli attuatori che supportano il parametro.
 * Utile per sincronizzare tutti i dispositivi di un certo tipo.
 *
 * Esempio:
 * - Richiesta: "Imposta TEMPERATURA a 22°C"
 * - Attuatori candidati: [Termostato1, Termostato2, Termostato3]
 * - Risultato: Tutti e 3 ricevono 22°C
 *
 * Caso d'uso tipico: scenario "Casa vuota" -> spegni tutte le luci
 */
public class BroadcastStrategy implements ActuationStrategy {

    @Override
    public List<ActuationResult> distribuisci(
            List<AttuatoreFacade> attuatoriCandidati,
            DispositivoParameter tipoParametro,
            IParametroValue valoreRichiesto) {

        List<ActuationResult> risultati = new ArrayList<>();

        if (attuatoriCandidati == null || attuatoriCandidati.isEmpty()) {
            return risultati;
        }

        // Invia a tutti gli attuatori attivi
        for (AttuatoreFacade attuatore : attuatoriCandidati) {
            if (attuatore.isActive()) {
                risultati.add(new ActuationResult(attuatore, valoreRichiesto));
            }
        }

        return risultati;
    }
}

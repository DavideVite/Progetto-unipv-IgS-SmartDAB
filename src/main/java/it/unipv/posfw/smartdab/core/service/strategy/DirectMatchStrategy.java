package it.unipv.posfw.smartdab.core.service.strategy;

import java.util.ArrayList;
import java.util.List;

import it.unipv.posfw.smartdab.adapter.facade.AttuatoreFacade;
import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.model.parametro.IParametroValue;

/**
 * Strategia di default: seleziona il primo attuatore idoneo e gli invia il valore completo.
 * Corrisponde al comportamento originale di ParametroManager.
 *
 * Esempio:
 * - Richiesta: "Imposta TEMPERATURA a 22°C"
 * - Attuatori candidati: [Termostato1, Termostato2]
 * - Risultato: Termostato1 riceve 22°C
 */
public class DirectMatchStrategy implements ActuationStrategy {

    @Override
    public List<ActuationResult> distribuisci(
            List<AttuatoreFacade> attuatoriCandidati,
            DispositivoParameter tipoParametro,
            IParametroValue valoreRichiesto) {

        List<ActuationResult> risultati = new ArrayList<>();

        if (attuatoriCandidati == null || attuatoriCandidati.isEmpty()) {
            return risultati;
        }

        // Prende il primo attuatore attivo
        for (AttuatoreFacade attuatore : attuatoriCandidati) {
            if (attuatore.isActive()) {
                risultati.add(new ActuationResult(attuatore, valoreRichiesto));
                break; // Solo il primo
            }
        }

        return risultati;
    }
}

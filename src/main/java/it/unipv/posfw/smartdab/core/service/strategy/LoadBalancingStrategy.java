package it.unipv.posfw.smartdab.core.service.strategy;

import java.util.ArrayList;
import java.util.List;

import it.unipv.posfw.smartdab.adapter.facade.AttuatoreFacade;
import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.model.parametro.IParametroValue;
import it.unipv.posfw.smartdab.core.domain.model.parametro.ParametroValue;

/**
 * PLACEHOLDER - Strategia di bilanciamento del carico.
 *
 * ATTENZIONE: Questa strategia ha senso SOLO per parametri di tipo "potenza" o "carico",
 * NON per parametri come TEMPERATURA, UMIDITA, LUMINOSITA dove dividere il valore
 * non ha significato fisico (es. 2 termostati a 11°C non fanno 22°C nella stanza!).
 *
 * CASI D'USO VALIDI:
 * - Potenza totale richiesta da distribuire su piu' attuatori
 * - Carico elettrico da bilanciare
 *
 * CASI D'USO NON VALIDI (comportamento errato):
 * - Temperatura: tutti i termostati devono ricevere lo stesso setpoint
 * - Luminosita: dividere i lumen non ha senso
 * - Umidita: idem
 *
 * REQUISITI PER IMPLEMENTAZIONE COMPLETA:
 * 1. Aggiungere attributo "potenzaMax" o "capacita" in AttuatoreFacade
 * 2. Aggiungere attributo "potenzaCorrente" per sapere il carico attuale
 * 3. Filtrare i parametri per cui ha senso il bilanciamento (es. POTENZA, non TEMPERATURA)
 * 4. Gestire parametri non divisibili (es. ON/OFF)
 *
 * IMPLEMENTAZIONE ATTUALE:
 * - Divide equamente il valore numerico (NON CORRETTO per temperatura!)
 * - Usare solo come esempio/placeholder
 */
public class LoadBalancingStrategy implements ActuationStrategy {

    @Override
    public List<ActuationResult> distribuisci(
            List<AttuatoreFacade> attuatoriCandidati,
            DispositivoParameter tipoParametro,
            IParametroValue valoreRichiesto) {

        List<ActuationResult> risultati = new ArrayList<>();

        if (attuatoriCandidati == null || attuatoriCandidati.isEmpty()) {
            return risultati;
        }

        // Conta attuatori attivi
        List<AttuatoreFacade> attuatoriAttivi = new ArrayList<>();
        for (AttuatoreFacade attuatore : attuatoriCandidati) {
            if (attuatore.isActive()) {
                attuatoriAttivi.add(attuatore);
            }
        }

        if (attuatoriAttivi.isEmpty()) {
            return risultati;
        }

        // Calcola valore diviso
        double valoreOriginale = valoreRichiesto.toNumericValue();
        double valoreDiviso = valoreOriginale / attuatoriAttivi.size();

        // Crea un nuovo ParametroValue per ogni attuatore
        for (AttuatoreFacade attuatore : attuatoriAttivi) {
            IParametroValue valoreRidotto = new ParametroValue(
                String.valueOf(valoreDiviso),
                tipoParametro
            );
            risultati.add(new ActuationResult(attuatore, valoreRidotto));
        }

        return risultati;
    }
}

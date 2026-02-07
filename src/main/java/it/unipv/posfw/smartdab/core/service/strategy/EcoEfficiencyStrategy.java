package it.unipv.posfw.smartdab.core.service.strategy;

import java.util.ArrayList;
import java.util.List;

import it.unipv.posfw.smartdab.adapter.facade.AttuatoreFacade;
import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.model.parametro.IParametroValue;

/**
 * PLACEHOLDER - Strategia eco-efficiente.
 *
 * Idea: tra i dispositivi disponibili, sceglie quello con:
 * - Flag energyEfficiency piu' alto, oppure
 * - Consumo nominale piu' basso
 *
 * REQUISITI PER IMPLEMENTAZIONE COMPLETA:
 * 1. Aggiungere attributo "consumoNominale" (double, in Watt) in AttuatoreFacade
 * 2. Oppure aggiungere attributo "energyEfficiency" (int, da 1 a 5 stelle)
 * 3. Modificare DispositivoPOJO per persistere questi valori
 *
 * IMPLEMENTAZIONE ATTUALE:
 * - Si comporta come DirectMatchStrategy (primo attuatore trovato)
 * - TODO: quando gli attributi saranno disponibili, ordinare per efficienza
 *
 * ESEMPIO DI IMPLEMENTAZIONE FUTURA:
 * <pre>
 * // Ordina per consumo crescente
 * attuatoriAttivi.sort(Comparator.comparingDouble(AttuatoreFacade::getConsumoNominale));
 * // Prendi il primo (piu' efficiente)
 * risultati.add(new ActuationResult(attuatoriAttivi.get(0), valoreRichiesto));
 * </pre>
 */
public class EcoEfficiencyStrategy implements ActuationStrategy {

    @Override
    public List<ActuationResult> distribuisci(
            List<AttuatoreFacade> attuatoriCandidati,
            DispositivoParameter tipoParametro,
            IParametroValue valoreRichiesto) {

        List<ActuationResult> risultati = new ArrayList<>();

        if (attuatoriCandidati == null || attuatoriCandidati.isEmpty()) {
            return risultati;
        }

        // TODO: Quando sara' disponibile l'attributo consumoNominale o energyEfficiency:
        // 1. Filtrare solo attuatori attivi
        // 2. Ordinare per efficienza (consumo crescente o rating decrescente)
        // 3. Selezionare il primo

        // IMPLEMENTAZIONE TEMPORANEA: comportamento DirectMatch
        for (AttuatoreFacade attuatore : attuatoriCandidati) {
            if (attuatore.isActive()) {
                risultati.add(new ActuationResult(attuatore, valoreRichiesto));
                break;
            }
        }

        return risultati;
    }
}

package it.unipv.posfw.smartdab.factory;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.enums.ParameterType;
import it.unipv.posfw.smartdab.core.domain.model.parametro.IParametroValue;
import it.unipv.posfw.smartdab.core.domain.model.parametro.ParametroValue;
import it.unipv.posfw.smartdab.core.domain.model.scenario.StanzaConfig;

/**
 * Factory per la creazione di StanzaConfig.
 * Centralizza la logica di validazione e creazione delle configurazioni
 * che possono essere usate sia per impostazioni manuali che per scenari.
 */
public class StanzaConfigFactory {

	/**
	 * Crea una configurazione con valore numerico (es. temperatura, luminosità).
	 * I vincoli (min, max, unit) sono presi da DispositivoParameter.
	 */
	public static StanzaConfig creaConfigNumerico(
			String stanzaId,
			DispositivoParameter tipoParametro,
			double valore) {

		if (tipoParametro.getType() != ParameterType.NUMERIC) {
			throw new IllegalArgumentException("Il parametro " + tipoParametro + " non è numerico");
		}

		IParametroValue parametroValue = new ParametroValue(String.valueOf(valore), tipoParametro);
		if (!parametroValue.isValid()) {
			throw new IllegalArgumentException("Valore non valido: " + valore +
					" (min: " + tipoParametro.getMin() + ", max: " + tipoParametro.getMax() + ")");
		}
		return new StanzaConfig(stanzaId, parametroValue, tipoParametro);
	}

	/**
	 * Crea una configurazione con valore booleano (es. acceso/spento).
	 * I label (trueLabel, falseLabel) sono presi da DispositivoParameter.
	 */
	public static StanzaConfig creaConfigBooleano(
			String stanzaId,
			DispositivoParameter tipoParametro,
			boolean valore) {

		if (tipoParametro.getType() != ParameterType.BOOLEAN) {
			throw new IllegalArgumentException("Il parametro " + tipoParametro + " non è booleano");
		}

		IParametroValue parametroValue = new ParametroValue(String.valueOf(valore), tipoParametro);
		return new StanzaConfig(stanzaId, parametroValue, tipoParametro);
	}

	/**
	 * Crea una configurazione con valore enum (es. modalità di funzionamento).
	 * I valori ammessi sono presi da DispositivoParameter.
	 */
	public static StanzaConfig creaConfigEnum(
			String stanzaId,
			DispositivoParameter tipoParametro,
			String valoreSelezionato) {

		if (tipoParametro.getType() != ParameterType.ENUM) {
			throw new IllegalArgumentException("Il parametro " + tipoParametro + " non è enum");
		}

		IParametroValue parametroValue = new ParametroValue(valoreSelezionato, tipoParametro);
		if (!parametroValue.isValid()) {
			throw new IllegalArgumentException("Valore non ammesso: " + valoreSelezionato +
					". Valori ammessi: " + tipoParametro.getAllowedValues());
		}
		return new StanzaConfig(stanzaId, parametroValue, tipoParametro);
	}
}

package it.unipv.posfw.smartdab.core.domain.model.parametro;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;

public interface IParametroValue {

	String getDisplayString();
	boolean isValid();
	DispositivoParameter getTipoParametro();

	/**
	 * Restituisce il valore grezzo come stringa.
	 * Permette l'accesso al dato senza dover fare instanceof
	 * sull'implementazione concreta.
	 */
	String getRawValue();

	/**
	 * Converte il valore in formato numerico (double).
	 * Ogni implementazione incapsula la propria logica di conversione,
	 * eliminando la necessita' di instanceof e switch nei chiamanti.
	 * - NUMERIC: restituisce il valore direttamente
	 * - BOOLEAN: restituisce 1.0 (true) o 0.0 (false)
	 * - ENUM: restituisce l'indice del valore nella lista allowedValues
	 */
	double toNumericValue();
}

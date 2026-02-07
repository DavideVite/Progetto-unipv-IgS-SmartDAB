package it.unipv.posfw.smartdab.core.service;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.enums.ParameterType;

/**
 * Validatore centralizzato per i valori dei parametri.
 * Contiene la logica di validazione con messaggi di errore descrittivi,
 * eliminando la duplicazione tra ScenarioFormPanel e StanzeController.
 *
 * Chiamato dal service (ParametroManager) e usabile dalla View per messaggi.
 */
public class ParametroValidator {

	/**
	 * Risultato della validazione con messaggio d'errore opzionale.
	 */
	public static class ValidazioneResult {
		private final boolean valido;
		private final String messaggio;

		private ValidazioneResult(boolean valido, String messaggio) {
			this.valido = valido;
			this.messaggio = messaggio;
		}

		public static ValidazioneResult ok() {
			return new ValidazioneResult(true, null);
		}

		public static ValidazioneResult errore(String messaggio) {
			return new ValidazioneResult(false, messaggio);
		}

		public boolean isValido() { return valido; }
		public String getMessaggio() { return messaggio; }
	}

	/**
	 * Valida un valore stringa per il parametro specificato.
	 * Controlla formato, range min/max e valori ammessi.
	 *
	 * @param param Il tipo di parametro con i suoi vincoli
	 * @param valoreStr Il valore da validare come stringa
	 * @return ValidazioneResult con esito e messaggio d'errore se non valido
	 */
	public static ValidazioneResult valida(DispositivoParameter param, String valoreStr) {
		if (valoreStr == null || valoreStr.trim().isEmpty()) {
			return ValidazioneResult.errore("Il valore non può essere vuoto");
		}

		return switch (param.getType()) {
			case NUMERIC -> validaNumerico(param, valoreStr.trim());
			case BOOLEAN -> validaBooleano(valoreStr.trim());
			case ENUM -> validaEnum(param, valoreStr.trim());
		};
	}

	private static ValidazioneResult validaNumerico(DispositivoParameter param, String valoreStr) {
		double valore;
		try {
			valore = Double.parseDouble(valoreStr);
		} catch (NumberFormatException e) {
			return ValidazioneResult.errore("Valore numerico non valido");
		}

		if (param.getMin() != null && valore < param.getMin()) {
			return ValidazioneResult.errore(
				"Valore sotto il minimo consentito (" + param.getMin() + " " + param.getUnit() + ")");
		}

		if (param.getMax() != null && valore > param.getMax()) {
			return ValidazioneResult.errore(
				"Valore sopra il massimo consentito (" + param.getMax() + " " + param.getUnit() + ")");
		}

		return ValidazioneResult.ok();
	}

	private static ValidazioneResult validaBooleano(String valoreStr) {
		String lower = valoreStr.toLowerCase();
		if (!lower.equals("true") && !lower.equals("false")) {
			return ValidazioneResult.errore("Valore booleano non valido (atteso: true/false)");
		}
		return ValidazioneResult.ok();
	}

	private static ValidazioneResult validaEnum(DispositivoParameter param, String valoreStr) {
		if (param.getAllowedValues() == null || !param.getAllowedValues().contains(valoreStr)) {
			return ValidazioneResult.errore("Valore non ammesso per il parametro " + param.name());
		}
		return ValidazioneResult.ok();
	}
}

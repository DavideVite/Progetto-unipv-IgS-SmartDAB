package it.unipv.posfw.smartdab.core.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.enums.EnumScenarioType;
import it.unipv.posfw.smartdab.core.domain.enums.ParameterType;
import it.unipv.posfw.smartdab.core.domain.model.casa.Stanza;
import it.unipv.posfw.smartdab.factory.StanzaConfigFactory;

/**
 * Inizializzatore degli scenari predefiniti.
 * Legge la configurazione da scenari_predefiniti.properties e crea gli scenari
 * tramite ScenarioManager. Separato da ScenarioManager per rispettare SRP.
 */
public class ScenariPredefinitInitializer {

	private static final String CONFIG_FILE = "scenari_predefiniti.properties";

	private final ScenarioManager scenarioManager;
	private final Properties config;

	public ScenariPredefinitInitializer(ScenarioManager scenarioManager) {
		this.scenarioManager = scenarioManager;
		this.config = caricaConfigurazione();
	}

	private Properties caricaConfigurazione() {
		Properties props = new Properties();
		try (InputStream is = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
			if (is != null) {
				props.load(is);
			} else {
				System.err.println("File di configurazione " + CONFIG_FILE + " non trovato");
			}
		} catch (IOException e) {
			System.err.println("Errore lettura " + CONFIG_FILE + ": " + e.getMessage());
		}
		return props;
	}

	/**
	 * Inizializza gli scenari predefiniti per tutte le stanze fornite.
	 * Per ogni scenario definito nel file .properties, lo crea (se non esiste)
	 * e aggiunge le configurazioni per ogni stanza.
	 *
	 * @param stanze Le stanze a cui applicare gli scenari predefiniti
	 */
	public void inizializza(Set<Stanza> stanze) {
		if (stanze == null || stanze.isEmpty()) {
			System.out.println("Nessuna stanza trovata: scenari predefiniti non creati");
			return;
		}

		// Trova tutti i nomi di scenario nel file properties
		// Le chiavi hanno formato: scenario.<nome>.tipo e scenario.<nome>.parametri
		config.stringPropertyNames().stream()
			.filter(key -> key.startsWith("scenario.") && key.endsWith(".tipo"))
			.forEach(key -> {
				String nomeScenario = key.substring("scenario.".length(), key.length() - ".tipo".length());
				creaScenarioDaConfig(nomeScenario, stanze);
			});
	}

	private void creaScenarioDaConfig(String nomeScenario, Set<Stanza> stanze) {
		if (scenarioManager.esisteScenario(nomeScenario)) return;

		String tipoStr = config.getProperty("scenario." + nomeScenario + ".tipo", "PREDEFINITO");
		EnumScenarioType tipo = EnumScenarioType.valueOf(tipoStr);

		String parametriStr = config.getProperty("scenario." + nomeScenario + ".parametri");
		if (parametriStr == null || parametriStr.isBlank()) {
			System.err.println("Nessun parametro definito per scenario: " + nomeScenario);
			return;
		}

		try {
			scenarioManager.creaScenario(nomeScenario, tipo);

			// Parsa i parametri: "TEMPERATURA:18.0, UMIDITA:45.0, ..."
			String[] coppie = parametriStr.split(",");
			for (Stanza stanza : stanze) {
				for (String coppia : coppie) {
					String[] parti = coppia.trim().split(":");
					if (parti.length != 2) continue;

					DispositivoParameter param = DispositivoParameter.valueOf(parti[0].trim());
					String valore = parti[1].trim();

					if (param.getType() == ParameterType.NUMERIC) {
						scenarioManager.aggiungiConfigurazione(nomeScenario,
							StanzaConfigFactory.creaConfigNumerico(stanza.getId(), param, Double.parseDouble(valore)));
					} else if (param.getType() == ParameterType.BOOLEAN) {
						scenarioManager.aggiungiConfigurazione(nomeScenario,
							StanzaConfigFactory.creaConfigBooleano(stanza.getId(), param, Boolean.parseBoolean(valore)));
					} else if (param.getType() == ParameterType.ENUM) {
						scenarioManager.aggiungiConfigurazione(nomeScenario,
							StanzaConfigFactory.creaConfigEnum(stanza.getId(), param, valore));
					}
				}
			}

			System.out.println("Scenario predefinito '" + nomeScenario + "' creato da configurazione");

		} catch (Exception e) {
			System.err.println("Errore creazione scenario '" + nomeScenario + "': " + e.getMessage());
		}
	}
}

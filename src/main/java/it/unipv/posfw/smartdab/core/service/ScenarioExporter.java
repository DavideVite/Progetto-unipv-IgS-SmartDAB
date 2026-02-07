package it.unipv.posfw.smartdab.core.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;

import it.unipv.posfw.smartdab.core.domain.model.scenario.Scenario;
import it.unipv.posfw.smartdab.core.domain.model.scenario.StanzaConfig;

/**
 * Classe per l'esportazione di scenari su file.
 *
 * Utilizza il pattern Decorator: PrintWriter decora BufferedWriter che decora FileWriter.
 * Questo permette scrittura efficiente (buffered) con formattazione (print/printf).
 *
 * Formato file esportato:
 * - Linee che iniziano con # sono commenti
 * - nome=<nome scenario>
 * - tipo=<PREDEFINITO|PERSONALIZZATO>
 * - attivo=<true|false>
 * - data_creazione=<ISO datetime>
 * - Configurazioni: CONFIG;<stanzaId>;<tipoParametro>;<valore>
 */
public class ScenarioExporter {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Esporta uno scenario su file.
     *
     * @param scenario Lo scenario da esportare
     * @param file Il file di destinazione
     * @throws IOException Se si verifica un errore durante la scrittura
     */
    public void esportaScenario(Scenario scenario, File file) throws IOException {
        // Pattern Decorator: PrintWriter -> BufferedWriter -> FileWriter
        // - FileWriter: scrittura su file di caratteri
        // - BufferedWriter: buffering per efficienza
        // - PrintWriter: metodi print/println/printf per formattazione
        try (PrintWriter writer = new PrintWriter(
                new BufferedWriter(new FileWriter(file)))) {

            scriviIntestazione(writer, scenario);
            scriviMetadati(writer, scenario);
            scriviConfigurazioni(writer, scenario);

            System.out.println("Scenario '" + scenario.getNome() + "' esportato in: " + file.getAbsolutePath());
        }
    }

    /**
     * Scrive l'intestazione del file (commenti).
     */
    private void scriviIntestazione(PrintWriter writer, Scenario scenario) {
        writer.println("# ========================================");
        writer.println("# Scenario SmartDAB Export");
        writer.println("# Nome: " + scenario.getNome());
        writer.println("# ========================================");
        writer.println();
    }

    /**
     * Scrive i metadati dello scenario.
     */
    private void scriviMetadati(PrintWriter writer, Scenario scenario) {
        writer.println("nome=" + scenario.getNome());
        writer.println("tipo=" + scenario.getTipo_scenario().name());
        writer.println("attivo=" + scenario.isActive());

        if (scenario.getData_creazione() != null) {
            writer.println("data_creazione=" + scenario.getData_creazione().format(DATE_FORMATTER));
        }
        if (scenario.getData_ultima_modifica() != null) {
            writer.println("data_ultima_modifica=" + scenario.getData_ultima_modifica().format(DATE_FORMATTER));
        }
        writer.println();
    }

    /**
     * Scrive le configurazioni dello scenario.
     * Formato: CONFIG;<stanzaId>;<tipoParametro>;<valore>
     */
    private void scriviConfigurazioni(PrintWriter writer, Scenario scenario) {
        writer.println("# Configurazioni (formato: CONFIG;stanzaId;tipoParametro;valore)");

        for (StanzaConfig config : scenario.getConfigurazioni()) {
            // Usa printf per formattazione precisa
            writer.printf("CONFIG;%s;%s;%s%n",
                    config.getStanzaId(),
                    config.getTipo_parametro().name(),
                    config.getParametro().getDisplayString().split(" ")[0]); // Rimuove unità di misura
        }
    }

    /**
     * Genera un nome file suggerito per l'export.
     *
     * @param scenario Lo scenario
     * @return Nome file suggerito (es. "scenario_Notte.txt")
     */
    public String suggerisciNomeFile(Scenario scenario) {
        // Rimuove caratteri non validi per nomi file
        String nomeNormalizzato = scenario.getNome()
                .replaceAll("[^a-zA-Z0-9_-]", "_");
        return "scenario_" + nomeNormalizzato + ".txt";
    }
}

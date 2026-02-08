package it.unipv.posfw.smartdab.core.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.StringTokenizer;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.enums.EnumScenarioType;
import it.unipv.posfw.smartdab.core.domain.model.parametro.IParametroValue;
import it.unipv.posfw.smartdab.core.domain.model.parametro.ParametroValue;
import it.unipv.posfw.smartdab.core.domain.model.scenario.Scenario;
import it.unipv.posfw.smartdab.core.domain.model.scenario.StanzaConfig;

/**
 * Classe per l'importazione di scenari da file.
 *
 * Utilizza il pattern Decorator: BufferedReader decora FileReader.
 * Questo permette lettura efficiente linea per linea.
 *
 * Classi I/O utilizzate:
 * - FileReader: lettura stream di caratteri da file
 * - BufferedReader: buffering + metodo readLine()
 * - StringTokenizer: parsing delle configurazioni
 * - String.split(): parsing dei metadati
 *
 * Formato file atteso:
 * - Linee che iniziano con # sono commenti (ignorate)
 * - nome=<nome scenario>
 * - tipo=<PREDEFINITO|PERSONALIZZATO>
 * - attivo=<true|false>
 * - data_creazione=<ISO datetime> (opzionale)
 * - Configurazioni: CONFIG;<stanzaId>;<tipoParametro>;<valore>
 */
public class ScenarioImporter {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    /**
     * Importa uno scenario da file.
     *
     * @param file Il file da cui importare
     * @return Lo scenario importato
     * @throws IOException Se si verifica un errore durante la lettura
     * @throws ScenarioImportException Se il formato del file non è valido
     */
    public Scenario importaScenario(File file) throws IOException, ScenarioImportException {
        // Pattern Decorator: BufferedReader -> FileReader
        // - FileReader: legge caratteri dal file
        // - BufferedReader: aggiunge buffering e readLine()
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String nome = null;
            EnumScenarioType tipo = EnumScenarioType.PERSONALIZZATO;
            boolean attivo = false;
            LocalDateTime dataCreazione = null;
            LocalDateTime dataUltimaModifica = null;

            Scenario scenario = null;
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                // Salta linee vuote e commenti
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                // Parsing metadati con String.split()
                if (line.startsWith("nome=")) {
                    nome = line.split("=", 2)[1].trim();
                }
                else if (line.startsWith("tipo=")) {
                    String tipoStr = line.split("=", 2)[1].trim();
                    try {
                        tipo = EnumScenarioType.valueOf(tipoStr);
                    } catch (IllegalArgumentException e) {
                        throw new ScenarioImportException(
                            "Tipo scenario non valido alla riga " + lineNumber + ": " + tipoStr);
                    }
                }
                else if (line.startsWith("attivo=")) {
                    attivo = Boolean.parseBoolean(line.split("=", 2)[1].trim());
                }
                else if (line.startsWith("data_creazione=")) {
                    String dataStr = line.split("=", 2)[1].trim();
                    dataCreazione = parseData(dataStr, lineNumber);
                }
                else if (line.startsWith("data_ultima_modifica=")) {
                    String dataStr = line.split("=", 2)[1].trim();
                    dataUltimaModifica = parseData(dataStr, lineNumber);
                }
                // Parsing configurazioni con StringTokenizer
                else if (line.startsWith("CONFIG;")) {
                    // Crea lo scenario se non ancora creato (dopo aver letto i metadati)
                    if (scenario == null) {
                        if (nome == null) {
                            throw new ScenarioImportException(
                                "Nome scenario mancante prima delle configurazioni (riga " + lineNumber + ")");
                        }
                        scenario = creaScenario(nome, tipo, attivo, dataCreazione, dataUltimaModifica);
                    }

                    StanzaConfig config = parseConfigurazione(line, lineNumber);
                    scenario.aggiungiConfigurazione(config);
                }
            }

            // Se non ci sono configurazioni ma ci sono metadati, crea comunque lo scenario
            if (scenario == null && nome != null) {
                scenario = creaScenario(nome, tipo, attivo, dataCreazione, dataUltimaModifica);
            }

            if (scenario == null) {
                throw new ScenarioImportException("File non contiene dati validi per uno scenario");
            }

            System.out.println("Scenario '" + scenario.getNome() + "' importato da: " + file.getAbsolutePath());
            return scenario;
        }
    }

    /**
     * Parsa una data dal formato ISO.
     */
    private LocalDateTime parseData(String dataStr, int lineNumber) throws ScenarioImportException {
        try {
            return LocalDateTime.parse(dataStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new ScenarioImportException(
                "Formato data non valido alla riga " + lineNumber + ": " + dataStr);
        }
    }

    /**
     * Crea un nuovo oggetto Scenario con i metadati letti.
     */
    private Scenario creaScenario(String nome, EnumScenarioType tipo, boolean attivo,
                                   LocalDateTime dataCreazione, LocalDateTime dataUltimaModifica) {
        // Usa il costruttore completo se abbiamo le date, altrimenti quello semplice
        Scenario scenario;
        if (dataCreazione != null) {
            scenario = new Scenario(null, nome, tipo, attivo, dataCreazione,
                    dataUltimaModifica != null ? dataUltimaModifica : LocalDateTime.now());
        } else {
            scenario = new Scenario(nome, tipo);
            if (attivo) {
                scenario.attivaScenario();
            }
        }
        return scenario;
    }

    /**
     * Parsa una riga di configurazione usando StringTokenizer.
     * Formato: CONFIG;<stanzaId>;<tipoParametro>;<valore>
     */
    private StanzaConfig parseConfigurazione(String line, int lineNumber) throws ScenarioImportException {
        // StringTokenizer per parsing con delimitatore ";"
        StringTokenizer tokenizer = new StringTokenizer(line, ";");

        // Il primo token è "CONFIG", lo saltiamo
        if (!tokenizer.hasMoreTokens()) {
            throw new ScenarioImportException("Configurazione vuota alla riga " + lineNumber);
        }
        tokenizer.nextToken(); // Salta "CONFIG"

        // Verifica che ci siano tutti i token necessari
        if (tokenizer.countTokens() < 3) {
            throw new ScenarioImportException(
                "Configurazione incompleta alla riga " + lineNumber +
                ". Formato atteso: CONFIG;stanzaId;tipoParametro;valore");
        }

        String stanzaId = tokenizer.nextToken().trim();
        String tipoParametroStr = tokenizer.nextToken().trim();
        String valore = tokenizer.nextToken().trim();

        // Parsing del tipo parametro
        DispositivoParameter tipoParametro;
        try {
            tipoParametro = DispositivoParameter.valueOf(tipoParametroStr);
        } catch (IllegalArgumentException e) {
            throw new ScenarioImportException(
                "Tipo parametro non valido alla riga " + lineNumber + ": " + tipoParametroStr);
        }

        // Creazione del valore parametro
        IParametroValue parametroValue = new ParametroValue(valore, tipoParametro);

        // Validazione del valore
        if (!parametroValue.isValid()) {
            throw new ScenarioImportException(
                "Valore non valido alla riga " + lineNumber +
                ": " + valore + " per parametro " + tipoParametro);
        }

        return new StanzaConfig(stanzaId, parametroValue, tipoParametro);
    }

    /**
     * Verifica se un file sembra essere un file di scenario esportato.
     *
     * @param file Il file da verificare
     * @return true se il file sembra valido
     */
    public boolean verificaFormatoFile(File file) {
        if (!file.exists() || !file.isFile() || !file.canRead()) {
            return false;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                // Il primo contenuto non-commento deve essere "nome="
                return line.startsWith("nome=");
            }
        } catch (IOException e) {
            return false;
        }
        return false;
    }

    /**
     * Eccezione per errori durante l'importazione di scenari.
     */
    public static class ScenarioImportException extends Exception {
        public ScenarioImportException(String message) {
            super(message);
        }

        public ScenarioImportException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}

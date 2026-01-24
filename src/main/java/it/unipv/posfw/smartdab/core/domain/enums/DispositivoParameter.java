package it.unipv.posfw.smartdab.core.domain.enums;

import java.util.List;

public enum DispositivoParameter {

    // === NUMERICI (min, max, unit) ===
    TEMPERATURA(16.0, 30.0, "°C"),
    UMIDITA(30.0, 80.0, "%"),
    PRESSIONE(950.0, 1050.0, "hPa"),
    AQI(0.0, 500.0, ""),
    CO2(400.0, 5000.0, "ppm"),
    VOC(0.0, 1000.0, "ppb"),
    PM2_5(0.0, 500.0, "µg/m³"),
    PM10(0.0, 500.0, "µg/m³"),
    LUMINOSITA(0.0, 100000.0, "lux"),
    RUMORE(0.0, 130.0, "dB"),
    INDICE_UV(0.0, 11.0, ""),
    LIVELLO(0.0, 100.0, "%"),
    VIBRAZIONE(0.0, 100.0, "mm/s"),
    TEMPO(0.0, null, "s"),
    ELETTRICITA(0.0, null, "kWh"),
    TENSIONE(0.0, 400.0, "V"),
    CORRENTE(0.0, 100.0, "A"),
    POTENZA(0.0, null, "W"),
    FOTOVOLTAICO(0.0, null, "kWh"),
    STATO_BATTERIA(0.0, 100.0, "%"),
    CONSUMO_ACQUA(0.0, null, "L"),
    CONSUMO_GAS(0.0, null, "m³"),
    PIOGGIA(0.0, 500.0, "mm"),
    FLUSSO(0.0, null, "L/min"),
    TEMPERATURA_FLUIDO(-50.0, 150.0, "°C"),
    QUALITA_ARIA(0.0, 100.0, "%"),

    // === BOOLEANI (trueLabel, falseLabel) ===
    CONTATTO_PORTA("Aperto", "Chiuso"),
    FUMO("Rilevato", "Assente"),
    GAS("Rilevato", "Assente"),
    MANOMISSIONE("Rilevata", "Assente"),
    SENSORE_PRESENZA("Presente", "Assente"),
    SENSORE_MOVIMENTO("Movimento", "Fermo"),

    // === ENUM (allowedValues) ===
    VENTILAZIONE(List.of("OFF", "LOW", "MEDIUM", "HIGH", "AUTO")),
    LUCE_RGB(List.of("OFF", "WHITE", "WARM", "COOL", "RGB")),

    // === DEFAULT ===
    UNKNOWN();

    // --- Attributi ---
    private final ParameterType type;
    private final Double min;
    private final Double max;
    private final String unit;
    private final String trueLabel;
    private final String falseLabel;
    private final List<String> allowedValues;

    // Costruttore NUMERIC
    DispositivoParameter(Double min, Double max, String unit) {
        this.type = ParameterType.NUMERIC;
        this.min = min;
        this.max = max;
        this.unit = unit;
        this.trueLabel = null;
        this.falseLabel = null;
        this.allowedValues = null;
    }

    // Costruttore BOOLEAN
    DispositivoParameter(String trueLabel, String falseLabel) {
        this.type = ParameterType.BOOLEAN;
        this.min = null;
        this.max = null;
        this.unit = null;
        this.trueLabel = trueLabel;
        this.falseLabel = falseLabel;
        this.allowedValues = null;
    }

    // Costruttore ENUM
    DispositivoParameter(List<String> allowedValues) {
        this.type = ParameterType.ENUM;
        this.min = null;
        this.max = null;
        this.unit = null;
        this.trueLabel = null;
        this.falseLabel = null;
        this.allowedValues = allowedValues;
    }

    // Costruttore UNKNOWN
    DispositivoParameter() {
        this.type = ParameterType.NUMERIC;
        this.min = null;
        this.max = null;
        this.unit = null;
        this.trueLabel = null;
        this.falseLabel = null;
        this.allowedValues = null;
    }

    // --- Getters ---
    public ParameterType getType() { return type; }
    public Double getMin() { return min; }
    public Double getMax() { return max; }
    public String getUnit() { return unit; }
    public String getTrueLabel() { return trueLabel; }
    public String getFalseLabel() { return falseLabel; }
    public List<String> getAllowedValues() { return allowedValues; }
}

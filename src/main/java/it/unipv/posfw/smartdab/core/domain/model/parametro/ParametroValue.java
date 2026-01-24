package it.unipv.posfw.smartdab.core.domain.model.parametro;

import java.util.Objects;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.enums.ParameterType;

/**
 * Implementazione unica di IParametroValue.
 * Contiene il valore grezzo come String e un riferimento a DispositivoParameter
 * che definisce il tipo e i vincoli.
 */
public final class ParametroValue implements IParametroValue {

    private final String rawValue;
    private final DispositivoParameter tipoParametro;

    public ParametroValue(String rawValue, DispositivoParameter tipoParametro) {
        this.rawValue = Objects.requireNonNull(rawValue, "rawValue non può essere null");
        this.tipoParametro = Objects.requireNonNull(tipoParametro, "tipoParametro non può essere null");
    }

    public String getRawValue() {
        return rawValue;
    }

    @Override
    public DispositivoParameter getTipoParametro() {
        return tipoParametro;
    }

    /**
     * Restituisce il valore parsato come Double (solo per NUMERIC).
     * @throws IllegalStateException se il tipo non è NUMERIC
     * @throws NumberFormatException se il valore non è parsabile
     */
    public Double getAsDouble() {
        if (tipoParametro.getType() != ParameterType.NUMERIC) {
            throw new IllegalStateException("Il parametro " + tipoParametro + " non è numerico");
        }
        return Double.parseDouble(rawValue);
    }

    /**
     * Restituisce il valore parsato come Boolean (solo per BOOLEAN).
     * @throws IllegalStateException se il tipo non è BOOLEAN
     */
    public Boolean getAsBoolean() {
        if (tipoParametro.getType() != ParameterType.BOOLEAN) {
            throw new IllegalStateException("Il parametro " + tipoParametro + " non è booleano");
        }
        return Boolean.parseBoolean(rawValue);
    }

    /**
     * Restituisce il valore come String (per ENUM o uso generico).
     */
    public String getAsString() {
        return rawValue;
    }

    @Override
    public boolean isValid() {
        return switch (tipoParametro.getType()) {
            case NUMERIC -> isValidNumeric();
            case BOOLEAN -> isValidBoolean();
            case ENUM -> isValidEnum();
        };
    }

    private boolean isValidNumeric() {
        try {
            double value = Double.parseDouble(rawValue);
            Double min = tipoParametro.getMin();
            Double max = tipoParametro.getMax();

            if (min != null && value < min) return false;
            if (max != null && value > max) return false;
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isValidBoolean() {
        String lower = rawValue.toLowerCase();
        return lower.equals("true") || lower.equals("false");
    }

    private boolean isValidEnum() {
        return tipoParametro.getAllowedValues() != null
            && tipoParametro.getAllowedValues().contains(rawValue);
    }

    @Override
    public String getDisplayString() {
        return switch (tipoParametro.getType()) {
            case NUMERIC -> formatNumeric();
            case BOOLEAN -> formatBoolean();
            case ENUM -> rawValue;
        };
    }

    private String formatNumeric() {
        try {
            double value = Double.parseDouble(rawValue);
            String formatted = String.format("%.1f", value);
            String unit = tipoParametro.getUnit();
            if (unit != null && !unit.isEmpty()) {
                return formatted + " " + unit;
            }
            return formatted;
        } catch (NumberFormatException e) {
            return rawValue;
        }
    }

    private String formatBoolean() {
        boolean value = Boolean.parseBoolean(rawValue);
        return value ? tipoParametro.getTrueLabel() : tipoParametro.getFalseLabel();
    }

    @Override
    public String toString() {
        return "ParametroValue [rawValue=" + rawValue + ", tipoParametro=" + tipoParametro + "]";
    }
}

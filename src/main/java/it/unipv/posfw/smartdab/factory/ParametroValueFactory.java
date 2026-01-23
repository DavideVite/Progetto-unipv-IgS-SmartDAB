package it.unipv.posfw.smartdab.factory;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.enums.ParameterType;
import it.unipv.posfw.smartdab.core.domain.model.parametro.IParametroValue;
import it.unipv.posfw.smartdab.core.domain.model.parametro.ParametroValue;

public class ParametroValueFactory {

    /**
     * Crea un IParametroValue basandosi sui vincoli definiti in DispositivoParameter.
     *
     * @param param il tipo di parametro (con vincoli embedded)
     * @param value il valore come stringa
     * @return ParametroValue con valore grezzo e riferimento al tipo
     */
    public static IParametroValue create(DispositivoParameter param, String value) {
        return new ParametroValue(value, param);
    }

    /**
     * Overload per valori numerici (converte Double -> String).
     */
    public static IParametroValue create(DispositivoParameter param, double value) {
        if (param.getType() != ParameterType.NUMERIC) {
            throw new IllegalArgumentException("Il parametro " + param + " non è numerico");
        }
        return new ParametroValue(String.valueOf(value), param);
    }

    /**
     * Overload per valori booleani (converte boolean -> String).
     */
    public static IParametroValue create(DispositivoParameter param, boolean value) {
        if (param.getType() != ParameterType.BOOLEAN) {
            throw new IllegalArgumentException("Il parametro " + param + " non è booleano");
        }
        return new ParametroValue(String.valueOf(value), param);
    }
}

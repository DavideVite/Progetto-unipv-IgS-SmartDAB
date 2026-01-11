package main.java.it.unipv.posfw.smartdab.factory;

import java.util.List;
import main.java.it.unipv.posfw.smartdab.core.domain.model.parametro.BooleanValue;
import main.java.it.unipv.posfw.smartdab.core.domain.model.parametro.NumericValue;
import main.java.it.unipv.posfw.smartdab.core.domain.model.parametro.EnumValue;

public class ParametroValueFactory {

    public static BooleanValue bool(boolean value, String trueLabel, String falseLabel) {
        return new BooleanValue(value, trueLabel, falseLabel);
    }

    public static NumericValue numericWithRange(double value, double min, double max, String unit) {
        return new NumericValue(value, min, max, unit);
    }

    public static EnumValue enumValue(String selectedValue, List<String> allowedValues) {
        return new EnumValue(selectedValue, allowedValues);
    }
}

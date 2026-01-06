package main.java.it.unipv.posfw.smartdab.src.core.domain.model.parametro;

import java.util.List;

public interface IParametroValue {

	String getDisplayString();
	boolean isValid(); 

	// Factory Methods
	 static BooleanValue bool(boolean value, String trueLabel, String falseLabel) {
	        return new BooleanValue(value, trueLabel, falseLabel);
 }
	 
	 static NumericValue numericWithRange(double value, double min, double max, String unit) {
	        return new NumericValue(value, min, max, unit);
	 }
	 
	 static EnumValue enumValue(String selectedValue, List<String> allowedValues) {
	        return new EnumValue(selectedValue, allowedValues);
	 
	 }
	        
 }

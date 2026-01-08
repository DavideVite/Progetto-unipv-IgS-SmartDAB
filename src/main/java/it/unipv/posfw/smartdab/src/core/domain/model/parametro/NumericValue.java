package main.java.it.unipv.posfw.smartdab.src.core.domain.model.parametro;

/**
 * Value Object per parametri numerici (temperatura, umidità, luminosità, etc.).
 * Supporta validazione di range min-max opzionale.
 */


public final class NumericValue implements IParametroValue{

	private final Double value;
	private final Double minValue;
	private final Double maxValue;
	private final String unit;
	
	
	public NumericValue(Double value, Double minValue, Double maxValue, String unit) {
		this.value = value;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.unit = unit;
	} 
	
	
	
	public Double getValue() {
		return value;
	}


	public Double getMinValue() {
		return minValue;
	}


	public Double getMaxValue() {
		return maxValue;
	}


	public String getUnit() {
		return unit;
	}




	@Override
	public boolean isValid() {
		if (minValue != null && value<minValue) {
			return false;
		}
		
		if (maxValue != null && value>maxValue) {
			return false; 
			
		}
		
		return true; 
		}
	
	
	@Override 
	public String getDisplayString() {
		String formatted = String.format("%.1f", value);
		
	if (unit != null && !unit.isEmpty()) {
		return formatted + " " + unit;
	}
	
	return formatted; 
	
	}



	@Override
	public String toString() {
		return "NumericValue [value=" + value + ", minValue=" + minValue + ", maxValue=" + maxValue + ", unit=" + unit
				+ "]";
	}
	

	
	
    }
	
}

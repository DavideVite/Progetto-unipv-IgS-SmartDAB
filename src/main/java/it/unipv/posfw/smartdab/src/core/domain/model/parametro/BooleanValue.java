import java.util.Objects;

public class BooleanValue implements IParametroValue{

	private final boolean value; 
	private final String trueLabel; 
	private final String falseLabel; 
	
	
    public BooleanValue(boolean value, String trueLabel, String falseLabel) {
        this.value = value;
        this.trueLabel = Objects.requireNonNull(trueLabel, "trueLabel non può essere null");
        this.falseLabel = Objects.requireNonNull(falseLabel, "falseLabel non può essere null");
    }
    
    public boolean getValue() {
        return value;
    }
    
    public String getTrueLabel() {
        return trueLabel;
    }
    
    public String getFalseLabel() {
        return falseLabel;
    }
	
    // Un valore booleano è sempre valido
    @Override
    public boolean isValid() {
        return true;
    }
        
    // uso dell'operatore ternario. if-else compatto         
    @Override
    public String getDisplayString() {
    	return value ? trueLabel : falseLabel; 
    	
    }
	
    @Override
    public String toString() {
        return "BooleanValue{" +
                "value=" + value +
                ", display='" + getDisplayString() + '\'' +
                '}';
    }
}

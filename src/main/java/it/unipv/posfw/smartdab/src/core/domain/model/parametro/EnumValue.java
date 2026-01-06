
import java.util.List;
import java.util.Objects;

public final  class EnumValue implements IParametroValue {

    private final String selectedValue;
    private final List<String> allowedValues;
	
    public EnumValue(String selectedvalue, List<String> allowedValues) {
    	this.selectedValue = Objects.requireNonNull(selectedvalue, "selectedValue non può essere null)");
    	this.allowedValues = List.copyOf(Objects.requireNonNull(allowedValues, "allowedValues non può essere null"));
    			
    	if (allowedValues.isEmpty()) {
    		throw new IllegalArgumentException ("allowedvalues non può essere vuoto");
    	}
    }
      public String getValue() {
            return selectedValue;
        }
        
       public List<String> getAllowedValues() {
            return allowedValues;
        }
 
       
       // Implementazioni metodi interfaccia 
       
       @Override 
       public boolean isValid() {
    	   return allowedValues.contains(selectedValue);
       }
       
       
       @Override
       public String getDisplayString() {
    	   return selectedValue; 
       }
       
       @Override
       public String toString() {
           return "EnumValue{" +
                   "selected='" + selectedValue + '\'' +
                   ", allowed=" + allowedValues +
                   ", valid=" + isValid() +
                   '}';
       }
       
       
       
   }
    
    
    


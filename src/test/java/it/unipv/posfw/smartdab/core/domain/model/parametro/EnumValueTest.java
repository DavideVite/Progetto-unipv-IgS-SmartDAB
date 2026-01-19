package it.unipv.posfw.smartdab.core.domain.model.parametro;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EnumValueTest {

    @Test
    @DisplayName("Costruttore con valori validi crea istanza correttamente")
    void constructor_withValidValues_createsInstance() {
        List<String> allowedValues = Arrays.asList("ROSSO", "VERDE", "BLU");
        EnumValue enumValue = new EnumValue("ROSSO", allowedValues);

        assertEquals("ROSSO", enumValue.getValue());
        assertEquals(allowedValues, enumValue.getAllowedValues());
    }

    @Test
    @DisplayName("Costruttore con selectedValue null lancia NullPointerException")
    void constructor_withNullSelectedValue_throwsNullPointerException() {
        List<String> allowedValues = Arrays.asList("ROSSO", "VERDE", "BLU");

        assertThrows(NullPointerException.class, () -> {
            new EnumValue(null, allowedValues);
        });
    }

    @Test
    @DisplayName("Costruttore con allowedValues null lancia NullPointerException")
    void constructor_withNullAllowedValues_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> {
            new EnumValue("ROSSO", null);
        });
    }

    @Test
    @DisplayName("Costruttore con allowedValues vuoto lancia IllegalArgumentException")
    void constructor_withEmptyAllowedValues_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new EnumValue("ROSSO", Collections.emptyList());
        });
    }

    @Test
    @DisplayName("isValid restituisce true quando selectedValue e' in allowedValues")
    void isValid_whenValueIsAllowed_returnsTrue() {
        List<String> allowedValues = Arrays.asList("ROSSO", "VERDE", "BLU");
        EnumValue enumValue = new EnumValue("VERDE", allowedValues);

        assertTrue(enumValue.isValid());
    }

    @Test
    @DisplayName("isValid restituisce false quando selectedValue non e' in allowedValues")
    void isValid_whenValueIsNotAllowed_returnsFalse() {
        List<String> allowedValues = Arrays.asList("ROSSO", "VERDE", "BLU");
        EnumValue enumValue = new EnumValue("GIALLO", allowedValues);

        assertFalse(enumValue.isValid());
    }

    @Test
    @DisplayName("getDisplayString restituisce il valore selezionato")
    void getDisplayString_returnsSelectedValue() {
        List<String> allowedValues = Arrays.asList("ROSSO", "VERDE", "BLU");
        EnumValue enumValue = new EnumValue("BLU", allowedValues);

        assertEquals("BLU", enumValue.getDisplayString());
    }

    @Test
    @DisplayName("toString restituisce rappresentazione corretta")
    void toString_returnsCorrectRepresentation() {
        List<String> allowedValues = Arrays.asList("ROSSO", "VERDE");
        EnumValue enumValue = new EnumValue("ROSSO", allowedValues);

        String result = enumValue.toString();

        assertTrue(result.contains("ROSSO"));
        assertTrue(result.contains("EnumValue"));
    }

    @Test
    @DisplayName("allowedValues e' immutabile")
    void allowedValues_isImmutable() {
        List<String> originalList = Arrays.asList("ROSSO", "VERDE", "BLU");
        EnumValue enumValue = new EnumValue("ROSSO", originalList);

        List<String> returnedList = enumValue.getAllowedValues();

        assertThrows(UnsupportedOperationException.class, () -> {
            returnedList.add("GIALLO");
        });
    }
}

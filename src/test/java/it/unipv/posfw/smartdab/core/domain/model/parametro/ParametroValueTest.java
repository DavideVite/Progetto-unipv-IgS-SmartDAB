package it.unipv.posfw.smartdab.core.domain.model.parametro;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;

import static org.junit.jupiter.api.Assertions.*;

class ParametroValueTest {

    // ===== TEST COSTRUTTORE =====

    @Test
    @DisplayName("Costruttore con valori validi crea istanza correttamente")
    void constructor_withValidValues_createsInstance() {
        ParametroValue pv = new ParametroValue("22.5", DispositivoParameter.TEMPERATURA);

        assertEquals("22.5", pv.getRawValue());
        assertEquals(DispositivoParameter.TEMPERATURA, pv.getTipoParametro());
    }

    @Test
    @DisplayName("Costruttore con rawValue null lancia NullPointerException")
    void constructor_withNullRawValue_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> {
            new ParametroValue(null, DispositivoParameter.TEMPERATURA);
        });
    }

    @Test
    @DisplayName("Costruttore con tipoParametro null lancia NullPointerException")
    void constructor_withNullTipoParametro_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> {
            new ParametroValue("22.5", null);
        });
    }

    // ===== TEST NUMERIC =====

    @Test
    @DisplayName("isValid restituisce true per valore numerico nel range")
    void isValid_numericInRange_returnsTrue() {
        ParametroValue pv = new ParametroValue("22.5", DispositivoParameter.TEMPERATURA);
        assertTrue(pv.isValid());
    }

    @Test
    @DisplayName("isValid restituisce false per valore numerico sotto il minimo")
    void isValid_numericBelowMin_returnsFalse() {
        ParametroValue pv = new ParametroValue("10.0", DispositivoParameter.TEMPERATURA); // min è 16
        assertFalse(pv.isValid());
    }

    @Test
    @DisplayName("isValid restituisce false per valore numerico sopra il massimo")
    void isValid_numericAboveMax_returnsFalse() {
        ParametroValue pv = new ParametroValue("35.0", DispositivoParameter.TEMPERATURA); // max è 30
        assertFalse(pv.isValid());
    }

    @Test
    @DisplayName("isValid restituisce false per valore non parsabile come numero")
    void isValid_numericNotParsable_returnsFalse() {
        ParametroValue pv = new ParametroValue("abc", DispositivoParameter.TEMPERATURA);
        assertFalse(pv.isValid());
    }

    @Test
    @DisplayName("getAsDouble restituisce il valore corretto")
    void getAsDouble_returnsCorrectValue() {
        ParametroValue pv = new ParametroValue("22.5", DispositivoParameter.TEMPERATURA);
        assertEquals(22.5, pv.getAsDouble());
    }

    @Test
    @DisplayName("getDisplayString per numerico formatta con unità")
    void getDisplayString_numeric_formatsWithUnit() {
        ParametroValue pv = new ParametroValue("22.5", DispositivoParameter.TEMPERATURA);
        assertEquals("22,5 °C", pv.getDisplayString());
    }

    // ===== TEST BOOLEAN =====

    @Test
    @DisplayName("isValid restituisce true per valore booleano valido")
    void isValid_booleanValid_returnsTrue() {
        ParametroValue pvTrue = new ParametroValue("true", DispositivoParameter.CONTATTO_PORTA);
        ParametroValue pvFalse = new ParametroValue("false", DispositivoParameter.CONTATTO_PORTA);

        assertTrue(pvTrue.isValid());
        assertTrue(pvFalse.isValid());
    }

    @Test
    @DisplayName("isValid restituisce false per valore booleano non valido")
    void isValid_booleanInvalid_returnsFalse() {
        ParametroValue pv = new ParametroValue("maybe", DispositivoParameter.CONTATTO_PORTA);
        assertFalse(pv.isValid());
    }

    @Test
    @DisplayName("getAsBoolean restituisce il valore corretto")
    void getAsBoolean_returnsCorrectValue() {
        ParametroValue pvTrue = new ParametroValue("true", DispositivoParameter.CONTATTO_PORTA);
        ParametroValue pvFalse = new ParametroValue("false", DispositivoParameter.CONTATTO_PORTA);

        assertTrue(pvTrue.getAsBoolean());
        assertFalse(pvFalse.getAsBoolean());
    }

    @Test
    @DisplayName("getDisplayString per booleano usa le label")
    void getDisplayString_boolean_usesLabels() {
        ParametroValue pvTrue = new ParametroValue("true", DispositivoParameter.CONTATTO_PORTA);
        ParametroValue pvFalse = new ParametroValue("false", DispositivoParameter.CONTATTO_PORTA);

        assertEquals("Aperto", pvTrue.getDisplayString());
        assertEquals("Chiuso", pvFalse.getDisplayString());
    }

    // ===== TEST ENUM =====

    @Test
    @DisplayName("isValid restituisce true per valore enum ammesso")
    void isValid_enumAllowed_returnsTrue() {
        ParametroValue pv = new ParametroValue("HIGH", DispositivoParameter.VENTILAZIONE);
        assertTrue(pv.isValid());
    }

    @Test
    @DisplayName("isValid restituisce false per valore enum non ammesso")
    void isValid_enumNotAllowed_returnsFalse() {
        ParametroValue pv = new ParametroValue("INVALID", DispositivoParameter.VENTILAZIONE);
        assertFalse(pv.isValid());
    }

    @Test
    @DisplayName("getDisplayString per enum restituisce il valore selezionato")
    void getDisplayString_enum_returnsSelectedValue() {
        ParametroValue pv = new ParametroValue("AUTO", DispositivoParameter.VENTILAZIONE);
        assertEquals("AUTO", pv.getDisplayString());
    }

    // ===== TEST TYPE MISMATCH =====

    @Test
    @DisplayName("getAsDouble su parametro non numerico lancia IllegalStateException")
    void getAsDouble_onNonNumeric_throwsIllegalStateException() {
        ParametroValue pv = new ParametroValue("true", DispositivoParameter.CONTATTO_PORTA);
        assertThrows(IllegalStateException.class, pv::getAsDouble);
    }

    @Test
    @DisplayName("getAsBoolean su parametro non booleano lancia IllegalStateException")
    void getAsBoolean_onNonBoolean_throwsIllegalStateException() {
        ParametroValue pv = new ParametroValue("22.5", DispositivoParameter.TEMPERATURA);
        assertThrows(IllegalStateException.class, pv::getAsBoolean);
    }
}

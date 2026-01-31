package it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import it.unipv.posfw.smartdab.core.beans.MisuraPOJO;

public class InsertMisuraTest {

	MisuraDAO dao = new MisuraDAOImpl();

	@Test
	public void insertMisuraTest() {

		LocalDateTime now = LocalDateTime.now();

		// S00 - Sala (dispositivo D00: FUMO)
		MisuraPOJO m0 = new MisuraPOJO(0, "FUMO", "", 1.0, "S00", now);

		// S01 - Cucina (dispositivo D01: AQI)
		MisuraPOJO m1 = new MisuraPOJO(0, "AQI", "", 75.0, "S01", now);

		// S02 - Studio (dispositivo D02: CONSUMO_ACQUA)
		MisuraPOJO m2 = new MisuraPOJO(0, "CONSUMO_ACQUA", "L", 120.5, "S02", now);

		// S03 - Camera da letto (dispositivo D03: CONTATTO_PORTA)
		MisuraPOJO m3 = new MisuraPOJO(0, "CONTATTO_PORTA", "", 0.0, "S03", now);

		// S04 - Bagno (dispositivo D04: ELETTRICITA)
		MisuraPOJO m4 = new MisuraPOJO(0, "ELETTRICITA", "kWh", 3.7, "S04", now);

		// S05 - Soggiorno (dispositivo D05: TEMPERATURA)
		MisuraPOJO m5 = new MisuraPOJO(0, "TEMPERATURA", "°C", 22.5, "S05", now);

		assertDoesNotThrow(() -> {
			dao.insertMisura(m0);
		}, "Inserimento misura FUMO in Sala fallito");

		assertDoesNotThrow(() -> {
			dao.insertMisura(m1);
		}, "Inserimento misura AQI in Cucina fallito");

		assertDoesNotThrow(() -> {
			dao.insertMisura(m2);
		}, "Inserimento misura CONSUMO_ACQUA in Studio fallito");

		assertDoesNotThrow(() -> {
			dao.insertMisura(m3);
		}, "Inserimento misura CONTATTO_PORTA in Camera da letto fallito");

		assertDoesNotThrow(() -> {
			dao.insertMisura(m4);
		}, "Inserimento misura ELETTRICITA in Bagno fallito");

		assertDoesNotThrow(() -> {
			dao.insertMisura(m5);
		}, "Inserimento misura TEMPERATURA in Soggiorno fallito");
	}
}

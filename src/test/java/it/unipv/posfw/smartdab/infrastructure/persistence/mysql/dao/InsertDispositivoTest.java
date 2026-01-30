package it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

import it.unipv.posfw.smartdab.core.beans.DispositivoPOJO;
import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.enums.DispositivoStates;
import it.unipv.posfw.smartdab.core.domain.model.casa.Stanza;

public class InsertDispositivoTest {
	DispositivoDAO dao = new DispositivoDAOImpl();
	
	@Test
	public void insertStanzaTest() {

		DispositivoPOJO d0 = new DispositivoPOJO ("D00", "S00", DispositivoParameter.FUMO, 
												  "ATTUATORE", DispositivoStates.ALIVE, true);	
		DispositivoPOJO d1 = new DispositivoPOJO ("D01", "S01", DispositivoParameter.AQI, 
				  "ATTUATORE", DispositivoStates.ALIVE, true);	
		DispositivoPOJO d2 = new DispositivoPOJO ("D02", "S02", DispositivoParameter.CONSUMO_ACQUA, 
				  "ATTUATORE", DispositivoStates.CONFLICT, true);	
		DispositivoPOJO d3 = new DispositivoPOJO ("D03", "S03", DispositivoParameter.CONTATTO_PORTA, 
				  "ATTUATORE", DispositivoStates.ALIVE, true);	
		DispositivoPOJO d4 = new DispositivoPOJO ("D04", "S04", DispositivoParameter.ELETTRICITA, 
				  "ATTUATORE", DispositivoStates.DISABLED, true);	
		DispositivoPOJO d5 = new DispositivoPOJO ("D05", "S05", DispositivoParameter.TEMPERATURA, 
				  "ATTUATORE", DispositivoStates.UNKNOWN, true);	

		assertDoesNotThrow(() -> {
			dao.insertDispositivo(d0);
		}, "Inserimento D00 fallito");

		assertDoesNotThrow(() -> {
			dao.insertDispositivo(d1);
		}, "Inserimento D01 fallito");

		assertDoesNotThrow(() -> {
			dao.insertDispositivo(d2);
		}, "Inserimento D02 fallito");

		assertDoesNotThrow(() -> {
			dao.insertDispositivo(d3);
		}, "Inserimento D03 fallito");

		assertDoesNotThrow(() -> {
			dao.insertDispositivo(d4);
		}, "Inserimento D04 fallito");

		assertDoesNotThrow(() -> {
			dao.insertDispositivo(d5);
		}, "Inserimento D05 fallito");
	}
}

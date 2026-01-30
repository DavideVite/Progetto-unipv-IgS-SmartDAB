package it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import it.unipv.posfw.smartdab.core.beans.CommunicationPOJO;
import it.unipv.posfw.smartdab.core.domain.enums.Message;

public class InsertCommunicationTest {

	@Test
	public void insertStanzaTest() {
		CommunicationDAO dao = new CommunicationDAOImpl();


		CommunicationPOJO c0 = new CommunicationPOJO (0, "PENDING", Message.ACK.toString(), 
													  3, "D00", LocalDateTime.now());	

		CommunicationPOJO c1 = new CommunicationPOJO (1, "PENDING", Message.ERROR.toString(), 
				  									  "788", "D00", LocalDateTime.now());
		
		CommunicationPOJO c2 = new CommunicationPOJO (2, "COMPLETED", Message.OFFLINE.toString(), 
				  									  null, "D00", LocalDateTime.now());
		
		CommunicationPOJO c3 = new CommunicationPOJO (3, "REJECTED", Message.ONLINE.toString(), 
				  									  false, "D00", LocalDateTime.now());
		
		CommunicationPOJO c4 = new CommunicationPOJO (4, "REJECTED", Message.STATE.toString(), 
				  									  0, "D00", LocalDateTime.now());
		
		CommunicationPOJO c5 = new CommunicationPOJO (5, "PENDING", Message.ACK.toString(), 
				  									  7654321, "D00", LocalDateTime.now());

		assertDoesNotThrow(() -> {
			dao.insertCommunication(c0);
		}, "Inserimento c0 fallito");

		assertDoesNotThrow(() -> {
			dao.insertCommunication(c1);
		}, "Inserimento c1 fallito");
		
		assertDoesNotThrow(() -> {
			dao.insertCommunication(c2);
		}, "Inserimento c2 fallito");
		
		assertDoesNotThrow(() -> {
			dao.insertCommunication(c3);
		}, "Inserimento c3 fallito");
		
		assertDoesNotThrow(() -> {
			dao.insertCommunication(c4);
		}, "Inserimento c4 fallito");
		
		assertDoesNotThrow(() -> {
			dao.insertCommunication(c5);
		}, "Inserimento c5 fallito");
	}
}

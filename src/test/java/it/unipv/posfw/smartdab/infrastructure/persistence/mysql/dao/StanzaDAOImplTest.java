package it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import it.unipv.posfw.smartdab.core.domain.model.casa.Stanza;

/**
 * Test di integrazione per StanzaDAOImpl.
 * Richiede connessione al database MySQL attiva.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class StanzaDAOImplTest {

    private static StanzaDAO stanzaDAO;
    private static String testId;

    @BeforeAll
    static void setUp() {
        stanzaDAO = new StanzaDAOImpl();
        // ID univoco per evitare conflitti tra esecuzioni
        testId = "TEST_" + UUID.randomUUID().toString().substring(0, 8);
    }

    @Test
    @Order(1)
    @DisplayName("Test inserimento stanza nel database")
    void testInsertStanza() {
        Stanza stanza = new Stanza(testId, "Cucina Test", 25.5);

        assertDoesNotThrow(() -> stanzaDAO.insertStanza(stanza));
        System.out.println("✓ Stanza inserita con ID: " + testId);
    }

    @Test
    @Order(2)
    @DisplayName("Test lettura stanza dal database")
    void testReadStanza() {
        Stanza stanza = stanzaDAO.readStanza(testId);

        assertNotNull(stanza, "La stanza dovrebbe esistere");
        assertEquals(testId, stanza.getId());
        assertEquals("Cucina Test", stanza.getNome());
        assertEquals(25.5, stanza.getMq(), 0.01);

        System.out.println("✓ Stanza letta: " + stanza.getNome() + " (" + stanza.getMq() + " mq)");
    }

    @Test
    @Order(3)
    @DisplayName("Test lettura stanza inesistente")
    void testReadStanzaNotFound() {
        Stanza stanza = stanzaDAO.readStanza("ID_INESISTENTE_12345");

        assertNull(stanza, "La stanza non dovrebbe esistere");
        System.out.println("✓ Stanza inesistente gestita correttamente");
    }

    @Test
    @Order(4)
    @DisplayName("Test aggiornamento stanza nel database")
    void testUpdateStanza() {
        // Leggiamo la stanza esistente
        Stanza stanza = stanzaDAO.readStanza(testId);
        assertNotNull(stanza, "La stanza dovrebbe esistere prima dell'update");

        // Creiamo una stanza con lo stesso ID ma dati modificati
        Stanza stanzaModificata = new Stanza(testId, "Soggiorno Modificato", 40.0);

        assertDoesNotThrow(() -> stanzaDAO.updateStanza(stanzaModificata));

        // Verifichiamo che i dati siano stati aggiornati
        Stanza stanzaAggiornata = stanzaDAO.readStanza(testId);
        assertNotNull(stanzaAggiornata);
        assertEquals("Soggiorno Modificato", stanzaAggiornata.getNome());
        assertEquals(40.0, stanzaAggiornata.getMq(), 0.01);

        System.out.println("✓ Stanza aggiornata: " + stanzaAggiornata.getNome() + " (" + stanzaAggiornata.getMq() + " mq)");
    }

    @Test
    @Order(5)
    @DisplayName("Test lettura tutte le stanze")
    void testReadAllStanze() {
        Set<Stanza> stanze = stanzaDAO.readAllStanze();

        assertNotNull(stanze);
        assertFalse(stanze.isEmpty(), "Dovrebbe esserci almeno una stanza");

        // Verifica che la nostra stanza di test sia presente
        boolean trovata = stanze.stream()
                .anyMatch(s -> testId.equals(s.getId()));
        assertTrue(trovata, "La stanza di test dovrebbe essere nella lista");

        System.out.println("✓ Trovate " + stanze.size() + " stanze nel database");
    }

    @Test
    @Order(6)
    @DisplayName("Test eliminazione stanza dal database")
    void testDeleteStanza() {
        assertDoesNotThrow(() -> stanzaDAO.deleteStanza(testId));

        // Verifica che sia stata eliminata
        Stanza stanza = stanzaDAO.readStanza(testId);
        assertNull(stanza, "La stanza dovrebbe essere stata eliminata");

        System.out.println("✓ Stanza eliminata con successo");
    }
}

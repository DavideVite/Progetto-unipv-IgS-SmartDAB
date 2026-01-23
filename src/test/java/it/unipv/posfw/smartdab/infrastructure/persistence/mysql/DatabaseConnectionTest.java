package it.unipv.posfw.smartdab.infrastructure.persistence.mysql;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per verificare la connessione al database MySQL.
 */
class DatabaseConnectionTest {

    @Test
    @DisplayName("Test connessione al database MySQL")
    void testDatabaseConnection() {
        Connection connection = null;
        try {
            connection = DatabaseConnection.getConnection();

            assertNotNull(connection, "La connessione non dovrebbe essere null");
            assertFalse(connection.isClosed(), "La connessione dovrebbe essere aperta");

            System.out.println("✓ Connessione al database riuscita!");
            System.out.println("  URL: " + connection.getMetaData().getURL());
            System.out.println("  Database: " + connection.getCatalog());
            System.out.println("  User: " + connection.getMetaData().getUserName());

        } catch (SQLException e) {
            fail("Errore nella connessione al database: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Test che la connessione sia valida")
    void testConnectionIsValid() {
        try {
            Connection connection = DatabaseConnection.getConnection();

            // isValid con timeout di 5 secondi
            assertTrue(connection.isValid(5), "La connessione dovrebbe essere valida");
            System.out.println("✓ La connessione è valida!");

        } catch (SQLException e) {
            fail("Errore nel test di validità: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Test query semplice sul database")
    void testSimpleQuery() {
        try {
            Connection connection = DatabaseConnection.getConnection();

            // Esegue una query semplice per testare che il database risponda
            var statement = connection.createStatement();
            var resultSet = statement.executeQuery("SELECT 1");

            assertTrue(resultSet.next(), "La query dovrebbe restituire un risultato");
            assertEquals(1, resultSet.getInt(1), "Il risultato dovrebbe essere 1");

            resultSet.close();
            statement.close();

            System.out.println("✓ Query di test eseguita con successo!");

        } catch (SQLException e) {
            fail("Errore nell'esecuzione della query: " + e.getMessage());
        }
    }
}

package it.unipv.posfw.smartdab.core.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import it.unipv.posfw.smartdab.adapter.facade.AttuatoreFacade;
import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.enums.Message;
import it.unipv.posfw.smartdab.core.domain.model.casa.Casa;
import it.unipv.posfw.smartdab.core.domain.model.casa.Stanza;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.Dispositivo;
import it.unipv.posfw.smartdab.core.domain.model.scenario.StanzaConfig;
import it.unipv.posfw.smartdab.core.port.messaging.IEventBusClient;
import it.unipv.posfw.smartdab.factory.StanzaConfigFactory;
import it.unipv.posfw.smartdab.infrastructure.messaging.request.Request;
import it.unipv.posfw.smartdab.infrastructure.messaging.topic.Topic;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.lampadaON_OFF.Lampada_ON_OFF;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.lampadaON_OFF.Lampada_Communicator;
// FIX: Aggiunto import ObservableParameter necessario per costruttore Lampada_ON_OFF
import it.unipv.posfw.smartdab.core.domain.model.parametro.ObservableParameter;
// FIX: Aggiunto import StanzaDAO per MockStanzaDAO
import it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao.StanzaDAO;
import java.util.Set;

/**
 * Test del flusso completo: Impostazione manuale di un parametro
 *
 * Flusso testato:
 * 1. GUI -> StanzaConfigFactory crea StanzaConfig
 * 2. StanzaConfig -> ParametroManager cerca dispositivo idoneo
 * 3. ParametroManager -> EventBus invia comando
 */
public class ImpostazioneManualeFlussoTest {

    private Casa casa;
    private Stanza stanzaSoggiorno;
    private GestoreStanze gestoreStanze;
    private ParametroManager parametroManager;
    private MockEventBusClient mockEventBus;
    private Lampada_ON_OFF lampada;

    @BeforeEach
    void setUp() {
        // Setup casa e stanza
        casa = new Casa();
        stanzaSoggiorno = new Stanza("S01", "Soggiorno", 25.0);
        casa.nuovaStanza(stanzaSoggiorno);

        /*
         * FIX: Corretto ordine di creazione oggetti per Lampada_ON_OFF.
         *
         * La signature corretta del costruttore e':
         * Lampada_ON_OFF(Topic topic, Lampada_Communicator c, ObservableParameter o, int intensita)
         *
         * Quindi dobbiamo:
         * 1. Creare prima il Topic (richiede RoomPort, cioe' Stanza che ora implementa RoomPort)
         * 2. Creare ObservableParameter per il parametro LUMINOSITA
         * 3. Creare la Lampada con tutti i parametri corretti
         */

        // 1. Crea il Topic PRIMA della lampada (richiede id, RoomPort, DispositivoParameter)
        // FIX: Stanza ora implementa RoomPort, quindi puo' essere passata a createTopic
        Topic topic = Topic.createTopic("Lamp01", stanzaSoggiorno, DispositivoParameter.LUMINOSITA);

        // 2. Crea ObservableParameter per la luminosita'
        ObservableParameter obsParam = new ObservableParameter(DispositivoParameter.LUMINOSITA);

        // 3. Crea il communicator
        Lampada_Communicator communicator = new Lampada_Communicator();

        // 4. Crea la lampada con la signature corretta: (Topic, Communicator, ObservableParameter, int)
        lampada = new Lampada_ON_OFF(topic, communicator, obsParam, 3000);

        // 5. Attiva la lampada
        lampada.switchDispositivo();

        // 6. Aggiungi lampada alla stanza
        stanzaSoggiorno.addDispositivo(lampada);

        // Setup managers con mock DAO (non serve connessione DB per i test)
        gestoreStanze = new GestoreStanze(casa, new MockStanzaDAO());
        mockEventBus = new MockEventBusClient();
        parametroManager = new ParametroManager(gestoreStanze, mockEventBus);
    }

    // ========== TEST STANZACONFIGFACTORY ==========

    @Test
    @DisplayName("Factory crea StanzaConfig numerico con valori validi")
    void testFactoryCreaConfigNumericoValido() {
        StanzaConfig config = StanzaConfigFactory.creaConfigNumerico(
            "Soggiorno",
            DispositivoParameter.TEMPERATURA,
            22.5
        );

        assertNotNull(config);
        assertEquals("Soggiorno", config.getStanzaId());
        assertEquals(DispositivoParameter.TEMPERATURA, config.getTipo_parametro());
        assertNotNull(config.getParametro());
        assertTrue(config.getParametro().isValid());
    }

    @Test
    @DisplayName("Factory lancia eccezione per valore fuori range")
    void testFactoryRifiutaValoreFuoriRange() {
        assertThrows(IllegalArgumentException.class, () -> {
            StanzaConfigFactory.creaConfigNumerico(
                "Soggiorno",
                DispositivoParameter.TEMPERATURA,
                50.0  // Fuori range (max 30)
            );
        });
    }

    @Test
    @DisplayName("Factory crea StanzaConfig booleano")
    void testFactoryCreaConfigBooleano() {
        StanzaConfig config = StanzaConfigFactory.creaConfigBooleano(
            "Soggiorno",
            DispositivoParameter.CONTATTO_PORTA,
            true
        );

        assertNotNull(config);
        assertEquals(DispositivoParameter.CONTATTO_PORTA, config.getTipo_parametro());
    }

    @Test
    @DisplayName("Factory lancia eccezione se tipo parametro non corrisponde")
    void testFactoryRifiutaTipoErrato() {
        // Prova a creare un config numerico con un parametro booleano
        assertThrows(IllegalArgumentException.class, () -> {
            StanzaConfigFactory.creaConfigNumerico(
                "Soggiorno",
                DispositivoParameter.CONTATTO_PORTA,  // È booleano, non numerico
                50.0
            );
        });
    }

    // ========== TEST RICERCA DISPOSITIVO IDONEO ==========

    @Test
    @DisplayName("ParametroManager trova attuatore idoneo per LUMINOSITA")
    void testTrovaAttuatoreIdoneo() {
        Dispositivo trovato = parametroManager.getDispositivoIdoneo(
            "Soggiorno",
            DispositivoParameter.LUMINOSITA
        );

        assertNotNull(trovato, "Dovrebbe trovare la lampada");
        assertTrue(trovato instanceof AttuatoreFacade, "Dovrebbe essere un AttuatoreFacade");
        // FIX: Dispositivo non ha getId(), l'ID e' nel Topic
        assertEquals("Lamp01", trovato.getTopic().getId());
    }

    @Test
    @DisplayName("ParametroManager restituisce null se parametro non supportato")
    void testNonTrovaAttuatorePerParametroNonSupportato() {
        Dispositivo trovato = parametroManager.getDispositivoIdoneo(
            "Soggiorno",
            DispositivoParameter.TEMPERATURA  // La lampada non supporta temperatura
        );

        assertNull(trovato, "Non dovrebbe trovare dispositivi per TEMPERATURA");
    }

    @Test
    @DisplayName("ParametroManager restituisce null per stanza inesistente")
    void testStanzaInesistente() {
        Dispositivo trovato = parametroManager.getDispositivoIdoneo(
            "StanzaFantasma",
            DispositivoParameter.LUMINOSITA
        );

        assertNull(trovato);
    }

    @Test
    @DisplayName("ParametroManager ignora dispositivi non attivi")
    void testIgnoraDispositivoNonAttivo() {
        lampada.switchDispositivo(); // Disattiva la lampada

        Dispositivo trovato = parametroManager.getDispositivoIdoneo(
            "Soggiorno",
            DispositivoParameter.LUMINOSITA
        );

        assertNull(trovato, "Non dovrebbe trovare dispositivi non attivi");
    }

    // ========== TEST FLUSSO COMPLETO ==========

    @Test
    @DisplayName("Flusso completo: Factory -> ParametroManager -> EventBus")
    void testFlussoCompleto() {
        // 1. GUI crea StanzaConfig tramite factory (simulando luminosità)
        // Nota: usiamo un valore numerico anche se la lampada è ON/OFF per semplicità
        StanzaConfig config = StanzaConfigFactory.creaConfigNumerico(
            "Soggiorno",
            DispositivoParameter.LUMINOSITA,
            5000.0  // valore luminosità
        );

        // 2. Verifica che la config sia stata creata correttamente
        assertNotNull(config);
        assertEquals("Soggiorno", config.getStanzaId());

        // 3. ParametroManager applica la configurazione
        boolean risultato = parametroManager.applicaStanzaConfig(config);

        // 4. Verifica che il comando sia stato inviato all'EventBus
        assertTrue(risultato, "L'applicazione della config dovrebbe avere successo");
        assertTrue(mockEventBus.isRequestSent(), "La request dovrebbe essere stata inviata");
        assertNotNull(mockEventBus.getLastRequest(), "Dovrebbe esserci una request salvata");
    }

    // ========== MOCK EVENTBUS CLIENT ==========

    /**
     * Mock semplice di IEventBusClient per i test
     */
    private static class MockEventBusClient implements IEventBusClient {
        private Request lastRequest;
        private boolean requestSent = false;

        @Override
        public void setRequest(Request request) {
            this.lastRequest = request;
        }

        @Override
        public Message sendRequest(Request request) {
            this.requestSent = true;
            return Message.ACK;  // Simula sempre successo
        }

        public Request getLastRequest() {
            return lastRequest;
        }

        public boolean isRequestSent() {
            return requestSent;
        }
    }

    // ========== MOCK STANZA DAO ==========

    /**
     * Mock semplice di StanzaDAO per i test (non richiede connessione DB)
     */
    private static class MockStanzaDAO implements StanzaDAO {

        @Override
        public void insertStanza(Stanza s) {
            // Mock: non fa nulla
        }

        @Override
        public Stanza readStanza(String id) {
            // Mock: restituisce null
            return null;
        }

        @Override
        public void updateStanza(Stanza s) {
            // Mock: non fa nulla
        }

        @Override
        public void deleteStanza(Stanza s) {
            // Mock: non fa nulla
        }

        @Override
        public Set<Stanza> readAllStanze() {
            // Mock: restituisce set vuoto
            return Set.of();
        }
    }
}

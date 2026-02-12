package it.unipv.posfw.smartdab.core.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import it.unipv.posfw.smartdab.adapter.facade.AttuatoreFacade;
import it.unipv.posfw.smartdab.core.beans.MisuraPOJO;
import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.model.casa.Casa;
import it.unipv.posfw.smartdab.core.domain.model.casa.Stanza;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.Dispositivo;
import it.unipv.posfw.smartdab.core.domain.model.parametro.IParametroValue;
import it.unipv.posfw.smartdab.core.domain.model.scenario.StanzaConfig;
import it.unipv.posfw.smartdab.core.port.communication.ICommandSender;
import it.unipv.posfw.smartdab.factory.StanzaConfigFactory;
import it.unipv.posfw.smartdab.infrastructure.messaging.topic.Topic;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.lampadaON_OFF.Lampada_ON_OFF;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.lampadaON_OFF.Lampada_Communicator;
import it.unipv.posfw.smartdab.core.domain.model.parametro.ObservableParameter;
import it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao.StanzaDAO;
import it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao.MisuraDAO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;

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
    private MockCommandSender mockCommandSender;
    private Lampada_ON_OFF lampada;

    @BeforeEach
    void setUp() {
        // Setup casa e stanza
        casa = new Casa();
        stanzaSoggiorno = new Stanza("S01", "Soggiorno", 25.0, LocalDateTime.now());
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
        gestoreStanze = new GestoreStanze(casa, new MockStanzaDAO(), new MockMisuraDAO());
        mockCommandSender = new MockCommandSender();
        parametroManager = new ParametroManager(gestoreStanze, mockCommandSender);
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

    // ========== TEST RICERCA ATTUATORE IDONEO ==========

    @Test
    @DisplayName("ParametroManager trova attuatore idoneo per LUMINOSITA")
    void testTrovaAttuatoreIdoneo() {
        // Nota: getAttuatoreIdoneo cerca per stanzaId, non per nome
        AttuatoreFacade trovato = parametroManager.getAttuatoreIdoneo(
            "S01",  // ID della stanza, non il nome
            DispositivoParameter.LUMINOSITA
        );

        assertNotNull(trovato, "Dovrebbe trovare la lampada");
        assertEquals("Lamp01", trovato.getTopic().getId());
    }

    @Test
    @DisplayName("ParametroManager restituisce null se parametro non supportato")
    void testNonTrovaAttuatorePerParametroNonSupportato() {
        AttuatoreFacade trovato = parametroManager.getAttuatoreIdoneo(
            "S01",  // ID della stanza
            DispositivoParameter.TEMPERATURA  // La lampada non supporta temperatura
        );

        assertNull(trovato, "Non dovrebbe trovare dispositivi per TEMPERATURA");
    }

    @Test
    @DisplayName("ParametroManager lancia eccezione per stanza inesistente")
    void testStanzaInesistente() {
        // Ora getAttuatoreIdoneo lancia StanzaNonTrovataException per stanze inesistenti
        assertThrows(Exception.class, () -> {
            parametroManager.getAttuatoreIdoneo(
                "StanzaFantasma",
                DispositivoParameter.LUMINOSITA
            );
        });
    }

    @Test
    @DisplayName("ParametroManager ignora dispositivi non attivi")
    void testIgnoraDispositivoNonAttivo() {
        lampada.switchDispositivo(); // Disattiva la lampada

        AttuatoreFacade trovato = parametroManager.getAttuatoreIdoneo(
            "S01",  // ID della stanza
            DispositivoParameter.LUMINOSITA
        );

        assertNull(trovato, "Non dovrebbe trovare dispositivi non attivi");
    }

    // ========== TEST FLUSSO COMPLETO ==========

    @Test
    @DisplayName("Flusso completo: Factory -> ParametroManager -> CommandSender")
    void testFlussoCompleto() {
        // 1. GUI crea StanzaConfig tramite factory (simulando luminosità)
        // Nota: usiamo l'ID della stanza, non il nome
        StanzaConfig config = StanzaConfigFactory.creaConfigNumerico(
            "S01",  // ID della stanza
            DispositivoParameter.LUMINOSITA,
            5000.0  // valore luminosità
        );

        // 2. Verifica che la config sia stata creata correttamente
        assertNotNull(config);
        assertEquals("S01", config.getStanzaId());

        // 3. ParametroManager applica la configurazione (ora è void, non boolean)
        // Se fallisce, lancera' un'eccezione
        assertDoesNotThrow(() -> parametroManager.applicaStanzaConfig(config));

        // 4. Verifica che il comando sia stato inviato via ICommandSender
        assertTrue(mockCommandSender.isCommandSent(), "Il comando dovrebbe essere stato inviato");
        assertNotNull(mockCommandSender.getLastDispositivo(), "Dovrebbe esserci un dispositivo salvato");
        assertEquals(DispositivoParameter.LUMINOSITA, mockCommandSender.getLastTipoParametro());
    }

    // ========== MOCK COMMAND SENDER ==========

    /**
     * Mock semplice di ICommandSender per i test
     */
    private static class MockCommandSender implements ICommandSender {
        private Dispositivo lastDispositivo;
        private DispositivoParameter lastTipoParametro;
        private IParametroValue lastValore;
        private boolean commandSent = false;

        @Override
        public boolean inviaComando(Dispositivo dispositivo, DispositivoParameter tipo, IParametroValue valore) {
            this.lastDispositivo = dispositivo;
            this.lastTipoParametro = tipo;
            this.lastValore = valore;
            this.commandSent = true;
            return true;  // Simula sempre successo
        }

        public Dispositivo getLastDispositivo() {
            return lastDispositivo;
        }

        public DispositivoParameter getLastTipoParametro() {
            return lastTipoParametro;
        }

        public IParametroValue getLastValore() {
            return lastValore;
        }

        public boolean isCommandSent() {
            return commandSent;
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

    // ========== MOCK MISURA DAO ==========

    /**
     * Mock semplice di MisuraDAO per i test (non richiede connessione DB)
     */
    private static class MockMisuraDAO implements MisuraDAO {

        @Override
        public void insertMisura(MisuraPOJO m) {
            // Mock: non fa nulla
        }

        @Override
        public List<MisuraPOJO> readMisuraStanza(String idStanza) {
            // Mock: restituisce lista vuota
            return new ArrayList<>();
        }

        @Override
        public List<MisuraPOJO> readUltimeMisurePerStanza() {
            // Mock: restituisce lista vuota
            return new ArrayList<>();
        }

        @Override
        public MisuraPOJO readUltimaMisura(String idStanza, String tipo) {
            // Mock: restituisce null
            return null;
        }
    }
}

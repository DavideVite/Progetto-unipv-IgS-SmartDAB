package it.unipv.posfw.smartdab.core.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameters;
import it.unipv.posfw.smartdab.core.domain.enums.Message;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.Dispositivo;
import it.unipv.posfw.smartdab.core.domain.model.parametro.BooleanValue;
import it.unipv.posfw.smartdab.core.port.messaging.IEventBusClient;
import it.unipv.posfw.smartdab.infrastructure.messaging.request.Request;
import it.unipv.posfw.smartdab.infrastructure.messaging.topic.Topic;

@ExtendWith(MockitoExtension.class)
class ParametroManagerTest {

    @Mock
    private GestoreStanze gestoreStanze;

    @Mock
    private IEventBusClient eventBusClient;

    @Mock
    private Dispositivo dispositivo;

    @Mock
    private Topic topic;

    private ParametroManager parametroManager;

    @BeforeEach
    void setUp() {
        parametroManager = new ParametroManager(gestoreStanze, eventBusClient);
    }

    @Test
    void impostaParametro_chiamaSetRequestESendRequest() {
        // Arrange
        String stanzaId = "stanza1";
        DispositivoParameters tipoParametro = DispositivoParameters.LUMINOSITA;
        BooleanValue valore = new BooleanValue(true, "Acceso", "Spento");

        when(gestoreStanze.getDispositiviPerStanza(stanzaId))
            .thenReturn(List.of(dispositivo));
        when(dispositivo.isAttuatore()).thenReturn(true);
        when(dispositivo.isActive()).thenReturn(true);
        when(dispositivo.getTopic()).thenReturn(topic);
        when(topic.getParameter()).thenReturn(tipoParametro);
        when(eventBusClient.sendRequest(any(Request.class)))
            .thenReturn(Message.ACK);

        // Act
        boolean result = parametroManager.impostaParametro(stanzaId, tipoParametro, valore);

        // Assert
        assertTrue(result);
        verify(eventBusClient).setRequest(any(Request.class));
        verify(eventBusClient).sendRequest(any(Request.class));
    }

    @Test
    void impostaParametro_ritornaFalse_quandoEventBusRitornaError() {
        // Arrange
        String stanzaId = "stanza1";
        DispositivoParameters tipoParametro = DispositivoParameters.LUMINOSITA;
        BooleanValue valore = new BooleanValue(true, "Acceso", "Spento");

        when(gestoreStanze.getDispositiviPerStanza(stanzaId))
            .thenReturn(List.of(dispositivo));
        when(dispositivo.isAttuatore()).thenReturn(true);
        when(dispositivo.isActive()).thenReturn(true);
        when(dispositivo.getTopic()).thenReturn(topic);
        when(topic.getParameter()).thenReturn(tipoParametro);
        when(eventBusClient.sendRequest(any(Request.class)))
            .thenReturn(Message.ERROR);

        // Act
        boolean result = parametroManager.impostaParametro(stanzaId, tipoParametro, valore);

        // Assert
        assertFalse(result);
    }

    @Test
    void impostaParametro_ritornaFalse_quandoNessunDispositivoAttivo() {
        // Arrange
        String stanzaId = "stanza1";
        DispositivoParameters tipoParametro = DispositivoParameters.LUMINOSITA;
        BooleanValue valore = new BooleanValue(true, "Acceso", "Spento");

        when(gestoreStanze.getDispositiviPerStanza(stanzaId))
            .thenReturn(List.of(dispositivo));
        when(dispositivo.isAttuatore()).thenReturn(true);
        when(dispositivo.isActive()).thenReturn(false);

        // Act
        boolean result = parametroManager.impostaParametro(stanzaId, tipoParametro, valore);

        // Assert
        assertFalse(result);
        verify(eventBusClient, never()).setRequest(any());
        verify(eventBusClient, never()).sendRequest(any());
    }

    @Test
    void impostaParametro_ritornaFalse_quandoValoreNonValido() {
        // Arrange
        String stanzaId = "stanza1";
        DispositivoParameters tipoParametro = DispositivoParameters.LUMINOSITA;

        // Mock di IParametroValue che ritorna false su isValid()
        var valoreNonValido = mock(it.unipv.posfw.smartdab.core.domain.model.parametro.IParametroValue.class);
        when(valoreNonValido.isValid()).thenReturn(false);

        // Act
        boolean result = parametroManager.impostaParametro(stanzaId, tipoParametro, valoreNonValido);

        // Assert
        assertFalse(result);
        verify(eventBusClient, never()).setRequest(any());
        verify(eventBusClient, never()).sendRequest(any());
    }

    @Test
    void impostaParametro_ignoraSensori_usaSoloAttuatori() {
        // Arrange
        String stanzaId = "stanza1";
        DispositivoParameters tipoParametro = DispositivoParameters.TEMPERATURA;
        BooleanValue valore = new BooleanValue(true, "On", "Off");

        Dispositivo sensore = mock(Dispositivo.class);
        Dispositivo attuatore = mock(Dispositivo.class);
        Topic attuatoreTopic = mock(Topic.class);

        when(gestoreStanze.getDispositiviPerStanza(stanzaId))
            .thenReturn(List.of(sensore, attuatore));

        // Sensore: non è un attuatore
        when(sensore.isAttuatore()).thenReturn(false);

        // Attuatore: è un attuatore attivo con parametro corretto
        when(attuatore.isAttuatore()).thenReturn(true);
        when(attuatore.isActive()).thenReturn(true);
        when(attuatore.getTopic()).thenReturn(attuatoreTopic);
        when(attuatoreTopic.getParameter()).thenReturn(tipoParametro);
        when(eventBusClient.sendRequest(any(Request.class))).thenReturn(Message.ACK);

        // Act
        boolean result = parametroManager.impostaParametro(stanzaId, tipoParametro, valore);

        // Assert
        assertTrue(result);
        verify(eventBusClient).setRequest(any(Request.class));
        verify(eventBusClient).sendRequest(any(Request.class));
    }

    @Test
    void impostaParametro_ritornaFalse_quandoNessunAttuatoreSupportaParametro() {
        // Arrange
        String stanzaId = "stanza1";
        DispositivoParameters tipoRichiesto = DispositivoParameters.TEMPERATURA;
        DispositivoParameters tipoDisponibile = DispositivoParameters.LUMINOSITA;
        BooleanValue valore = new BooleanValue(true, "On", "Off");

        when(gestoreStanze.getDispositiviPerStanza(stanzaId))
            .thenReturn(List.of(dispositivo));
        when(dispositivo.isAttuatore()).thenReturn(true);
        when(dispositivo.isActive()).thenReturn(true);
        when(dispositivo.getTopic()).thenReturn(topic);
        when(topic.getParameter()).thenReturn(tipoDisponibile);

        // Act
        boolean result = parametroManager.impostaParametro(stanzaId, tipoRichiesto, valore);

        // Assert
        assertFalse(result);
        verify(eventBusClient, never()).setRequest(any());
        verify(eventBusClient, never()).sendRequest(any());
    }

    @Test
    void impostaParametro_usaPrimoAttuatoreCorrispondente() {
        // Arrange
        String stanzaId = "stanza1";
        DispositivoParameters tipoParametro = DispositivoParameters.TEMPERATURA;
        BooleanValue valore = new BooleanValue(true, "On", "Off");

        Dispositivo attuatore1 = mock(Dispositivo.class);
        Dispositivo attuatore2 = mock(Dispositivo.class);
        Topic topic1 = mock(Topic.class);
        Topic topic2 = mock(Topic.class);

        when(gestoreStanze.getDispositiviPerStanza(stanzaId))
            .thenReturn(List.of(attuatore1, attuatore2));

        when(attuatore1.isAttuatore()).thenReturn(true);
        when(attuatore1.isActive()).thenReturn(true);
        when(attuatore1.getTopic()).thenReturn(topic1);
        when(topic1.getParameter()).thenReturn(tipoParametro);

        when(attuatore2.isAttuatore()).thenReturn(true);
        when(attuatore2.isActive()).thenReturn(true);
        when(attuatore2.getTopic()).thenReturn(topic2);
        when(topic2.getParameter()).thenReturn(tipoParametro);

        when(eventBusClient.sendRequest(any(Request.class))).thenReturn(Message.ACK);

        // Act
        boolean result = parametroManager.impostaParametro(stanzaId, tipoParametro, valore);

        // Assert
        assertTrue(result);
        verify(eventBusClient, times(1)).setRequest(any(Request.class));
        verify(eventBusClient, times(1)).sendRequest(any(Request.class));
    }

    @Test
    void impostaParametro_gestisceTopicNull() {
        // Arrange
        String stanzaId = "stanza1";
        DispositivoParameters tipoParametro = DispositivoParameters.TEMPERATURA;
        BooleanValue valore = new BooleanValue(true, "On", "Off");

        when(gestoreStanze.getDispositiviPerStanza(stanzaId))
            .thenReturn(List.of(dispositivo));
        when(dispositivo.isAttuatore()).thenReturn(true);
        when(dispositivo.isActive()).thenReturn(true);
        when(dispositivo.getTopic()).thenReturn(null);

        // Act
        boolean result = parametroManager.impostaParametro(stanzaId, tipoParametro, valore);

        // Assert
        assertFalse(result);
        verify(eventBusClient, never()).setRequest(any());
        verify(eventBusClient, never()).sendRequest(any());
    }

    @Test
    void impostaParametro_saltaAttuatoriInattivi() {
        // Arrange
        String stanzaId = "stanza1";
        DispositivoParameters tipoParametro = DispositivoParameters.TEMPERATURA;
        BooleanValue valore = new BooleanValue(true, "On", "Off");

        Dispositivo attuatoreInattivo = mock(Dispositivo.class);
        Dispositivo attuatoreAttivo = mock(Dispositivo.class);
        Topic topicAttivo = mock(Topic.class);

        when(gestoreStanze.getDispositiviPerStanza(stanzaId))
            .thenReturn(List.of(attuatoreInattivo, attuatoreAttivo));

        when(attuatoreInattivo.isAttuatore()).thenReturn(true);
        when(attuatoreInattivo.isActive()).thenReturn(false);

        when(attuatoreAttivo.isAttuatore()).thenReturn(true);
        when(attuatoreAttivo.isActive()).thenReturn(true);
        when(attuatoreAttivo.getTopic()).thenReturn(topicAttivo);
        when(topicAttivo.getParameter()).thenReturn(tipoParametro);

        when(eventBusClient.sendRequest(any(Request.class))).thenReturn(Message.ACK);

        // Act
        boolean result = parametroManager.impostaParametro(stanzaId, tipoParametro, valore);

        // Assert
        assertTrue(result);
    }
}

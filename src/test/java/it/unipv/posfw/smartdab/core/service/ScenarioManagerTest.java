package it.unipv.posfw.smartdab.core.service;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameters;
import it.unipv.posfw.smartdab.core.domain.enums.EnumScenarioType;
import it.unipv.posfw.smartdab.core.domain.model.scenario.Scenario;
import it.unipv.posfw.smartdab.core.domain.model.scenario.ScenarioStanzaConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ScenarioManagerTest {

    private ScenarioManager manager;

    @BeforeEach
    void setUp() {
        manager = new ScenarioManager();
    }

    @Test
    @DisplayName("Crea scenario vuoto e popola con configurazioni tramite Manager")
    void creaScenarioVuoto_ePopolaConConfig_funzionaCorrettamente() {
        // 1) Creare scenario vuoto
        Scenario scenario = manager.creaScenario("Relax", EnumScenarioType.PERSONALIZZATO);

        assertNotNull(scenario);
        assertEquals("Relax", scenario.getNome());
        assertTrue(scenario.getConfigurazioni().isEmpty());

        // 2) Popolarlo con ScenarioStanzaConfig tramite Manager
        ScenarioStanzaConfig config = manager.creaConfigNumerico(
                "soggiorno",
                DispositivoParameters.TEMPERATURA,
                22.0, 0.0, 40.0, "°C"
        );
        boolean aggiunto = manager.aggiungiConfigurazione("Relax", config);

        assertTrue(aggiunto);
        assertEquals(1, scenario.getConfigurazioni().size());
        assertEquals("soggiorno", scenario.getConfigurazioni().get(0).getStanzaId());
    }

    @Test
    @DisplayName("Rimozione configurazione tramite Manager")
    void rimuoviConfigurazione_tramiteManager_funzionaCorrettamente() {
        manager.creaScenario("Relax", EnumScenarioType.PERSONALIZZATO);
        ScenarioStanzaConfig config = manager.creaConfigNumerico(
                "soggiorno",
                DispositivoParameters.TEMPERATURA,
                22.0, 0.0, 40.0, "°C"
        );
        manager.aggiungiConfigurazione("Relax", config);

        boolean rimosso = manager.rimuoviConfigurazione("Relax", config);

        assertTrue(rimosso);
        assertTrue(manager.getScenario("Relax").getConfigurazioni().isEmpty());
    }

    @Test
    @DisplayName("Creazione scenario con nome duplicato lancia eccezione")
    void creaScenario_conNomeDuplicato_lanciaEccezione() {
        manager.creaScenario("Relax", EnumScenarioType.PERSONALIZZATO);

        assertThrows(IllegalArgumentException.class, () -> {
            manager.creaScenario("Relax", EnumScenarioType.PERSONALIZZATO);
        });
    }
}

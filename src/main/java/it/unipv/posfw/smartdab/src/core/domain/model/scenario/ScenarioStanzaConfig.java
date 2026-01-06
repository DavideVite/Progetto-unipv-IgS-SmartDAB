package main.java.it.unipv.posfw.smartdab.src.core.domain.model.scenario;


public class ScenarioStanzaConfig {

	private String stanzaId;
	private IParametroValue valore; 
	private EnumTipoParametro tipo_parametro; 
	
	public ScenarioStanzaConfig (String stanzaId, IParametroValue valore, EnumTipoParametro tipo_parametro)  {
		this.stanzaId = stanzaId;
		this.valore = valore;
		this.tipo_parametro = tipo_parametro;
	}

	public String getStanzaId() {
		return stanzaId;
	}

	public IParametroValue getParametro() {
		return valore;
	}
		
	
	public EnumTipoParametro getTipo_parametro() {
		return tipo_parametro; 
	}

	@Override
	public String toString() {
		return "ScenarioStanzaConfig [stanzaId=" + stanzaId + ", valore=" + valore + ", tipo_parametro="
				+ tipo_parametro + "]";
	}
	
	
}

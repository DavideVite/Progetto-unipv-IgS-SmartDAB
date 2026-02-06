package it.unipv.posfw.smartdab.core.domain.model.scenario;

import java.util.Objects;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.model.parametro.IParametroValue;

public class StanzaConfig {

	private final String stanzaId;
	private final IParametroValue valore;
	private final DispositivoParameter tipo_parametro;

	public StanzaConfig(String stanzaId, IParametroValue valore, DispositivoParameter tipo_parametro) {
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

	public DispositivoParameter getTipo_parametro() {
		return tipo_parametro;
	}

	@Override
	public String toString() {
		return "StanzaConfig [stanzaId=" + stanzaId + ", valore=" + valore + ", tipo_parametro="
				+ tipo_parametro + "]";
	}

    @Override
    public int hashCode() {
        return Objects.hash(this.stanzaId, this.tipo_parametro);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final StanzaConfig other = (StanzaConfig) obj;
        return Objects.equals(this.stanzaId, other.stanzaId)
            && this.tipo_parametro == other.tipo_parametro;
    }
}

package it.unipv.posfw.smartdab.core.domain.model.parametro;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;

public interface IParametroValue {

	String getDisplayString();
	boolean isValid();
	DispositivoParameter getTipoParametro();
}

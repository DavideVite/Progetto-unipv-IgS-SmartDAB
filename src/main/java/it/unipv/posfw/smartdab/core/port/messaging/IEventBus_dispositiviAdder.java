package it.unipv.posfw.smartdab.core.port.messaging;

import it.unipv.posfw.smartdab.core.domain.model.dispositivo.Dispositivo;

public interface IEventBus_dispositiviAdder {
	public boolean addDispositivo(Dispositivo d);
}

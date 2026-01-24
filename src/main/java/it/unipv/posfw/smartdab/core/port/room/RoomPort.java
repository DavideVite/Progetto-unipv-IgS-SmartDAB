package it.unipv.posfw.smartdab.core.port.room;

import java.util.List;

import it.unipv.posfw.smartdab.core.domain.model.dispositivo.Dispositivo;
import it.unipv.posfw.smartdab.core.port.communication.observer.Observer;

public interface RoomPort extends Observer{
	public String getId();
	public List<Dispositivo> getDispositivi();
}

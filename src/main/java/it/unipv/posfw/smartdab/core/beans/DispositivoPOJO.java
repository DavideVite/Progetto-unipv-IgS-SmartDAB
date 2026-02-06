package it.unipv.posfw.smartdab.core.beans;

import java.time.LocalDateTime;

import it.unipv.posfw.smartdab.adapter.facade.AttuatoreFacade;
import it.unipv.posfw.smartdab.adapter.facade.SensoreFacade;
import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.enums.DispositivoStates;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.Dispositivo;

public class DispositivoPOJO {
	private String id;
	private String stanza;
	private DispositivoParameter parametro;
	private String tipo; // Sensore/Attuatore
	private DispositivoStates stato;
	private boolean attivo;
	private LocalDateTime created_at;
	
	public DispositivoPOJO(String id, String stanza, DispositivoParameter parametro, 
						   String tipo, DispositivoStates stato, boolean attivo) {
		this.id = id;
		this.stanza = stanza;
		this.parametro = parametro;
		this.tipo = tipo.toUpperCase();
		this.stato = stato;
		this.attivo = attivo;
		created_at = LocalDateTime.now();
	}
	
	public DispositivoPOJO(String id, String stanza, DispositivoParameter parametro, 
			String tipo, DispositivoStates stato, boolean attivo, LocalDateTime t) {
		this.id = id;
		this.stanza = stanza;
		this.parametro = parametro;
		this.tipo = tipo.toUpperCase();
		this.stato = stato;
		this.attivo = attivo;
		created_at = t;
	}
	
	public DispositivoPOJO(Dispositivo d) {
		this.id = d.getTopic().getId();
		this.stanza = d.getTopic().getRoom();
		this.parametro = d.getTopic().getParameter();
		
		try {
			d = (AttuatoreFacade)d;
			this.tipo = "ATTUATORE";
		} catch(ClassCastException e) {
			d = (SensoreFacade)d;
			this.tipo = "SENSORE";
		} finally {
			this.tipo = "NESSUNO";
		}
		
		this.stato = d.getState();
		this.attivo = d.isActive();
		created_at = LocalDateTime.now();
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStanza() {
		return stanza;
	}

	public void setStanza(String stanza) {
		this.stanza = stanza;
	}

	public String getTipo() {
		return tipo;
	}
	
	public void setTipo(String tipo) {
		this.tipo = tipo.toUpperCase();
	}
	
	public LocalDateTime getCreated_at() {
		return created_at;
	}
	
	public void setCreated_at(LocalDateTime created_at) {
		this.created_at = created_at;
	}

	@Override
	public String toString() {
		return "Dispositivo di " + parametro + " (" + id +
				" di tipo " + tipo + ", contenuto nella stanza " + 
				stanza + ", attualmente " + stato + " e " + (attivo ? "ATTIVO" : "SPENTO") + 
				", aggiunto il " + created_at.toString() + ").";
	}

	public boolean isAttivo() {
		return attivo;
	}

	public void setAttivo(boolean attivo) {
		this.attivo = attivo;
	}

	public DispositivoStates getStato() {
		return stato;
	}

	public void setStato(DispositivoStates stato) {
		this.stato = stato;
	}

	public DispositivoParameter getParametro() {
		return parametro;
	}

	public void setParametro(DispositivoParameter parametro) {
		this.parametro = parametro;
	}
	
	
}

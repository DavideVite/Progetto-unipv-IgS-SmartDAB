package it.unipv.posfw.smartdab.core.beans;

import java.time.LocalDateTime;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.enums.DispositivoStates;

public class DispositivoPOJO {
	private String id;
	private String stanza;
	private DispositivoParameter parametro;
	private String tipo; // Sensore/Attuatore
	private DispositivoStates stato;
	private boolean attivo;
	private LocalDateTime created_at;
	private String model;
	
	public DispositivoPOJO(String id, String stanza, DispositivoParameter parametro, 
						   String tipo, DispositivoStates stato, boolean attivo, String model) {
		this.id = id;
		this.stanza = stanza;
		this.parametro = parametro;
		this.tipo = tipo.toUpperCase();
		this.stato = stato;
		this.attivo = attivo;
		this.model = model;
		created_at = LocalDateTime.now();
	}
	
	public DispositivoPOJO(String id, String stanza, DispositivoParameter parametro, 
			String tipo, DispositivoStates stato, boolean attivo, LocalDateTime t, String model) {
		this.id = id;
		this.stanza = stanza;
		this.parametro = parametro;
		this.tipo = tipo.toUpperCase();
		this.stato = stato;
		this.attivo = attivo;
		this.model = model;
		created_at = t;
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

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}
	
	
}

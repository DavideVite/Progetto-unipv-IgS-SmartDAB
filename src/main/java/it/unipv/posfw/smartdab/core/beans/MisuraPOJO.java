package it.unipv.posfw.smartdab.core.beans;

import java.time.LocalDateTime;

public class MisuraPOJO {
	private int id;
	private String tipo;
	private String unita;
	private double valore;
	private String idStanza;
	private LocalDateTime timestamp;
	
	public MisuraPOJO (int id, String tipo, String unita, double valore, String idStanza, LocalDateTime timestamp) {
		this.id = id;
		this.tipo = tipo;
		this.unita = unita;
		this.valore = valore;
		this.idStanza = idStanza;
		this.timestamp = timestamp;
	}

	public int getId() {
		return id;
	}

	public String getUnita() {
		return unita;
	}

	public double getValore() {
		return valore;
	}

	public String getTipo() {
		return tipo;
	}

	public String getIdStanza() {
		return idStanza;
	}
	
	public LocalDateTime getTimestamp() {
		return timestamp;
	}

}


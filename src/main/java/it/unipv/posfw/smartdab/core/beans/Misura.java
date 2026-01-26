package it.unipv.posfw.smartdab.core.beans;

import java.sql.Timestamp;

public class Misura {
	private String id;
	private String tipo;
	private String unita;
	private double valore;
	private String idStanza;
	private Timestamp timestamp;
	
	public Misura (String id, String tipo, String unita, double valore, String idStanza, Timestamp timestamp) {
		this.id = id;
		this.tipo = tipo;
		this.unita = unita;
		this.valore = valore;
		this.idStanza = idStanza;
		this.timestamp = timestamp;
	}

	public String getId() {
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
	
	public Timestamp getTimestamp() {
		return timestamp;
	}

}


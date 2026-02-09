package it.unipv.posfw.smartdab.core.beans;

import java.time.LocalDateTime;

/**
 * Questa classe è un contenitore di informazioni di una comunicazione del sistema SmartDAB
 * @author Alessandro Ingenito
 * @version 1.0
 */

public class CommunicationPOJO {
	private int id;
	private String esito;
	private String tipo;
	private Object value;
	private String dispositivo;
	private LocalDateTime last_update;

	// Formato corto
	public CommunicationPOJO(String tipo, Object value, String dispositivo) {
		this.tipo = tipo;
		this.value = value;
		this.dispositivo = dispositivo;
		this.esito = "PENDING";
		last_update = LocalDateTime.now();
	}

	// Formato con esito
	public CommunicationPOJO(String esito, String tipo, Object value, String dispositivo) {
		this.esito = esito;
		this.tipo = tipo;
		this.value = value;
		this.dispositivo = dispositivo;
		last_update = LocalDateTime.now();
	}

	// Formato esteso: prelevato dalla base di dati
	public CommunicationPOJO(int id, String esito, String tipo, Object value, String dispositivo, LocalDateTime t) {
		this.id = id;
		this.esito = esito;
		this.tipo = tipo;
		this.value = value;
		this.dispositivo = dispositivo;
		last_update = t;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public LocalDateTime getLast_update() {
		return last_update;
	}
	
	public void setLast_update(LocalDateTime last_update) {
		this.last_update = last_update;
	}
	
	public void setLast_update() {
		this.last_update = LocalDateTime.now();
	}
	
	public String getEsito() {
		return esito;
	}
	
	public void setEsito(String esito) {
		this.esito = esito;
	}
	
	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getDispositivo() {
		return dispositivo;
	}

	public void setDispositivo(String dispositivo) {
		this.dispositivo = dispositivo;
	}

	
}

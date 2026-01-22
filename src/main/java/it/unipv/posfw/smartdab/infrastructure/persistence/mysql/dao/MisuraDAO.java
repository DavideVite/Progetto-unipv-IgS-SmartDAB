package it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao;

import java.util.List;

import it.unipv.posfw.smartdab.core.beans.Misura;

public interface MisuraDAO {
	
	public void insertMisura(Misura m);
	
	public List<Misura> readMisuraStanza (String idStanza);
	
	public Misura readUltimaMisura (String idStanza, String tipo);

}

package it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao;

import java.util.List;

import it.unipv.posfw.smartdab.core.beans.MisuraPOJO;

public interface MisuraDAO {
	
	public void insertMisura(MisuraPOJO m);
	
	public List<MisuraPOJO> readMisuraStanza (String idStanza);
	
	public List<MisuraPOJO> readUltimeMisurePerStanza ();

}

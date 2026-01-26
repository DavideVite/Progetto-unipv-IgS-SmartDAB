package it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao;

import java.util.Set;

import it.unipv.posfw.smartdab.core.domain.model.casa.Stanza;

public interface StanzaDAO {
	
	public void insertStanza(Stanza s);
	
	public Stanza readStanza(String id);
	
	public void updateStanza(Stanza s);
	
	public void deleteStanza(String id);
	
	public Set<Stanza> readAllStanze();

}

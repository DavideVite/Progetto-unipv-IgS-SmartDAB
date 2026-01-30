package it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao;

import java.util.ArrayList;

import it.unipv.posfw.smartdab.core.beans.CommunicationPOJO;

public interface CommunicationDAO {
    ArrayList<CommunicationPOJO> selectN(int n);
    
	public boolean insertCommunication(CommunicationPOJO c);
	
	public boolean updateCommunication(CommunicationPOJO c);
	
	public boolean existsById(int id);
}

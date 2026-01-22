package it.unipv.posfw.smartdab.infrastructure.persistence.mysql.DAO;

import java.util.ArrayList;

import it.unipv.posfw.smartdab.core.beans.CommunicationPOJO;

public interface CommunicationDAO {
	public ArrayList<CommunicationPOJO> selectN(int n);
}

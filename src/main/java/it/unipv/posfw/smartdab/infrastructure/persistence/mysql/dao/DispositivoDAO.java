package it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao;

import java.util.ArrayList;

import it.unipv.posfw.smartdab.core.beans.DispositivoPOJO;

public interface DispositivoDAO {
	public ArrayList<DispositivoPOJO> selectN(int n);
	
	public boolean insertDispositivo(DispositivoPOJO d);
	
	public boolean updateDispositivo(DispositivoPOJO d);
	
	public boolean disableDispositivo(String id);
	
	public boolean existsById(String id);
}

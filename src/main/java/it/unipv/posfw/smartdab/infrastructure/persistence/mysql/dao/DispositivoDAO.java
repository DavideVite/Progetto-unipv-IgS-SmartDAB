package it.unipv.posfw.smartdab.infrastructure.persistence.mysql.DAO;

import java.util.ArrayList;

import it.unipv.posfw.smartdab.core.beans.DispositivoPOJO;

public interface DispositivoDAO {
	public ArrayList<DispositivoPOJO> selectN(int n);
}

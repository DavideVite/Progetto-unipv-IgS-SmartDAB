package it.unipv.posfw.smartdab.infrastructure.persistence.mysql.dao;

import java.util.ArrayList;

import it.unipv.posfw.smartdab.core.beans.CommunicationPOJO;

public interface CommunicationDAO {
    ArrayList<CommunicationPOJO> selectN(int n);
}

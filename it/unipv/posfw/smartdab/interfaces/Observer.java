package it.unipv.posfw.smartdab.interfaces;

import java.util.EventObject;

public interface Observer {
	void update(Observable o, Object args);
}

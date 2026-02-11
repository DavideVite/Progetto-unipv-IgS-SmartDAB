package it.unipv.posfw.smartdab.factory;

import java.util.Properties;
import java.io.FileInputStream;
import java.lang.reflect.Method;

import it.unipv.posfw.smartdab.infrastructure.messaging.EventBus;
import it.unipv.posfw.smartdab.core.service.DispositiviManager;

/**
 * Classe factory per la gestione della prima istanza dell'event bus tramite properties
 * @author Alessandro Ingenito
 * @version 1.0
 */

public class EventBusFactory {
	private static EventBus event;
	private static final String E_PROPERTYNAME = "eventbus.dispositiviobserver.class.name";

	public static EventBus getEventBus(DispositiviManager dm) {
		if(event == null) {
			String eventBusClassName;

			try {

				Properties p = new Properties(System.getProperties());
				p.load(new FileInputStream("properties/dispositiviObserver_properties.txt"));
				eventBusClassName = p.getProperty(E_PROPERTYNAME);
				Class<?> c = Class.forName(eventBusClassName);
				Method getInstance = c.getMethod("getInstance", DispositiviManager.class);
				
				return (EventBus)getInstance.invoke(null, dm);
				 

			} catch(Exception e) {
				e.printStackTrace();
			}
		}

		return event;
	}
}

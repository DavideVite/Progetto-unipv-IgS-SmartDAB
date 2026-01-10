package main.java.it.unipv.posfw.smartdab.factory;

import java.util.Properties;
import java.io.FileInputStream;
import java.lang.reflect.Constructor;

import main.java.it.unipv.posfw.smartdab.infrastructure.messaging.EventBus;

public class EventBusFactory {
	private static EventBus event;
	private static final String E_PROPERTYNAME = "eventbus.dispositiviobserver.class.name";

	public static EventBus getEventBus() {
		if(event == null) {
			String eventBusClassName;

			try {

				Properties p = new Properties(System.getProperties());
				p.load(new FileInputStream("properties/dispositiviObserver_properties.txt"));
				eventBusClassName = p.getProperty(E_PROPERTYNAME);
				Constructor<?> c = Class.forName(eventBusClassName).getConstructor();
				event = (EventBus)c.newInstance();

			} catch(Exception e) {
				e.printStackTrace();
			}
		}

		return event;
	}
}

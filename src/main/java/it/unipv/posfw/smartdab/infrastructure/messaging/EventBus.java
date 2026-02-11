package it.unipv.posfw.smartdab.infrastructure.messaging;

import java.util.ArrayList;
import java.util.Iterator;

import it.unipv.posfw.smartdab.adapter.facade.AttuatoreFacade;
import it.unipv.posfw.smartdab.core.beans.DispositivoPOJO;
import it.unipv.posfw.smartdab.core.domain.enums.DispositivoStates;
import it.unipv.posfw.smartdab.core.domain.enums.Message;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.Dispositivo;
import it.unipv.posfw.smartdab.core.port.messaging.IEventBusClient;
import it.unipv.posfw.smartdab.core.port.messaging.IEventBusMalfunzionamenti;
import it.unipv.posfw.smartdab.core.port.messaging.IEventBus_dispositiviAdder;
import it.unipv.posfw.smartdab.core.service.DispositiviManager;
import it.unipv.posfw.smartdab.infrastructure.messaging.request.Request;

/**
 * Questa classe rappresenta il punto di contatto tra i dispositivi e il sistema SmartDAB, 
 * ovvero ne consente lo scambio di messaggi o eventi
 * @see Dispositivi
 * @author Alessandro Ingenito
 * @version 1.3
 */

public class EventBus implements DispositiviObserver, IEventBusClient, IEventBus_dispositiviAdder, IEventBusMalfunzionamenti {
	private ArrayList<Dispositivo> dispositivi = new ArrayList<>();
	private DispositiviManager dispositiviManager;
	private static EventBus instance = null;
	private Request request;
	
	/* Come chiamare l'event bus:
	 * 1) setRequest()
	 * 2) sendRequest() <- Richiede la conoscenza della richiesta da mandare, seguendo notazione
	 * 3) Restituisce un Messaggio che informa sulla buona riuscita dell'operazione
	 * 
	 * Oppure per i sensori:
	 * 
	 * 1) update()
	 * 2) L'event bus aggiorna tutti i subrscriber
	 */
	
	
	// Quando event bus riceve una request = topic (home/room/dispositivo/parameter) + type + val
	
	public void setRequest(Request request) {
		this.request = request;
	}
	
	public boolean addDispositivo(Dispositivo d) {
		if(dispositivi.add(d)) {
			dispositiviManager.aggiungiDispositivo(new DispositivoPOJO(d));
			return true;
		}
		
		System.out.println("Il dispositivo " + d.getTopic().getId() + " è già presente nella lista");
		return false;
	}
	
	
	// Per i test
	public void clearDispositivi() {
		try {
			dispositivi.clear();
			dispositiviManager.clearDispositivi();
		} catch(NullPointerException e) {
			
		}
	}

	
	/**
	 * 
	 * @param id del dispositivo da cercare
	 * @return restituisce il dispositivo con quell'id presente nella lista, altrimenti restituisce null
	 */
	
	public Dispositivo searchDispositivoByName(String name) {
		Iterator<Dispositivo> iter = dispositivi.iterator();
		Dispositivo d;
		
		while(iter.hasNext()) {
			d = iter.next();
			if(name.equals(d.getTopic().getId())) {
				return d;
			}
		}
		
		System.out.println("Dispositivo " + name + " non trovato");
		return null;
	}
	
	// Metodo per rilevazione di malfunzionamenti
	public void disableDispositivo(DispositivoPOJO d) {
		searchDispositivoByName(d.getId()).setState(DispositivoStates.DISABLED);
		dispositiviManager.disableDispositivo(d.getId());
	}
	
	/**
	 * @return restituisce tutti gli iscritti a un topic (parametro + stanza)
	 */
	
	public ArrayList<Dispositivo> getSubscribers() {
		Iterator<Dispositivo> iter = dispositivi.iterator();
		ArrayList<Dispositivo> subs = new ArrayList<>();
		Dispositivo d;
		
		while(iter.hasNext()) {
			d = iter.next();
			
			
			// Confronto le caratteristiche dei dispositivi con quelle ricercate e verifico che
			// non siano disabilitati o in conflitto
			if(request.getTopic().getParameter().equals(d.getTopic().getParameter()) &&
			   request.getTopic().getRoom().equals(d.getTopic().getRoom()) &&
			   
			   !(d.getState().toString().equals(DispositivoStates.DISABLED.toString()) ||
				 d.getState().toString().equals(DispositivoStates.CONFLICT.toString())	 )
			   
				) {
				
				// Se il dispositivo è un attuatore lo inserisco, altrimenti passo avanti
				
				try {
					d = (AttuatoreFacade)d;
					subs.add(d);
				} catch(ClassCastException e) {
					continue;
				}

			}
		}
		return subs;
	}
	
	/**
	 * Metodo per inviare le richieste ai dispositivi specificati nella richiesta
	 * 
	 * @param request contiene le informazioni sul dispositivo da raggiungere e cosa fare
	 */
	
	public Message sendRequest(Request request) {
		return searchDispositivoByName(request.getTopic().getId()
				).getCommunicator().processRequest(request);
	}
	
	private EventBus(DispositiviManager dm) {
		dispositiviManager = dm;
	}
	
	/**
	 * Metodo per la gestione del pattern Singleton
	 */
	
	public static EventBus getInstance(DispositiviManager dm) {
		if(instance == null) {
			instance = new EventBus(dm);
		}
		
		return instance;
	}

	
	// Metodo esclusivo dei sensori che misurano dei parametri o per aggiornare lo stato dei dispositivi
	
	// topic = "home/room/sensore/parameter"
	// type = "UPDATE.PARAMETER"
	// val = payload
	
	/**
	 * Questo metodo è quello che i communicator dei dispositivi chiamano per inviare informazioni
	 * @param request request = topic (es. "home/room/sensore/parameter") + type (es. UPDATE.PARAMETER) + val (es. 25)
	 */
	
	@Override
	public Message update(Request request) {
		
		Dispositivo sender = searchDispositivoByName(request.getTopic().getId());
		
		if(sender == null) {
			System.out.println("Dispositivo " + request.getTopic().getId() + " non riconosciuto");
			return Message.ERROR;
		}
		
		if(sender.getState().equals(DispositivoStates.CONFLICT)) {
			System.out.println("Richiesta di " + request.getTopic().getId() + " respinta");
			return Message.ERROR;
		}
		
		
		String type = request.getType();
		
		// Verifico se il messaggio arrivato è pertinente al metodo
		if(type.equals(Message.UPDATE + "." + Message.PARAMETER)) {
			
			// Prendi tutti gli iscritti al topic e itera
			Iterator<Dispositivo> iterSubs = getSubscribers().iterator();
			Dispositivo d;
			

			while(iterSubs.hasNext()) {
				d = iterSubs.next();
				
				
				request.setRequest(d.getTopic(), Message.ACT.toString(), request.getVal());
				setRequest(request);
				
				// Manda richiesta
				for(int i = 0; i < 10; i++) {
					
					// Se la richiesta va a buon fine
					if(sendRequest(request).equals(Message.ACK)) {
						
						// Se un dispositivo in stato UNKNOWN risponde, allora è ALIVE
						if(d.getState().equals(DispositivoStates.UNKNOWN))
							d.setState(DispositivoStates.ALIVE);
							break;
					}

					// Se il dispositivo non risponde a 10 chiamate allora vado al prossimo
					else if(i == 9) {
						System.out.println("Dispositivo " + request.getTopic().getId() + " non ha risposto");
						
						// Il suo stato attuale è ignoto data l'assenza di risposta
						if(d.getState().equals(DispositivoStates.ALIVE))
							d.setState(DispositivoStates.UNKNOWN);
						
						// Un dispositivo con stato ignoto viene disabilitato se non risponde
						else if(d.getState().equals(DispositivoStates.UNKNOWN)) {
							d.setState(DispositivoStates.DISABLED);
						}
						
						dispositiviManager.aggiornaDispositivo(new DispositivoPOJO(d));
					}
				}
			}
		}
		
		else if(type.equals(Message.ONLINE.toString()) || type.equals(Message.OFFLINE.toString())) {
			
			// Se lo stato del sender è diverso da quello registrato viene aggiornato
			if(!sender.getState().toString().equals(type)) {
				
				// Cambio lo stato del dispositivo nella lista a seconda del type
				searchDispositivoByName(request.getTopic().getId()).setState(
									type.equals(Message.ONLINE.toString()) ? 
									DispositivoStates.ALIVE : DispositivoStates.DISABLED);
			
				// Aggiorno il dispositivo nel database
				dispositiviManager.aggiornaDispositivo(new DispositivoPOJO(sender));
			}
		}
		
		else return Message.ERROR;
		
		return Message.ACK;
		
	}
}

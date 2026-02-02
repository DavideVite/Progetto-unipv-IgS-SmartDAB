package it.unipv.posfw.smartdab.infrastructure.messaging;

import java.util.ArrayList;
import java.util.Iterator;

import it.unipv.posfw.smartdab.adapter.facade.AttuatoreFacade;
import it.unipv.posfw.smartdab.core.beans.DispositivoPOJO;
import it.unipv.posfw.smartdab.core.domain.enums.DispositivoStates;
import it.unipv.posfw.smartdab.core.domain.enums.Message;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.Dispositivo;
import it.unipv.posfw.smartdab.core.port.messaging.IEventBusClient;
import it.unipv.posfw.smartdab.core.service.DispositiviManager;
import it.unipv.posfw.smartdab.infrastructure.messaging.request.Request;

public class EventBus implements DispositiviObserver, IEventBusClient {
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

	// Aggiunge solo in memoria, senza persistere nel DB (usato al boot dal DispositivoLoader)
	public boolean addDispositivoInMemory(Dispositivo d) {
		if(dispositivi.add(d)) {
			return true;
		}
		System.out.println("Il dispositivo " + d.getTopic().getId() + " è già presente nella lista");
		return false;
	}
	
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
			   request.getTopic().getId().equals(d.getTopic().getId()) &&
			   
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
	
	public Message sendRequest(Request request) {
		Dispositivo target = searchDispositivoByName(request.getTopic().getId());
		if (target == null) return Message.ERROR;
		return target.getCommunicator().processRequest(request);
	}
	
	private EventBus(DispositiviManager dm) {
		dispositiviManager = dm;
	}
	
	// Singleton
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
	
	@Override
	public Message update(Request request) {
		
		String type = request.getType();
		
		// Verifico se il messaggio arrivato è pertinente alla funzionalità di un sensore
		if(type.equals(Message.UPDATE + "." + Message.PARAMETER)) {
			setRequest(request);

			// Prendi tutti gli iscritti al topic e itera
			Iterator<Dispositivo> iterSubs = getSubscribers().iterator();
			Dispositivo d;

			while(iterSubs.hasNext()) {
				d = iterSubs.next();
				
				// Manda richiesta
				for(int i = 0; i < 10; i++) {
					
					// Se la richiesta va a buon fine
					if(sendRequest(request).equals(Message.ACK)) {

						// Se un dispositivo in stato UNKNOWN risponde, allora è ALIVE
						if(d.getState().toString().equals(DispositivoStates.UNKNOWN.toString())) {
							d.setState(DispositivoStates.ALIVE);
						}
						break;
					}

					// Se il dispositivo non risponde a 10 chiamate allora vado al prossimo
					else if(i == 9) {
						System.out.println("Dispositivo " + request.getTopic().getId() + " non ha risposto");
						// Il suo stato attuale è ignoto data l'assenza di risposta
						d.setState(DispositivoStates.UNKNOWN);
					}
				}
			}
		}
		
		else if(type.equals(Message.ONLINE.toString()) || type.equals(Message.OFFLINE.toString())) {
			searchDispositivoByName(request.getTopic().getId()).setState(
									type.equals(Message.ONLINE.toString()) ? 
									DispositivoStates.ALIVE : DispositivoStates.DISABLED);
		}
		
		return Message.ACK;
		
	}
}

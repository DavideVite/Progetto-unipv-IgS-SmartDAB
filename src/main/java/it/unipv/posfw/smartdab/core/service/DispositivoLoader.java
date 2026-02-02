package it.unipv.posfw.smartdab.core.service;

import java.util.List;

import it.unipv.posfw.smartdab.adapter.facade.AttuatoreFacade;
import it.unipv.posfw.smartdab.core.beans.DispositivoPOJO;
import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.model.casa.Stanza;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.Dispositivo;
import it.unipv.posfw.smartdab.factory.DispositivoFactory;
import it.unipv.posfw.smartdab.infrastructure.messaging.EventBus;

/**
 * Orchestratore che collega il mondo POJO (database) al mondo dominio (EventBus + Stanze).
 *
 * Per ogni DispositivoPOJO:
 * 1. Trova la Stanza corrispondente
 * 2. Crea il Dispositivo di dominio tramite DispositivoFactory
 * 3. Aggiunge il Dispositivo alla Stanza
 * 4. Registra il Dispositivo nell'EventBus (solo in memoria, senza ripersistere nel DB)
 * 5. Per gli attuatori: registra la Stanza come observer dell'ObservableParameter
 */
public class DispositivoLoader {

	private final GestoreStanze gestoreStanze;
	private final EventBus eventBus;

	public DispositivoLoader(GestoreStanze gestoreStanze, EventBus eventBus) {
		this.gestoreStanze = gestoreStanze;
		this.eventBus = eventBus;
	}

	/**
	 * Carica tutti i dispositivi POJO come oggetti dominio, collegandoli a Stanze e EventBus.
	 * Usato al boot dell'applicazione.
	 */
	public void caricaTutti(List<DispositivoPOJO> pojos) {
		if (pojos == null) return;

		int caricati = 0;
		int errori = 0;

		for (DispositivoPOJO pojo : pojos) {
			if (caricaSingolo(pojo)) {
				caricati++;
			} else {
				errori++;
			}
		}

		System.out.println("DispositivoLoader: caricati " + caricati + " dispositivi di dominio"
				+ (errori > 0 ? " (" + errori + " errori)" : ""));
	}

	/**
	 * Carica un singolo dispositivo POJO come oggetto dominio.
	 * Usato sia al boot che quando si crea un nuovo dispositivo via UI.
	 *
	 * @return true se il caricamento e' andato a buon fine
	 */
	public boolean caricaSingolo(DispositivoPOJO pojo) {
		if (pojo == null) return false;

		// 1. Trova la Stanza
		Stanza stanza = gestoreStanze.cercaStanzaPerId(pojo.getStanza());
		if (stanza == null) {
			System.out.println("DispositivoLoader: stanza " + pojo.getStanza()
					+ " non trovata per dispositivo " + pojo.getId());
			return false;
		}

		// 2. Crea il Dispositivo di dominio
		Dispositivo dispositivo = DispositivoFactory.fromPOJO(pojo, stanza);
		if (dispositivo == null) {
			System.out.println("DispositivoLoader: impossibile creare dominio per " + pojo.getId());
			return false;
		}

		// 3. Aggiungi alla Stanza
		stanza.addDispositivo(dispositivo);

		// 4. Registra nell'EventBus (solo in memoria, il POJO e' gia' nel DB)
		eventBus.addDispositivoInMemory(dispositivo);

		// 5. Per gli attuatori: registra la Stanza come observer dell'ObservableParameter
		//    e inizializza il parametro nella mappa della Stanza se non esiste ancora
		if (dispositivo instanceof AttuatoreFacade) {
			AttuatoreFacade attuatore = (AttuatoreFacade) dispositivo;
			attuatore.getParameter().addObserver(stanza);

			DispositivoParameter param = pojo.getParametro();
			if (param != null && stanza.getParametri().get(param.name()) == null) {
				double valoreIniziale = param.getMin() != null ? param.getMin() : 0.0;
				stanza.getParametri().put(param.name(), valoreIniziale);
				stanza.getParametriTarget().put(param.name(), valoreIniziale);
			}
		}

		// 6. Registra l'EventBus come observer del communicator del dispositivo
		dispositivo.getCommunicator().addObserver(eventBus);

		System.out.println("DispositivoLoader: caricato " + pojo.getId()
				+ " (" + pojo.getTipo() + "/" + pojo.getParametro() + ") nella stanza " + stanza.getNome());

		return true;
	}
}

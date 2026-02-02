package it.unipv.posfw.smartdab.factory;

import it.unipv.posfw.smartdab.adapter.facade.GenericAttuatore;
import it.unipv.posfw.smartdab.adapter.facade.GenericSensore;
import it.unipv.posfw.smartdab.core.beans.DispositivoPOJO;
import it.unipv.posfw.smartdab.core.domain.enums.DispositivoParameter;
import it.unipv.posfw.smartdab.core.domain.model.casa.Stanza;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.Dispositivo;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.lampadaON_OFF.Lampada_Communicator;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.lampadaON_OFF.Lampada_ON_OFF;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.pompa_di_calore.PompaDiCalore;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.attuatori.pompa_di_calore.PompaDiCalore_Communicator;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.sensori.RilevatoreFumo.RilevatoreFumo;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.sensori.Termometro.Termometro;
import it.unipv.posfw.smartdab.core.domain.model.parametro.ObservableParameter;
import it.unipv.posfw.smartdab.core.port.communication.StandardCommunicator;
import it.unipv.posfw.smartdab.infrastructure.messaging.topic.Topic;

/**
 * Factory che converte DispositivoPOJO (dati dal DB) in Dispositivo (oggetti dominio).
 *
 * Mapping esplicito per i tipi noti, fallback generico per gli altri.
 */
public class DispositivoFactory {

	/**
	 * Crea un Dispositivo di dominio a partire da un POJO e dalla Stanza di appartenenza.
	 *
	 * @param pojo   Il POJO caricato dal database
	 * @param stanza La stanza (implementa RoomPort, necessaria per il Topic)
	 * @return Il Dispositivo di dominio, oppure null se la creazione fallisce
	 */
	public static Dispositivo fromPOJO(DispositivoPOJO pojo, Stanza stanza) {
		if (pojo == null || stanza == null) return null;

		try {
			Topic topic = Topic.createTopic(pojo.getId(), stanza, pojo.getParametro());
			Dispositivo dispositivo;

			if ("ATTUATORE".equals(pojo.getTipo())) {
				dispositivo = creaAttuatore(pojo, topic);
			} else if ("SENSORE".equals(pojo.getTipo())) {
				dispositivo = creaSensore(pojo, topic);
			} else {
				System.out.println("Tipo dispositivo sconosciuto: " + pojo.getTipo() + " per " + pojo.getId());
				return null;
			}

			// Ripristina stato dal DB
			if (pojo.getStato() != null) {
				dispositivo.setState(pojo.getStato());
			}
			// Ripristina attivo/spento
			if (pojo.isAttivo() != dispositivo.isActive()) {
				dispositivo.switchDispositivo();
			}

			return dispositivo;

		} catch (IllegalArgumentException e) {
			System.out.println("Errore creazione Topic per dispositivo " + pojo.getId() + ": " + e.getMessage());
			return null;
		}
	}

	private static Dispositivo creaAttuatore(DispositivoPOJO pojo, Topic topic) {
		DispositivoParameter param = pojo.getParametro();
		ObservableParameter obsParam = new ObservableParameter(param);

		// Mapping esplicito per tipi concreti noti
		switch (param) {
			case TEMPERATURA: {
				PompaDiCalore_Communicator comm = new PompaDiCalore_Communicator();
				return new PompaDiCalore(topic, comm, obsParam);
			}
			case LUMINOSITA: {
				Lampada_Communicator comm = new Lampada_Communicator();
				return new Lampada_ON_OFF(topic, comm, obsParam, 3000);
			}
			default: {
				// Fallback generico
				StandardCommunicator comm = new StandardCommunicator();
				GenericAttuatore attuatore = new GenericAttuatore(topic, comm, obsParam);
				comm.setDispositivo(attuatore);
				return attuatore;
			}
		}
	}

	private static Dispositivo creaSensore(DispositivoPOJO pojo, Topic topic) {
		DispositivoParameter param = pojo.getParametro();

		// Mapping esplicito per tipi concreti noti
		switch (param) {
			case TEMPERATURA: {
				StandardCommunicator comm = new StandardCommunicator();
				return new Termometro(topic, comm);
			}
			case FUMO: {
				StandardCommunicator comm = new StandardCommunicator();
				return new RilevatoreFumo(topic, comm);
			}
			default: {
				// Fallback generico
				StandardCommunicator comm = new StandardCommunicator();
				GenericSensore sensore = new GenericSensore(topic, comm);
				comm.setDispositivo(sensore);
				return sensore;
			}
		}
	}
}

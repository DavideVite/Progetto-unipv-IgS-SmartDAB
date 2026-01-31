package it.unipv.posfw.smartdab.core.port.communication.observer;

import it.unipv.posfw.smartdab.core.domain.model.casa.Stanza;

/**
 * Listener per eventi relativi alle stanze.
 * Permette ai componenti interessati di reagire alla creazione o rimozione di stanze.
 */
public interface StanzaListener {

	void onStanzaAggiunta(Stanza stanza);

	void onStanzaRimossa(Stanza stanza);
}

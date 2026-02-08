package it.unipv.posfw.smartdab.core.port.room;

import java.util.Set;

import it.unipv.posfw.smartdab.core.domain.model.casa.Stanza;

/**
 * Port per ottenere l'elenco delle stanze.
 * Definita nel core per permettere la Dependency Inversion:
 * il core dichiara di cosa ha bisogno, l'infrastruttura lo fornisce.
 */
public interface IStanzaProvider {
	Set<Stanza> readAllStanze();
}

package it.unipv.posfw.smartdab.core.service;

import java.util.HashMap;
import java.util.Map;

import it.unipv.posfw.smartdab.core.domain.enums.DispositivoStates;
import it.unipv.posfw.smartdab.core.domain.model.dispositivo.Dispositivo;
import it.unipv.posfw.smartdab.strategy.MalfunzionamentoStrategy;
import it.unipv.posfw.smartdab.strategy.StrategiaCritica;

public class GestoreMalfunzionamenti {
	
	private Map<Dispositivo, Integer> tentativiFalliti = new HashMap<>();
	private MalfunzionamentoStrategy strategia;
	
	public GestoreMalfunzionamenti(MalfunzionamentoStrategy strategia) {
		this.strategia = strategia;
		
	}
	
	public void controllaConnessione(Dispositivo d, Object value) {
		if (value == null) {
	     
		// Se è già disabilitato, non facciamo nulla finché non torna un valore valido
	    if (d.getState() == DispositivoStates.DISABLED) return;
	    
			//cerca d nella mappa, se lo trova restituisce il numero di fallimenti salvati precedentemente
			//se non lo trova restituisce 0
			int conteggio = tentativiFalliti.getOrDefault(d,0) + 1;   
			tentativiFalliti.put(d, conteggio);
			
			//scelta della strategia
			//posso usare una strategia diversa se il parametro è critico
			MalfunzionamentoStrategy strategyDaUsare = strategia;
			
			if(d.getTopic().toString().contains("gas") || d.getTopic().toString().contains("fumo") || d.getTopic().toString().contains("manomissione")) {
				strategyDaUsare = new StrategiaCritica();
			}
			
			//uso la strategia
			if(strategyDaUsare.deveDisabilitare(d, value, conteggio)) {
				disabilitaDispositivo(d);
			}
		} else {
			//resetta il contatore a 0 perché il dispositivo ha risposto correttamente
			tentativiFalliti.put(d,0);
			
			//se il dispositivo era DISABLED viene portato allo stato ALIVE
			if(d.getState() == DispositivoStates.DISABLED) {
				d.setState(DispositivoStates.ALIVE);
			}
		}
	}
	
	private void disabilitaDispositivo(Dispositivo d) {
		//cambia lo stato in DISABLED
		d.setState(DispositivoStates.DISABLED);
		
		//se il dispostivo risulta accesso lo spegne
		if(d.isActive()) {
			d.switchDispositivo();
		}
	}

}

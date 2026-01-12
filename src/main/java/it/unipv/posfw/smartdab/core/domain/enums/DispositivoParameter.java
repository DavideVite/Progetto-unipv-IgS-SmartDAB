package main.java.it.unipv.posfw.smartdab.core.domain.enums;

// Se un produttore non trova un parametro conforme al proprio dispositivo, può inserirlo in coda 
// a quelli presenti

public enum DispositivoParameters {
	TEMPERATURA, UMIDITA, PRESSIONE, AQI, CO2, VOC, PM2_5,PM10, LUMINOSITA, RUMORE, INDICE_UV,
	CONTATTO_PORTA, LIVELLO, VIBRAZIONE, FUMO, GAS, TEMPO, MANOMISSIONE, ELETTRICITA, TENSIONE, CORRENTE, 
	POTENZA, FOTOVOLTAICO, STATO_BATTERIA, CONSUMO_ACQUA, CONSUMO_GAS, PIOGGIA, FLUSSO, 
	TEMPERATURA_FLUIDO, QUALITA_ARIA, VENTILAZIONE, SENSORE_PRESENZA, SENSORE_MOVIMENTO, LUCE_RGB,
	UNKNOWN
}

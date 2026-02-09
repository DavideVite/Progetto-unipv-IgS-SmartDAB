package it.unipv.posfw.smartdab.core.domain.enums;

/**
 * Questa classe è una enum che contiene tutti i possibili messaggi che dei dispositivi possono mandare
 * e ricevere all'interno del sistema SmartDAB
 * @author Alessandro Ingenito
 * @version 1.0
 */

public enum Message {
	ACK, ONLINE, OFFLINE, CONFIG, DISABLE, ENABLE, SETPOINT, UPDATE, STATE, ERROR, SWITCH, SET,
	PARAMETER, ACT
}

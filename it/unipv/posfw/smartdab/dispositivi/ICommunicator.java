package it.unipv.posfw.smartdab.dispositivi;

public interface ICommunicator {
	final String format = "";
	
	public boolean sendPayload(String payload);
	public Message processRequest(String request);
	public boolean changeState(DispositivoStates state);
}

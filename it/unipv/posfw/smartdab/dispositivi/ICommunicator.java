package it.unipv.posfw.smartdab.dispositivi;

public interface ICommunicator {
	
	// es. home/stanza1/disp1/param1/val
	final String format = "home/%s/%s/%s/%s";
	
	public boolean sendPayload(String payload);
	public Message processRequest(String request);
	public boolean changeState(DispositivoStates state);
}

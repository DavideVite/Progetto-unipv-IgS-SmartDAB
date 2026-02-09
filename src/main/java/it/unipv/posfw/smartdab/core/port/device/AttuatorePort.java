package it.unipv.posfw.smartdab.core.port.device;

/**
 * Interfaccia tecnica per gli attuatori dotati di setpoint
 * @author Alessandro Ingenito
 * @version 1.0
 */

public interface AttuatorePort extends DevicePort {
	public void changeSetpoint(double setpoint);
}

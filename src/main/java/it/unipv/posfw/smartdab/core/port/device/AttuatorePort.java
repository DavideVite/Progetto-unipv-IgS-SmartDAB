package it.unipv.posfw.smartdab.core.port.device;

public interface AttuatorePort extends DevicePort {
	public void changeSetpoint(double setpoint);
}

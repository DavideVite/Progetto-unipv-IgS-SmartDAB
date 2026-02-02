package it.unipv.posfw.smartdab.core.service.strategy;

@FunctionalInterface
public interface ParameterSyncFunction {
    double apply(double currentValue, double targetValue);
}

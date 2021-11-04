package org.fiware.impress.model;

public record MachineInfo(Integer availableMachines, Integer machinesInUse, Double averageUsage, Double averageAvailability) {
}

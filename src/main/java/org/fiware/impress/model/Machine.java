package org.fiware.impress.model;

import java.time.LocalDate;
import java.util.Map;

public record Machine(String id, String type, String model, boolean inUse, LocalDate lastMaintenance, LocalDate nextMaintenance, Double averageUsage, Double averageAvailability, String currentCustomer, Map<String, Object> generalInfo, String healthState, EnergyInformation energyInformation) {
}

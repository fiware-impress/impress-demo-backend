package org.fiware.impress.model;

import java.time.LocalDate;

public record UsageInformation(Double averageUsage, Double averageAvailability, String currentCustomer, LocalDate lastMainenance, LocalDate nextMaintenance) {
}

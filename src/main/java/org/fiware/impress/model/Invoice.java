package org.fiware.impress.model;

import java.time.Instant;

public record Invoice(String invoiceId, String customerId, String machineId, Instant creationDate, Number amount, Number discount) {
}

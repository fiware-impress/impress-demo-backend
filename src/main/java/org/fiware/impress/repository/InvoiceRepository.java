package org.fiware.impress.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fiware.contract.api.InvoiceApiClient;
import org.fiware.impress.mapping.EntityMapper;
import org.fiware.impress.model.Invoice;

import javax.inject.Singleton;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Singleton
@RequiredArgsConstructor
public class InvoiceRepository {

	private final InvoiceApiClient invoiceApiClient;
	private final EntityMapper entityMapper;
	private final ServiceInfoRepository serviceInfoRepository;

	public Optional<Invoice> getInvoice(URI invoiceId) {
		return invoiceApiClient.getInvoiceById(invoiceId.toString()).map(invoiceVO -> entityMapper.invoiceVOToInvoice(invoiceVO, serviceInfoRepository));
	}

	public List<Invoice> getInvoices() {
		return invoiceApiClient.getInvoices()
				.orElse(List.of())
				.stream()
				.map(invoiceVO -> entityMapper.invoiceVOToInvoice(invoiceVO, serviceInfoRepository))
				.collect(Collectors.toList());
	}
}

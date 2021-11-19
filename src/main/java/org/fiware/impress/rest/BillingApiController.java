package org.fiware.impress.rest;

import io.micronaut.http.annotation.Controller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fiware.baas.api.BillingApi;
import org.fiware.baas.model.AddressVO;
import org.fiware.baas.model.InvoiceListVO;
import org.fiware.baas.model.InvoiceOverviewVO;
import org.fiware.baas.model.InvoiceVO;
import org.fiware.baas.model.LegalPersonVO;
import org.fiware.impress.mapping.EntityMapper;
import org.fiware.impress.model.Invoice;
import org.fiware.impress.model.Organization;
import org.fiware.impress.repository.InvoiceRepository;
import org.fiware.impress.repository.OrganizationRepository;
import org.fiware.impress.repository.SmartServiceRepository;
import org.graalvm.compiler.nodes.calc.IntegerDivRemNode;

import java.net.URI;
import java.rmi.dgc.Lease;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of the BaaS BillingApi.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class BillingApiController implements BillingApi {

	private final InvoiceRepository invoiceRepository;
	private final OrganizationRepository organizationRepository;
	private final EntityMapper entityMapper;

	@Override
	public Optional<InvoiceVO> getInvoiceById(String id) {
		return invoiceRepository.getInvoice(URI.create(id)).map(i -> entityMapper.invoiceToInvoiceVO(i, organizationRepository));
	}

	@Override
	public InvoiceListVO getInvoices(Optional<Integer> pageSize, Optional<String> pageAnchor) {
		List<InvoiceOverviewVO> invoiceOverviews = invoiceRepository
				.getInvoices()
				.stream()
				.map(i -> entityMapper.invoiceToInvoiceVO(i, organizationRepository))
				.map(InvoiceVO::getOverview)
				.collect(Collectors.toList());

		return new InvoiceListVO()
				.pageSize(invoiceOverviews.size())
				.pageAnchor(invoiceOverviews
						.stream()
						.findFirst()
						.map(InvoiceOverviewVO::invoiceId)
						.orElse(""))
				.invoices(invoiceOverviews)
				.total(invoiceOverviews.size());
	}
}

package org.fiware.impress.mapping;

import org.fiware.baas.model.AddressVO;
import org.fiware.baas.model.GeneralMachineInformationVO;
import org.fiware.baas.model.InvoiceOverviewVO;
import org.fiware.baas.model.LegalPersonVO;
import org.fiware.baas.model.MachineInfoVO;
import org.fiware.baas.model.MachineVO;
import org.fiware.baas.model.MaintenanceVO;
import org.fiware.baas.model.UsageInformationVO;
import org.fiware.broker.model.EntityVO;
import org.fiware.contract.model.InvoiceVO;
import org.fiware.contract.model.MeasurementPointVO;
import org.fiware.contract.model.OrderVO;
import org.fiware.contract.model.OrganizationVO;
import org.fiware.contract.model.PriceDefinitionVO;
import org.fiware.contract.model.ProviderVO;
import org.fiware.contract.model.SmartServiceVO;
import org.fiware.impress.model.Invoice;
import org.fiware.impress.model.Machine;
import org.fiware.impress.model.MachineInfo;
import org.fiware.impress.model.Organization;
import org.fiware.impress.model.SmartService;
import org.fiware.impress.repository.OrganizationRepository;
import org.fiware.impress.repository.ServiceInfoRepository;
import org.mapstruct.Mapper;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Mapper(componentModel = "jsr330")
public interface EntityMapper {

	String DEFAULT_MODEL = "noModelDefined";

	default Invoice invoiceVOToInvoice(InvoiceVO invoiceVO, ServiceInfoRepository serviceInfoRepository) {
		String invoiceId = invoiceVO.getId();
		String customerId = invoiceVO.getCustomer().getId();
		Number amount = invoiceVO.getAmount();
		Instant creationDate = invoiceVO.creationDate().toInstant();
		OrderVO orderVO = invoiceVO.getReferencesOrders().stream().findFirst().get();
		Number discount = orderVO.getDiscount();
		String machineId = invoiceVO.getReferencesOrders().stream()
				.findFirst()
				.map(OrderVO::acceptedOfferId)
				.flatMap(serviceInfoRepository::getMachineIdByOfferId)
				.orElse("");
		return new Invoice(invoiceId, customerId, machineId, creationDate, amount, discount);
	}

	default org.fiware.baas.model.InvoiceVO invoiceToInvoiceVO(Invoice invoice, OrganizationRepository organizationRepository) {
		Organization customer = organizationRepository.getOrganizationById(invoice.customerId()).orElseThrow(() -> new RuntimeException("No such customer exists."));
		AddressVO addressVO = new AddressVO().city(customer.addressLocality()).street(customer.streetAddress()).zipcode(customer.postalCode());
		LegalPersonVO legalPersonVO = new LegalPersonVO().address(addressVO).id(UUID.fromString(customer.id())).name(customer.legalName());

		InvoiceOverviewVO invoiceOverviewVO = new InvoiceOverviewVO()
				.amount(invoice.amount().doubleValue())
				.creationDate(LocalDate.from(invoice.creationDate()))
				.customer(customer.legalName())
				.machineId(invoice.machineId())
				.invoiceId(invoice.invoiceId());
		org.fiware.baas.model.InvoiceVO invoiceVO = new org.fiware.baas.model.InvoiceVO();
		invoiceVO.customer(legalPersonVO);
		invoiceVO.discount(invoice.discount().doubleValue());
		invoiceVO.setOverview(invoiceOverviewVO);
		return invoiceVO;
	}

	default MachineVO machineToMachineVO(Machine machine) {
		MaintenanceVO maintenanceVO = new MaintenanceVO();
		Optional<LocalDate> optionalNextMaintenance = Optional.ofNullable(machine.nextMaintenance());
		Optional<LocalDate> optionalLastMaintenance = Optional.ofNullable(machine.lastMaintenance());

		optionalNextMaintenance.ifPresent(next -> maintenanceVO.next(next));
		optionalLastMaintenance.ifPresent(last -> maintenanceVO.last(last));


		UsageInformationVO usageInformationVO = new UsageInformationVO();
		usageInformationVO.averageUsage(machine.averageUsage())
				.averageAvailability(machine.averageAvailability());
		if (machine.currentCustomer() == null || machine.currentCustomer().isEmpty()) {
			usageInformationVO.currentCustomer(null);
		} else {
			usageInformationVO.currentCustomer(machine.currentCustomer());
		}
		if (optionalNextMaintenance.isPresent() || optionalLastMaintenance.isPresent()) {
			usageInformationVO.maintenanceInfo(maintenanceVO);
		} else {
			usageInformationVO.maintenanceInfo(null);
		}

		GeneralMachineInformationVO generalMachineInformationVO = new GeneralMachineInformationVO();
		generalMachineInformationVO.putAll(machine.generalInfo());

		MachineVO machineVO = new MachineVO();
		machineVO.id(machine.id())
				.model(machine.model())
				.type(machine.type())
				.generalInformation(generalMachineInformationVO)
				.usageInformation(usageInformationVO);
		// TODO: Bookings
		return machineVO;
	}

	default Machine entityVoToMachine(EntityVO entityVO) {

		Map<String, Object> additionalProperties = entityVO.getAdditionalProperties();

		String model = (String) ((Map) additionalProperties.getOrDefault("model", Map.of("value", DEFAULT_MODEL))).get("value");
		boolean inUse = (Boolean) ((Map) additionalProperties.getOrDefault("inUse", Map.of("value", false))).get("value");
		LocalDate lastMaintenance = null;
		LocalDate nextMaintenance = null;
		if (additionalProperties.containsKey("nextMaintenance")) {
			nextMaintenance = (LocalDate) ((Map) additionalProperties.get("nextMaintenance")).get("value");
			additionalProperties.remove("nextMaintenance");
		}
		if (additionalProperties.containsKey("lastMaintenance")) {
			lastMaintenance = (LocalDate) ((Map) additionalProperties.get("lastMaintenance")).get("value");
			additionalProperties.remove("lastMaintenance");
		}
		Double averageUsage = (Double) ((Map) additionalProperties.getOrDefault("averageUsage", Map.of("value", 0d))).get("value");
		Double averageAvailability = (Double) ((Map) additionalProperties.getOrDefault("averageAvailability", Map.of("value", 0d))).get("value");
		String currentCustomer = (String) ((Map) additionalProperties.getOrDefault("currentCustomer", Map.of("value", ""))).get("value");

		additionalProperties.remove("model");
		additionalProperties.remove("inUse");
		additionalProperties.remove("averageUsage");
		additionalProperties.remove("averageAvailability");
		additionalProperties.remove("currentCustomer");

		return new Machine(
				entityVO.id().toString(),
				entityVO.type(),
				model,
				inUse,
				nextMaintenance,
				lastMaintenance,
				averageUsage,
				averageAvailability,
				currentCustomer,
				additionalProperties);
	}

	Organization organizationVOToOrganization(OrganizationVO organizationVO);

	MachineInfoVO machineInfoToMachineInfoVO(MachineInfo machineInfo);

	default SmartService smartServiceVoToSmartService(SmartServiceVO smartServiceVO) {
		String providerMachineId = smartServiceVO.getPriceDefinitions()
				.stream()
				.findFirst()
				.map(PriceDefinitionVO::getMeasurementPoint)
				.map(MeasurementPointVO::provider)
				.map(ProviderVO::getId)
				.orElse("");
		return new SmartService(smartServiceVO.id(), providerMachineId);
	}
}

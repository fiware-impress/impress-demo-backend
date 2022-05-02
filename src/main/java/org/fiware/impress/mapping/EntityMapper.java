package org.fiware.impress.mapping;

import org.fiware.baas.model.AddressVO;
import org.fiware.baas.model.EnergyInformationVO;
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
import org.fiware.impress.model.EnergyInformation;
import org.fiware.impress.model.Invoice;
import org.fiware.impress.model.Machine;
import org.fiware.impress.model.MachineInfo;
import org.fiware.impress.model.Organization;
import org.fiware.impress.model.SmartService;
import org.fiware.impress.repository.OrganizationRepository;
import org.fiware.impress.repository.ServiceInfoRepository;
import org.mapstruct.Mapper;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Mapper(componentModel = "jsr330")
public interface EntityMapper {

	String DEFAULT_MODEL = "noModelDefined";

	default Invoice invoiceVOToInvoice(InvoiceVO invoiceVO, ServiceInfoRepository serviceInfoRepository, String downloadLinkTemplate) {
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
		try {
			URL downloadLink = new URL(String.format(downloadLinkTemplate, invoiceId));
			return new Invoice(invoiceId, customerId, machineId, creationDate, amount, discount, downloadLink);
		} catch (MalformedURLException e) {
			throw new RuntimeException(String.format("Was not able to build downloadlink for %s.", invoiceId), e);
		}
	}

	default org.fiware.baas.model.InvoiceVO invoiceToInvoiceVO(Invoice invoice, OrganizationRepository organizationRepository) {
		Organization customer = organizationRepository.getOrganizationById(invoice.customerId()).orElseThrow(() -> new RuntimeException("No such customer exists."));
		AddressVO addressVO = new AddressVO().city(customer.addressLocality()).street(customer.streetAddress()).zipcode(customer.postalCode());
		LegalPersonVO legalPersonVO = new LegalPersonVO().address(addressVO).id(UUID.fromString(customer.id())).name(customer.legalName());

		InvoiceOverviewVO invoiceOverviewVO = new InvoiceOverviewVO()
				.amount(invoice.amount().doubleValue())
				.creationDate(LocalDate.ofInstant(invoice.creationDate(), ZoneId.of("UTC")))
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
		generalMachineInformationVO.active((Boolean) machine.generalInfo().getOrDefault("active", false));
		generalMachineInformationVO.maxLiftingWeight(((Number) machine.generalInfo().getOrDefault("maxLiftingWeight", 0)).doubleValue());
		generalMachineInformationVO.currentWeight(((Number) machine.generalInfo().getOrDefault("currentWeight", 0)).doubleValue());
		generalMachineInformationVO.maxHookHeight(((Number) machine.generalInfo().getOrDefault("maxHookHeight", 0)).doubleValue());
		generalMachineInformationVO.location((List<Double>) ((Map) ((Map) machine.generalInfo().get("location")).get("value")).get("coordinates"));

		MachineVO machineVO = new MachineVO();
		machineVO.id(machine.id())
				.model(machine.model())
				.type(machine.type())
				.generalInformation(generalMachineInformationVO)
				.usageInformation(usageInformationVO)
				.energyInformation(map(machine.energyInformation()));
		// TODO: Bookings
		return machineVO;
	}

	EnergyInformationVO map(EnergyInformation energyInformation);

	default Machine entityVoToMachine(EntityVO entityVO) {

		Map<String, Object> additionalProperties = entityVO.getAdditionalProperties();

		String model = (String) ((Map) additionalProperties.getOrDefault("model", Map.of("value", DEFAULT_MODEL))).get("value");
		boolean inUse = (Boolean) ((Map) additionalProperties.getOrDefault("inUse", Map.of("value", false))).get("value");
		LocalDate lastMaintenance = null;
		LocalDate nextMaintenance = null;
		if (additionalProperties.containsKey("nextMaintenance")) {
			nextMaintenance = LocalDate.parse((String) ((Map) additionalProperties.get("nextMaintenance")).get("value"));
			additionalProperties.remove("nextMaintenance");
		}
		if (additionalProperties.containsKey("lastMaintenance")) {
			lastMaintenance = LocalDate.parse((String) ((Map) additionalProperties.get("lastMaintenance")).get("value"));
			additionalProperties.remove("lastMaintenance");
		}

		Double currentConsumption = ((Number) ((Map) additionalProperties.getOrDefault("currentConsumption", Map.of("value", 0d))).get("value")).doubleValue();
		Double currentCost = ((Number) ((Map) additionalProperties.getOrDefault("currentCost", Map.of("value", 40.20))).get("value")).doubleValue();
		EnergyInformation energyInformation = new EnergyInformation(currentConsumption, currentCost);
		Double averageUsage = ((Number) ((Map) additionalProperties.getOrDefault("averageUsage", Map.of("value", 0d))).get("value")).doubleValue();
		Double averageAvailability = ((Number) ((Map) additionalProperties.getOrDefault("averageAvailability", Map.of("value", 0d))).get("value")).doubleValue();
		String currentCustomer = (String) ((Map) additionalProperties.getOrDefault("currentCustomer", Map.of("value", ""))).get("value");
		String healthStatus = (String) ((Map) additionalProperties.getOrDefault("healthState", Map.of("value", "UNKNONW"))).get("value");
		additionalProperties.remove("model");
		additionalProperties.remove("inUse");
		additionalProperties.remove("averageUsage");
		additionalProperties.remove("averageAvailability");
		additionalProperties.remove("currentCustomer");
		Map<String, Object> generalInfo = new HashMap<>();
		if (additionalProperties.containsKey("generalInformation")) {
			Set<Map.Entry> entrySet = ((Map) ((Map) additionalProperties.get("generalInformation")).get("value")).entrySet();
			entrySet.stream().forEach(e -> {
				var key = (String) e.getKey();
				var value = ((Map) e.getValue()).get("value");
				generalInfo.put(key, value);
			});
		}

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
				generalInfo,
				healthStatus,
				energyInformation);
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

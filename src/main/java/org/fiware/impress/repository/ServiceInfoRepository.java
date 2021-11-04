package org.fiware.impress.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fiware.contract.api.OfferApiClient;
import org.fiware.contract.api.SmartServiceApiClient;
import org.fiware.contract.model.MeasurementPointVO;
import org.fiware.contract.model.OfferVO;
import org.fiware.contract.model.PriceDefinitionVO;
import org.fiware.contract.model.ProviderVO;

import javax.inject.Singleton;
import java.util.Optional;

@Slf4j
@Singleton
@RequiredArgsConstructor
public class ServiceInfoRepository {

	private final SmartServiceApiClient smartServiceApiClient;
	private final OfferApiClient offerApiClient;

	public Optional<String> getMachineIdByOfferId(String id) {
		return offerApiClient.getOfferById(id)
				.map(OfferVO::getServiceId)
				.flatMap(smartServiceApiClient::getServiceById)
				.flatMap(serviceVO -> serviceVO.getPriceDefinitions().stream().findFirst())
				.map(PriceDefinitionVO::getMeasurementPoint)
				.map(MeasurementPointVO::getProvider)
				.map(ProviderVO::getId);
	}
//	public Optional<SmartService> getServiceByOfferId(String id) {
//		return offerApiClient.getOfferById(id)
//				.map(OfferVO::getServiceId)
//				.flatMap(smartServiceApiClient::getServiceById)
//
//	}
}

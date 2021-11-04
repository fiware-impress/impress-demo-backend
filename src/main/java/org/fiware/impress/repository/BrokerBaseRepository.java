package org.fiware.impress.repository;

import lombok.RequiredArgsConstructor;
import org.fiware.broker.api.EntitiesApiClient;
import org.fiware.impress.configuration.GeneralProperties;
import org.fiware.impress.mapping.EntityMapper;

@RequiredArgsConstructor
public abstract class BrokerBaseRepository {

	protected final GeneralProperties generalProperties;
	protected final EntitiesApiClient entitiesApi;
	protected final EntityMapper entityMapper;

	protected String getLinkHeader() {
		return String.format("<%s>; rel=\"http://www.w3.org/ns/json-ld#context\"; type=\"application/ld+json", generalProperties.getContextUrl());
	}
}

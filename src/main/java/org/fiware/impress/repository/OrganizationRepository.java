package org.fiware.impress.repository;

import com.fasterxml.jackson.annotation.OptBoolean;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fiware.broker.api.EntitiesApiClient;
import org.fiware.contract.api.OrganizationApiClient;
import org.fiware.impress.configuration.GeneralProperties;
import org.fiware.impress.mapping.EntityMapper;
import org.fiware.impress.model.Organization;

import javax.inject.Singleton;
import java.net.URI;
import java.util.Optional;

@Slf4j
@Singleton
@RequiredArgsConstructor
public class OrganizationRepository {

	private final OrganizationApiClient organizationApiClient;
	private final EntityMapper entityMapper;

	public Optional<Organization> getOrganizationById(String id) {
		return organizationApiClient.getOrganizationById(id).map(entityMapper::organizationVOToOrganization);
	}
}

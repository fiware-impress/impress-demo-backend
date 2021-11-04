package org.fiware.impress.rest;

import io.micronaut.http.annotation.Controller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fiware.baas.api.MachinesApi;
import org.fiware.baas.model.MachineInfoVO;
import org.fiware.baas.model.MachineListVO;
import org.fiware.baas.model.MachineVO;
import org.fiware.baas.model.PropertyHistoryVO;
import org.fiware.impress.mapping.EntityMapper;
import org.fiware.impress.repository.MachineRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the BaaS MachinesApi.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class MachinesApiController implements MachinesApi {

	private final MachineRepository machineRepository;
	private final EntityMapper entityMapper;

	@Override
	public MachineInfoVO getMachineInfoByCoordinates(String lat, String longi, Double perimeter) {
		return entityMapper.machineInfoToMachineInfoVO(machineRepository.getMachineInfoByCoordinates(lat, longi, perimeter));
	}

	@Override
	public MachineListVO getMachinesByCoordinates(String lat, String longi, Double perimeter, Integer pageSize, String pageAnchor) {
		List<MachineVO> machines = machineRepository
				.getMachinesByCoordinates(lat, longi, perimeter)
				.stream()
				.map(entityMapper::machineToMachineVO)
				.collect(Collectors.toList());
		return new MachineListVO().machines(machines)
				.pageSize(pageSize)
				.pageAnchor(pageAnchor)
				.total(machines.size());
	}

	@Override
	public MachineVO getMachinesById(String id) {
		return machineRepository.getMachineById(id).map(entityMapper::machineToMachineVO).orElse(null);
	}

	@Override
	public PropertyHistoryVO getMachineMonitoringAtDate(String id, String property, LocalDate dateAt) {
		throw new UnsupportedOperationException("Not implemented yet");
	}
}

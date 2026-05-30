package com.example.bootcamp.domain.usecase;

import com.example.bootcamp.domain.api.BootcampServicePort;
import com.example.bootcamp.domain.constants.Constants;
import com.example.bootcamp.domain.exceptions.BootcampAlreadyExistsException;
import com.example.bootcamp.domain.exceptions.CapacityNotFoundException;
import com.example.bootcamp.domain.model.Bootcamp;
import com.example.bootcamp.domain.model.Capacity;
import com.example.bootcamp.domain.spi.BootcampPersistencePort;
import com.example.bootcamp.domain.spi.CapacityExternalService;
import reactor.core.publisher.Mono;

import java.util.List;

public class BootcampUseCase implements BootcampServicePort {

    private final BootcampPersistencePort bootcampPersistencePort;
    private final CapacityExternalService capacityExternalService;

    public BootcampUseCase(BootcampPersistencePort bootcampPersistencePort, CapacityExternalService capacityExternalService) {
        this.bootcampPersistencePort = bootcampPersistencePort;
        this.capacityExternalService = capacityExternalService;
    }

    @Override
    public Mono<Bootcamp> registerBootcamp(Bootcamp bootcamp) {
        List<Long> ids = bootcamp.capacities().stream()
                .map(Capacity::id)
                .toList();

        return bootcampPersistencePort.existByName(bootcamp.name())
                .filter(exists -> !exists)
                .switchIfEmpty(Mono.error(new BootcampAlreadyExistsException(String.format(Constants.BOOTCAMP_ALREADY_EXISTS, bootcamp.name()))))
                .flatMap(unused -> capacityExternalService.verifyCapacitiesByIds(ids))
                .filter(isValid -> isValid)
                .switchIfEmpty(Mono.error(new CapacityNotFoundException(Constants.CAPACITY_NOT_EXISTS)))
                .flatMap(isValid -> bootcampPersistencePort.save(bootcamp));
    }
}
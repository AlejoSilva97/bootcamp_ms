package com.example.bootcamp.domain.usecase;

import com.example.bootcamp.domain.api.BootcampServicePort;
import com.example.bootcamp.domain.constants.Constants;
import com.example.bootcamp.domain.exceptions.BootcampAlreadyExistsException;
import com.example.bootcamp.domain.exceptions.BootcampNotFoundException;
import com.example.bootcamp.domain.exceptions.CapacityNotFoundException;
import com.example.bootcamp.domain.model.Bootcamp;
import com.example.bootcamp.domain.model.Capacity;
import com.example.bootcamp.domain.model.PaginationParams;
import com.example.bootcamp.domain.spi.BootcampPersistencePort;
import com.example.bootcamp.domain.spi.CapacityExternalService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

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

    @Override
    public Flux<Bootcamp> getAllBootcamps(PaginationParams params) {
        return bootcampPersistencePort.findAll(params)
                .collectList()
                .filter(bootcamps -> !bootcamps.isEmpty())
                .flatMapMany(this::enrichBootcampsWithCapacities);
    }

    private Flux<Bootcamp> enrichBootcampsWithCapacities(List<Bootcamp> bootcamps) {
        List<Long> capacitiesIds = getCapacitiesIds(bootcamps);

        if (capacitiesIds.isEmpty()) {
            return Flux.fromIterable(bootcamps);
        }

        return capacityExternalService.getCapacitiesByIds(capacitiesIds)
                .collectMap(Capacity::id, capacity -> capacity)
                .flatMapMany(capacityMap -> enrichBootcampList(bootcamps, capacityMap));
    }

    private List<Long> getCapacitiesIds(List<Bootcamp> bootcamps) {
        return bootcamps.stream()
                .flatMap(b -> b.capacities().stream())
                .map(Capacity::id)
                .distinct()
                .toList();
    }

    private Flux<Bootcamp> enrichBootcampList(List<Bootcamp> bootcamps, Map<Long, Capacity> capacityMap) {
        return Flux.fromIterable(bootcamps)
                .map(bootcamp -> {
                    List<Capacity> enrichedCapacities = bootcamp.capacities().stream()
                            .map(c -> capacityMap.getOrDefault(c.id(), new Capacity(c.id(), "Unknown", List.of())))
                            .toList();

                    return new Bootcamp(
                            bootcamp.id(),
                            bootcamp.name(),
                            bootcamp.description(),
                            bootcamp.launchDate(),
                            bootcamp.duration(),
                            enrichedCapacities);
                });
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return bootcampPersistencePort.existsById(id)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new BootcampNotFoundException(String.format(Constants.BOOTCAMP_NOT_FOUND, id)));
                    }
                    return bootcampPersistencePort.findOrphanCapacityIds(id).collectList();
                })
                .flatMap(orphanCapacityIds ->
                        bootcampPersistencePort.deleteById(id)
                                .then(Mono.defer(() -> {
                                    if (orphanCapacityIds.isEmpty()) {
                                        return Mono.empty();
                                    }
                                    return capacityExternalService.deleteCapacitiesByIds(orphanCapacityIds);
                                }))
                );
    }
}
package com.example.bootcamp.domain.usecase;

import com.example.bootcamp.domain.api.BootcampServicePort;
import com.example.bootcamp.domain.constants.Constants;
import com.example.bootcamp.domain.exceptions.BootcampAlreadyExistsException;
import com.example.bootcamp.domain.exceptions.BootcampNotFoundException;
import com.example.bootcamp.domain.exceptions.CapacityNotFoundException;
import com.example.bootcamp.domain.model.Bootcamp;
import com.example.bootcamp.domain.model.Capacity;
import com.example.bootcamp.domain.model.PaginationParams;
import com.example.bootcamp.domain.model.Technology;
import com.example.bootcamp.domain.spi.BootcampPersistencePort;
import com.example.bootcamp.domain.spi.CapacityExternalService;
import com.example.bootcamp.domain.spi.ReportExternalService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BootcampUseCase implements BootcampServicePort {

    private static final Logger LOGGER = Logger.getLogger(BootcampUseCase.class.getName());

    private final BootcampPersistencePort bootcampPersistencePort;
    private final CapacityExternalService capacityExternalService;
    private final ReportExternalService reportExternalService;

    public BootcampUseCase(BootcampPersistencePort bootcampPersistencePort,
                           CapacityExternalService capacityExternalService,
                           ReportExternalService reportExternalService) {
        this.bootcampPersistencePort = bootcampPersistencePort;
        this.capacityExternalService = capacityExternalService;
        this.reportExternalService = reportExternalService;
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
                .flatMap(isValid -> bootcampPersistencePort.save(bootcamp))
                .doOnNext(savedBootcamp -> triggerBackgroundReport(savedBootcamp.id()));
    }

    private void triggerBackgroundReport(Long bootcampId) {
        this.getBootcampsByIds(List.of(bootcampId))
                .single()
                .flatMap(enrichedBootcamp -> {
                    int capacitiesCount = enrichedBootcamp.capacities().size();

                    int techsCount = (int) enrichedBootcamp.capacities().stream()
                            .filter(c -> c.techs() != null)
                            .flatMap(c -> c.techs().stream())
                            .map(Technology::id)
                            .distinct()
                            .count();

                    return reportExternalService.sendBootcampReport(enrichedBootcamp, capacitiesCount, techsCount);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(
                        unused -> LOGGER.info("Reporte asíncrono enviado con éxito para el Bootcamp ID: " + bootcampId),
                        error -> LOGGER.log(Level.SEVERE, "Fallo crítico enviando reporte asíncrono para Bootcamp ID: " + bootcampId, error)
                );
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

    @Override
    public Flux<Bootcamp> getBootcampsByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Flux.empty();
        }

        List<Long> uniqueIds = ids.stream().distinct().toList();

        return bootcampPersistencePort.findAllByIds(uniqueIds)
                .collectList()
                .flatMapMany(bootcamps -> {
                    if (bootcamps.size() != uniqueIds.size()) {
                        return Mono.error(new BootcampNotFoundException(Constants.BOOTCAMPS_NOT_FOUND));
                    }
                    return enrichBootcampsWithCapacities(bootcamps);
                });
    }
}
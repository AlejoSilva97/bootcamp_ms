package com.example.bootcamp.infrastructure.adapters.persistenceadapter;

import com.example.bootcamp.domain.model.Bootcamp;
import com.example.bootcamp.domain.spi.BootcampPersistencePort;
import com.example.bootcamp.infrastructure.adapters.persistenceadapter.entity.BootcampCapacityEntity;
import com.example.bootcamp.infrastructure.adapters.persistenceadapter.mapper.BootcampEntityMapper;
import com.example.bootcamp.infrastructure.adapters.persistenceadapter.repository.BootcampRepository;
import com.example.bootcamp.infrastructure.adapters.persistenceadapter.repository.BootcampCapacityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BootcampPersistenceAdapter implements BootcampPersistencePort {

    private final BootcampRepository bootcampRepository;
    private final BootcampCapacityRepository bootcampCapacityRepository;
    private final BootcampEntityMapper bootcampEntityMapper;
    private final DatabaseClient databaseClient;

    @Override
    @Transactional
    public Mono<Bootcamp> save(Bootcamp bootcamp) {
        return bootcampRepository.save(bootcampEntityMapper.toEntity(bootcamp))
                .flatMap(savedEntity -> {
                    List<BootcampCapacityEntity> relations = bootcamp.capacities().stream()
                            .map(capacity -> new BootcampCapacityEntity(null, savedEntity.getId(), capacity.id()))
                            .toList();

                    return bootcampCapacityRepository.saveAll(relations)
                            .then(Mono.just(new Bootcamp(
                                    savedEntity.getId(),
                                    savedEntity.getName(),
                                    savedEntity.getDescription(),
                                    savedEntity.getLaunchDate(),
                                    savedEntity.getDuration(),
                                    bootcamp.capacities()
                            )));
                });
    }

    @Override
    public Mono<Boolean> existByName(String name) {
        return bootcampRepository.findByName(name)
                .map(entity -> true)
                .defaultIfEmpty(false);
    }
}
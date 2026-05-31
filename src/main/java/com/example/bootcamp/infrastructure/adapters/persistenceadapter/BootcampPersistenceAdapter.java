package com.example.bootcamp.infrastructure.adapters.persistenceadapter;

import com.example.bootcamp.domain.model.Bootcamp;
import com.example.bootcamp.domain.model.Capacity;
import com.example.bootcamp.domain.model.PaginationParams;
import com.example.bootcamp.domain.spi.BootcampPersistencePort;
import com.example.bootcamp.infrastructure.adapters.persistenceadapter.constants.DatabaseConstants;
import com.example.bootcamp.infrastructure.adapters.persistenceadapter.entity.BootcampCapacityEntity;
import com.example.bootcamp.infrastructure.adapters.persistenceadapter.entity.BootcampEntity;
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
                            .then(Mono.just(bootcampEntityMapper.toModel(savedEntity, bootcamp.capacities())));
                });
    }

    @Override
    public Mono<Boolean> existByName(String name) {
        return bootcampRepository.findByName(name)
                .map(entity -> true)
                .defaultIfEmpty(false);
    }

    public Flux<Bootcamp> findAll(PaginationParams params) {
        final String query = buildQuery(params);

        return databaseClient.sql(query)
                .bind(DatabaseConstants.SIZE, params.size())
                .bind(DatabaseConstants.OFFSET, (long) params.page() * params.size())
                .map(bootcampEntityMapper::rowToEntity)
                .all()
                .collectList()
                .filter(entities -> !entities.isEmpty())
                .flatMapMany(this::enrichEntitiesWithRelations)
                .switchIfEmpty(Flux.empty());
    }

    private static String buildQuery(PaginationParams params) {
        String sortColumn = DatabaseConstants.SORT_BY_CAPACITIES.equalsIgnoreCase(params.sortBy())
                ? DatabaseConstants.SORT_BY_COUNT
                : DatabaseConstants.SORT_BY_NAME;

        String sortOrder = DatabaseConstants.ASC.equalsIgnoreCase(params.direction())
                ? DatabaseConstants.ASC
                : DatabaseConstants.DESC;

        return String.format(DatabaseConstants.QUERY, sortColumn, sortOrder);
    }

    private Flux<Bootcamp> enrichEntitiesWithRelations(List<BootcampEntity> entities) {
        List<Long> bootcampIds = entities.stream().map(BootcampEntity::getId).toList();

        return bootcampCapacityRepository.findByIdBootcampIn(bootcampIds)
                .collectList()
                .flatMapMany(relations -> mapEntitiesToDomain(entities, relations));
    }

    private Flux<Bootcamp> mapEntitiesToDomain(List<BootcampEntity> entities, List<BootcampCapacityEntity> relations) {
        return Flux.fromIterable(entities)
                .map(entity -> {
                    List<Capacity> capacities = relations.stream()
                            .filter(r -> r.getIdBootcamp().equals(entity.getId()))
                            .map(r -> new Capacity(r.getIdCapacity(), null, List.of()))
                            .toList();

                    return bootcampEntityMapper.toModel(entity, capacities);
                });
    }
}
package com.example.bootcamp.infrastructure.adapters.persistenceadapter.repository;

import com.example.bootcamp.infrastructure.adapters.persistenceadapter.entity.BootcampCapacityEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface BootcampCapacityRepository extends ReactiveCrudRepository<BootcampCapacityEntity, Long> {
    Flux<BootcampCapacityEntity> findByIdBootcampIn(List<Long> bootcampIds);
    Mono<Void> deleteByIdBootcamp(Long idBootcamp);

    @Query(
            "SELECT id_capacity FROM bootcamp_capacity " +
                    "WHERE id_bootcamp = :bootcampId " +
                    "AND id_capacity NOT IN (" +
                    "    SELECT id_capacity FROM bootcamp_capacity WHERE id_bootcamp != :bootcampId" +
                    ")"
    )
    Flux<Long> findOrphanCapacityIds(Long bootcampId);
}
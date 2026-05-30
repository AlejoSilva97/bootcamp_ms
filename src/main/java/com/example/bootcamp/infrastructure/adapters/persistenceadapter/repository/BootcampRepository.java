package com.example.bootcamp.infrastructure.adapters.persistenceadapter.repository;

import com.example.bootcamp.infrastructure.adapters.persistenceadapter.entity.BootcampEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;


@Repository
public interface BootcampRepository extends ReactiveCrudRepository<BootcampEntity, Long> {
    Mono<BootcampEntity> findByName(String name);
}

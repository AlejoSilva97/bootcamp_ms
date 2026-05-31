package com.example.bootcamp.domain.spi;

import com.example.bootcamp.domain.model.Bootcamp;
import com.example.bootcamp.domain.model.PaginationParams;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface BootcampPersistencePort {
    Mono<Bootcamp> save(Bootcamp bootcamp);
    Mono<Boolean> existByName(String name);
    Flux<Bootcamp> findAll(PaginationParams params);
    Mono<Void> deleteById(Long id);
    Flux<Long> findOrphanCapacityIds(Long bootcampId);
    Mono<Boolean> existsById(Long id);
    Flux<Bootcamp> findAllByIds(List<Long> ids);
}

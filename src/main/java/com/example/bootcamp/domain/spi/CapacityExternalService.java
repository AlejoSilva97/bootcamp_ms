package com.example.bootcamp.domain.spi;

import com.example.bootcamp.domain.model.Capacity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CapacityExternalService {
    Mono<Boolean> verifyCapacitiesByIds(List<Long> ids);
    Flux<Capacity> getCapacitiesByIds(List<Long> ids);
}

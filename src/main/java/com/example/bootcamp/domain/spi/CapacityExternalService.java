package com.example.bootcamp.domain.spi;

import reactor.core.publisher.Mono;

import java.util.List;

public interface CapacityExternalService {
    Mono<Boolean> verifyCapacitiesByIds(List<Long> ids);
}

package com.example.bootcamp.domain.api;

import com.example.bootcamp.domain.model.Bootcamp;
import com.example.bootcamp.domain.model.PaginationParams;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BootcampServicePort {
    Mono<Bootcamp> registerBootcamp(Bootcamp bootcamp);
    Flux<Bootcamp> getAllBootcamps(PaginationParams params);
}

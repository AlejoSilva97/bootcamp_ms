package com.example.bootcamp.domain.spi;

import com.example.bootcamp.domain.model.Bootcamp;
import reactor.core.publisher.Mono;

public interface ReportExternalService {
    Mono<Void> sendBootcampReport(Bootcamp enrichedBootcamp, int capacitiesCount, int techsCount);
}
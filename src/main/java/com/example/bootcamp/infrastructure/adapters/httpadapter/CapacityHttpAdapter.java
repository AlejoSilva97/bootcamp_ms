package com.example.bootcamp.infrastructure.adapters.httpadapter;

import com.example.bootcamp.domain.spi.CapacityExternalService;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CapacityHttpAdapter implements CapacityExternalService {

    private final WebClient capacityWebClient;

    public CapacityHttpAdapter(WebClient capacityWebClient) {
        this.capacityWebClient = capacityWebClient;
    }

    @Override
    public Mono<Boolean> verifyCapacitiesByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Mono.just(false);
        }

        String idsParam = ids.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        return capacityWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/capacities/exists")
                        .queryParam("ids", idsParam)
                        .build())
                .retrieve()
                .bodyToMono(Boolean.class);
    }
}
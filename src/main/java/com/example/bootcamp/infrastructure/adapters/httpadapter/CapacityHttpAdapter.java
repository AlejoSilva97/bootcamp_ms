package com.example.bootcamp.infrastructure.adapters.httpadapter;

import com.example.bootcamp.domain.constants.Constants;
import com.example.bootcamp.domain.model.Capacity;
import com.example.bootcamp.domain.model.Technology;
import com.example.bootcamp.domain.spi.CapacityExternalService;
import com.example.bootcamp.infrastructure.adapters.httpadapter.dto.ExternalCapacityDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
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

    @Override
    public Flux<Capacity> getCapacitiesByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Flux.empty();
        }

        String idsParam = ids.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        return capacityWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/capacities/bulk")
                        .queryParam("ids", idsParam)
                        .build())
                .retrieve()
                .bodyToFlux(ExternalCapacityDTO.class)
                .map(this::mapToDomain);
    }

    private Capacity mapToDomain(ExternalCapacityDTO dto) {
        List<Technology> domainTechnologies = dto.technologies() == null
                ? List.of()
                : dto.technologies().stream()
                .map(techDto -> new Technology(techDto.id(), techDto.name()))
                .toList();

        return new Capacity(
                dto.id(),
                dto.name(),
                domainTechnologies
        );
    }

    @Override
    public Mono<Void> deleteCapacitiesByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Mono.empty();
        }

        String idsParam = ids.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        return capacityWebClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/capacities")
                        .queryParam("ids", idsParam)
                        .build())
                .retrieve()
                .toBodilessEntity()
                .doOnSuccess(unused -> log.info(Constants.SUCCESS_REQUESTED_DELETION, idsParam))
                .onErrorResume(e -> {
                    log.warn(Constants.CAPACITIES_COULD_NOT_BE_DELETED, e.getMessage());
                    return Mono.empty();
                })
                .then();
    }
}
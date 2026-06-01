package com.example.bootcamp.infrastructure.adapters.httpadapter;

import com.example.bootcamp.domain.model.Bootcamp;
import com.example.bootcamp.domain.spi.ReportExternalService;
import com.example.bootcamp.infrastructure.adapters.httpadapter.dto.ReportRequestDTO;
import com.example.bootcamp.infrastructure.adapters.httpadapter.mapper.ReportExternalMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReportHttpAdapter implements ReportExternalService {

    private final WebClient reportWebClient;
    private final ReportExternalMapper reportExternalMapper;

    @Override
    public Mono<Void> sendBootcampReport(Bootcamp enrichedBootcamp, int capacitiesCount, int techsCount) {
        ReportRequestDTO requestDTO = reportExternalMapper.toRequestDTO(enrichedBootcamp, capacitiesCount, techsCount);

        return reportWebClient.post()
                .uri("/reports")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(requestDTO)
                .retrieve()
                .toBodilessEntity()
                .doOnSuccess(unused -> log.info("Async metrics report sent successfully to report_ms for bootcamp: {}", enrichedBootcamp.id()))
                .doOnError(error -> log.error("Failed to route metrics report to report_ms for bootcamp: {}", enrichedBootcamp.id(), error))
                .then();
    }
}
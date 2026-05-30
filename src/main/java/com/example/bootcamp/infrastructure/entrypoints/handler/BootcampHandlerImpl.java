package com.example.bootcamp.infrastructure.entrypoints.handler;

import com.example.bootcamp.domain.api.BootcampServicePort;
import com.example.bootcamp.domain.constants.Constants;
import com.example.bootcamp.domain.model.Bootcamp;
import com.example.bootcamp.domain.model.Capacity;
import com.example.bootcamp.infrastructure.entrypoints.dto.BootcampRequestDTO;
import com.example.bootcamp.infrastructure.entrypoints.mapper.BootcampMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class BootcampHandlerImpl {

    private final BootcampServicePort bootcampServicePort;
    private final BootcampMapper bootcampMapper;

    public Mono<ServerResponse> createBootcamp(ServerRequest request) {
        return request.bodyToMono(BootcampRequestDTO.class)
                .flatMap(dto -> bootcampServicePort.registerBootcamp(bootcampMapper.toModel(dto))
                        .doOnSuccess(savedBootcamp -> log.info(Constants.BOOTCAMP_CREATED))
                )
                .flatMap(savedBootcamp -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .bodyValue(Constants.BOOTCAMP_CREATED));
    }
}
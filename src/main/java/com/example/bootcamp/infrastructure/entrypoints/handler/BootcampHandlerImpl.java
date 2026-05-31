package com.example.bootcamp.infrastructure.entrypoints.handler;

import com.example.bootcamp.domain.api.BootcampServicePort;
import com.example.bootcamp.domain.constants.Constants;
import com.example.bootcamp.domain.model.PaginationParams;
import com.example.bootcamp.infrastructure.entrypoints.dto.BootcampRequestDTO;
import com.example.bootcamp.infrastructure.entrypoints.mapper.BootcampMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    public Mono<ServerResponse> getAllBootcamps(ServerRequest request) {
        int page = request.queryParam("page").map(Integer::parseInt).orElse(0);
        int size = request.queryParam("size").map(Integer::parseInt).orElse(10);
        String sortBy = request.queryParam("sortBy").orElse("name");
        String direction = request.queryParam("direction").orElse("DESC");

        PaginationParams params = new PaginationParams(page,size, sortBy, direction);

        return bootcampServicePort.getAllBootcamps(params)
                .map(bootcampMapper::toResponseDTO)
                .collectList()
                .flatMap(list -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(list));
    }
}
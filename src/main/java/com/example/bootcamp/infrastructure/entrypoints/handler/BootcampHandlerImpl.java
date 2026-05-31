package com.example.bootcamp.infrastructure.entrypoints.handler;

import com.example.bootcamp.domain.api.BootcampServicePort;
import com.example.bootcamp.domain.constants.Constants;
import com.example.bootcamp.domain.exceptions.InvalidFieldException;
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

import java.util.List;

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

    public Mono<ServerResponse> deleteBootcamp(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        return bootcampServicePort.deleteById(id)
                .then(ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> getBootcampsByIds(ServerRequest request) {
        return extractAndParseIds(request)
                .flatMapMany(bootcampServicePort::getBootcampsByIds)
                .map(bootcampMapper::toResponseDTO)
                .collectList()
                .flatMap(list -> ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(list));
    }

    private Mono<List<Long>> extractAndParseIds(ServerRequest request) {
        return Mono.justOrEmpty(request.queryParams().get("ids"))
                .filter(idsParam -> !idsParam.isEmpty() && !idsParam.get(0).isBlank())
                .switchIfEmpty(Mono.error(new InvalidFieldException(Constants.IDS_PARAMETER_REQUIRED)))
                .map(idsParam -> idsParam.stream()
                        .flatMap(s -> java.util.Arrays.stream(s.split(",")))
                        .map(String::trim)
                        .map(Long::valueOf)
                        .toList())
                .onErrorMap(NumberFormatException.class, e ->
                        new InvalidFieldException(Constants.IDS_PARAMETER_INVALID));
    }
}
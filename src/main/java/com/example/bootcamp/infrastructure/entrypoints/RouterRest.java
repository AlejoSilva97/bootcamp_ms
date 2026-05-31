package com.example.bootcamp.infrastructure.entrypoints;

import com.example.bootcamp.infrastructure.entrypoints.dto.BootcampRequestDTO;
import com.example.bootcamp.infrastructure.entrypoints.dto.BootcampResponseDTO;
import com.example.bootcamp.infrastructure.entrypoints.handler.BootcampHandlerImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/bootcamps",
                    method = RequestMethod.POST,
                    beanClass = BootcampHandlerImpl.class,
                    beanMethod = "createBootcamp",
                    operation = @Operation(
                            summary = "Registrar un nuevo bootcamp",
                            description = "Valida las reglas de negocio del dominio (nombre único, fechas correctas, de 1 a 4 capacidades existentes) y almacena el bootcamp de forma relacional.",
                            operationId = "createBootcamp",
                            requestBody = @RequestBody(
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = BootcampRequestDTO.class))
                            ),
                            responses = {
                                    @ApiResponse(
                                            responseCode = "201",
                                            description = "Bootcamp created successfully",
                                            content = @Content(schema = @Schema(implementation = String.class))
                                    ),
                                    @ApiResponse(responseCode = "400", description = "Campos inválidos o cantidad incorrecta de capacidades asociadas"),
                                    @ApiResponse(responseCode = "404", description = "Una o más capacidades asignadas no existen en el sistema externo"),
                                    @ApiResponse(responseCode = "409", description = "Ya existe un bootcamp registrado con el mismo nombre")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/bootcamps",
                    method = RequestMethod.GET,
                    beanClass = BootcampHandlerImpl.class,
                    beanMethod = "getAllBootcamps",
                    operation = @Operation(
                            summary = "Listar bootcamps con paginación y ordenamiento dinámico",
                            description = "Retorna un flujo paginado de bootcamps enriquecidos con la información completa de sus capacidades y tecnologías. Permite ordenar alfabéticamente por 'name' o de forma cuantitativa por 'capacities'.",
                            operationId = "getAllBootcamps",
                            parameters = {
                                    @Parameter(name = "page", in = ParameterIn.QUERY, description = "Número de la página a consultar (basado en índice 0)", schema = @Schema(type = "integer", defaultValue = "0")),
                                    @Parameter(name = "size", in = ParameterIn.QUERY, description = "Cantidad máxima de registros por página", schema = @Schema(type = "integer", defaultValue = "10")),
                                    @Parameter(name = "sortBy", in = ParameterIn.QUERY, description = "Campo de ordenamiento ('name' o 'capacities')", schema = @Schema(type = "string", defaultValue = "name")),
                                    @Parameter(name = "direction", in = ParameterIn.QUERY, description = "Sentido del ordenamiento ('ASC' o 'DESC')", schema = @Schema(type = "string", defaultValue = "DESC"))
                            },
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Lista de bootcamps paginada, ordenada y enriquecida obtenida con éxito",
                                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = BootcampResponseDTO.class)))
                                    ),
                                    @ApiResponse(responseCode = "400", description = "Parámetros de consulta inválidos o con formato incorrecto")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(BootcampHandlerImpl bootcampHandler) {
        return route(POST("/bootcamps"), bootcampHandler::createBootcamp)
                .andRoute(GET("/bootcamps"), bootcampHandler::getAllBootcamps);
    }
}
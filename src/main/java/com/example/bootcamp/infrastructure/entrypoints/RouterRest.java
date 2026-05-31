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

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
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
            ),
            @RouterOperation(
                    path = "/bootcamps/{id}",
                    method = RequestMethod.DELETE,
                    beanClass = BootcampHandlerImpl.class,
                    beanMethod = "deleteBootcamp",
                    operation = @Operation(
                            summary = "Eliminar un bootcamp por ID y sus capacidades huérfanas",
                            description = "Elimina un bootcamp de forma transaccional junto con sus relaciones y desencadena de manera reactiva la eliminación en cascada de sus capacidades y tecnologías asociadas si no pertenecen a ningún otro recurso.",
                            operationId = "deleteBootcamp",
                            parameters = {
                                    @Parameter(name = "id", in = ParameterIn.PATH, description = "ID del bootcamp a eliminar", required = true, schema = @Schema(type = "integer"))
                            },
                            responses = {
                                    @ApiResponse(responseCode = "204", description = "Bootcamp y sus dependencias huérfanas eliminados con éxito"),
                                    @ApiResponse(responseCode = "404", description = "El bootcamp con el ID provisto no existe")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/bootcamps/bulk",
                    method = RequestMethod.GET,
                    beanClass = BootcampHandlerImpl.class,
                    beanMethod = "getBootcampsByIds",
                    operation = @Operation(
                            summary = "Obtener bootcamps por lote de IDs de forma enriquecida",
                            description = "Recibe una lista de IDs de bootcamps por parámetro de consulta, obtiene su información local y la enriquece de forma síncrona con sus capacidades y tecnologías del servicio externo. No aplica paginación.",
                            operationId = "getBootcampsByIds",
                            parameters = {
                                    @Parameter(name = "ids", in = ParameterIn.QUERY, description = "Lista de IDs de los bootcamps separados por comas (ej. 1,2,3)", required = true, schema = @Schema(type = "string"))
                            },
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Colección de bootcamps solicitados obtenida y enriquecida con éxito",
                                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = BootcampResponseDTO.class)))
                                    ),
                                    @ApiResponse(responseCode = "400", description = "El parámetro 'ids' no fue enviado o posee un formato inválido")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(BootcampHandlerImpl bootcampHandler) {
        return route(POST("/bootcamps"), bootcampHandler::createBootcamp)
                .andRoute(GET("/bootcamps"), bootcampHandler::getAllBootcamps)
                .andRoute(DELETE("/bootcamps/{id}"), bootcampHandler::deleteBootcamp)
                .andRoute(GET("/bootcamps/bulk"), bootcampHandler::getBootcampsByIds);
    }
}
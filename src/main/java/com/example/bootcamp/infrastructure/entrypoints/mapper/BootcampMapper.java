package com.example.bootcamp.infrastructure.entrypoints.mapper;

import com.example.bootcamp.domain.model.Bootcamp;
import com.example.bootcamp.domain.model.Capacity;
import com.example.bootcamp.domain.model.Technology; // Asegúrate de importar tu modelo de dominio
import com.example.bootcamp.infrastructure.entrypoints.dto.BootcampRequestDTO;
import com.example.bootcamp.infrastructure.entrypoints.dto.BootcampResponseDTO;
import com.example.bootcamp.infrastructure.entrypoints.dto.CapacityResponseDTO;
import com.example.bootcamp.infrastructure.entrypoints.dto.TechnologyResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BootcampMapper {

    // --- MAPEO DE ENTRADA (Request -> Dominio) ---
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "capacityIds", target = "capacities", qualifiedByName = "idsToCapacities")
    Bootcamp toModel(BootcampRequestDTO dto);

    @Named("idsToCapacities")
    default List<Capacity> mapIdsToCapacities(List<Long> capacityIds) {
        if (capacityIds == null) return List.of();
        return capacityIds.stream()
                .map(id -> new Capacity(id, null, null))
                .toList();
    }

    // --- MAPEO DE SALIDA (Dominio -> Response) ---
    // MapStruct detectará automáticamente las listas anidadas gracias a los métodos de abajo
    BootcampResponseDTO toResponseDTO(Bootcamp bootcamp);

    CapacityResponseDTO toCapacityResponseDTO(Capacity capacity);

    // Dentro de BootcampMapper.java
    @Mapping(source = "id", target = "id") // Ajusta el 'source' si en tu dominio se llama diferente
    @Mapping(source = "name", target = "name")
    TechnologyResponseDTO toTechnologyResponseDTO(Technology technology);
}
package com.example.bootcamp.infrastructure.entrypoints.dto;

import java.util.List;

public record CapacityResponseDTO(
        Long id,
        String name,
        List<TechnologyResponseDTO> techs
) {}
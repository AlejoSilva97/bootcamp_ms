package com.example.bootcamp.infrastructure.entrypoints.dto;

import java.time.LocalDateTime;
import java.util.List;

public record BootcampResponseDTO(
        Long id,
        String name,
        String description,
        LocalDateTime launchDate,
        Integer duration,
        List<CapacityResponseDTO> capacities
) {}
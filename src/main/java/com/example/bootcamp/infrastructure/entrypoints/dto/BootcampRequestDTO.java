package com.example.bootcamp.infrastructure.entrypoints.dto;

import java.time.LocalDateTime;
import java.util.List;

public record BootcampRequestDTO(
        String name,
        String description,
        LocalDateTime launchDate,
        Integer duration,
        List<Long> capacityIds
) {}
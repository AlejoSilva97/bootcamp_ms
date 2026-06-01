package com.example.bootcamp.infrastructure.adapters.httpadapter.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ReportRequestDTO(
        Long idBootcamp,
        String name,
        String description,
        LocalDateTime launchDate,
        Integer duration,
        Integer capacitiesCounter,
        Integer techsCounter,
        List<ReportCapacityDTO> capacities
) {}
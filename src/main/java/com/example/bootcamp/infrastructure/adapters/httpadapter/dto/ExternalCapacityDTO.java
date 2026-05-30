package com.example.bootcamp.infrastructure.adapters.httpadapter.dto;

import java.util.List;

public record ExternalCapacityDTO(Long id, String name, String description, List<ExternalTechDTO> technologies) {}

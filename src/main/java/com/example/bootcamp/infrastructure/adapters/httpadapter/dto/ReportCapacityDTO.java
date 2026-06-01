package com.example.bootcamp.infrastructure.adapters.httpadapter.dto;

import java.util.List;

public record ReportCapacityDTO(Long id, String name, List<ReportTechDTO> techs) {}
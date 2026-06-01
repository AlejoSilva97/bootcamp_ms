package com.example.bootcamp.infrastructure.adapters.httpadapter.mapper;

import com.example.bootcamp.domain.model.Bootcamp;
import com.example.bootcamp.domain.model.Capacity;
import com.example.bootcamp.domain.model.Technology;
import com.example.bootcamp.infrastructure.adapters.httpadapter.dto.ReportCapacityDTO;
import com.example.bootcamp.infrastructure.adapters.httpadapter.dto.ReportRequestDTO;
import com.example.bootcamp.infrastructure.adapters.httpadapter.dto.ReportTechDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReportExternalMapper {

    @Mapping(source = "bootcamp.id", target = "idBootcamp")
    @Mapping(source = "bootcamp.name", target = "name")
    @Mapping(source = "bootcamp.description", target = "description")
    @Mapping(source = "bootcamp.launchDate", target = "launchDate")
    @Mapping(source = "bootcamp.duration", target = "duration")
    @Mapping(source = "capacitiesCount", target = "capacitiesCounter")
    @Mapping(source = "techsCount", target = "techsCounter")
    @Mapping(source = "bootcamp.capacities", target = "capacities")
    ReportRequestDTO toRequestDTO(Bootcamp bootcamp, int capacitiesCount, int techsCount);

    ReportCapacityDTO mapCapacity(Capacity capacity);

    ReportTechDTO mapTech(Technology technology);
}
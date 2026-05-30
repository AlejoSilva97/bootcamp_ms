package com.example.bootcamp.infrastructure.adapters.persistenceadapter.mapper;

import com.example.bootcamp.domain.model.Bootcamp;
import com.example.bootcamp.infrastructure.adapters.persistenceadapter.entity.BootcampEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BootcampEntityMapper {

    @Mapping(target = "capacities", ignore = true)
    Bootcamp toModel(BootcampEntity entity);

    BootcampEntity toEntity(Bootcamp bootcamp);
}
package com.example.bootcamp.infrastructure.adapters.persistenceadapter.mapper;

import com.example.bootcamp.domain.model.Bootcamp;
import com.example.bootcamp.domain.model.Capacity;
import com.example.bootcamp.infrastructure.adapters.persistenceadapter.entity.BootcampEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import io.r2dbc.spi.Readable;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface BootcampEntityMapper {

    BootcampEntity toEntity(Bootcamp bootcamp);

    @Mapping(source = "capacities", target = "capacities")
    Bootcamp toModel(BootcampEntity entity, List<Capacity> capacities);

    default BootcampEntity rowToEntity(Readable readable) {
        return new BootcampEntity(
                readable.get("id", Long.class),
                readable.get("name", String.class),
                readable.get("description", String.class),
                readable.get("launch_date", LocalDateTime.class),
                readable.get("duration", Integer.class)
        );
    }
}
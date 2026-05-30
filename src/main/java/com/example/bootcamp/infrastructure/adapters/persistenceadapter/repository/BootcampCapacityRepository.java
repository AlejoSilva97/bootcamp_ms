package com.example.bootcamp.infrastructure.adapters.persistenceadapter.repository;

import com.example.bootcamp.infrastructure.adapters.persistenceadapter.entity.BootcampCapacityEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import java.util.List;

@Repository
public interface BootcampCapacityRepository extends ReactiveCrudRepository<BootcampCapacityEntity, Long> {}
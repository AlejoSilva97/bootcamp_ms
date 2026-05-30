package com.example.bootcamp.application.config;

import com.example.bootcamp.domain.spi.BootcampPersistencePort;
import com.example.bootcamp.domain.spi.CapacityExternalService;
import com.example.bootcamp.domain.usecase.BootcampUseCase;
import com.example.bootcamp.domain.api.BootcampServicePort;
import com.example.bootcamp.infrastructure.adapters.persistenceadapter.BootcampPersistenceAdapter;
import com.example.bootcamp.infrastructure.adapters.persistenceadapter.mapper.BootcampEntityMapper;
import com.example.bootcamp.infrastructure.adapters.persistenceadapter.repository.BootcampRepository;
import com.example.bootcamp.infrastructure.adapters.persistenceadapter.repository.BootcampCapacityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class UseCasesConfig {
        private final BootcampRepository bootcampRepository;
        private final BootcampCapacityRepository bootcampCapacityRepository;
        private final BootcampEntityMapper bootcampEntityMapper;

        @Bean
        public BootcampServicePort bootcampServicePort(BootcampPersistencePort bootcampPersistencePort,
                                                       CapacityExternalService capacityExternalService) {
            return new BootcampUseCase(bootcampPersistencePort, capacityExternalService);
        }
}

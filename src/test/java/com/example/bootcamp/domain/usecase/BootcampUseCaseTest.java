package com.example.bootcamp.domain.usecase;

import com.example.bootcamp.domain.constants.Constants;
import com.example.bootcamp.domain.exceptions.BootcampAlreadyExistsException;
import com.example.bootcamp.domain.exceptions.CapacityNotFoundException;
import com.example.bootcamp.domain.model.Bootcamp;
import com.example.bootcamp.domain.model.Capacity;
import com.example.bootcamp.domain.spi.BootcampPersistencePort;
import com.example.bootcamp.domain.spi.CapacityExternalService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BootcampUseCaseTest {

    @Mock
    private BootcampPersistencePort bootcampPersistencePort;

    @Mock
    private CapacityExternalService capacityExternalService;

    @InjectMocks
    private BootcampUseCase bootcampUseCase;

    @Test
    @DisplayName("Should register bootcamp successfully when validation passes and name is unique")
    void registerBootcamp_GivenValidBootcampAndNameDoesNotExist_WhenRegisterBootcampIsCalled_ThenReturnSavedBootcamp() {
        Bootcamp inputBootcamp = createValidBootcamp();

        when(bootcampPersistencePort.existByName(inputBootcamp.name()))
                .thenReturn(Mono.just(false));
        when(capacityExternalService.verifyCapacitiesByIds(anyList()))
                .thenReturn(Mono.just(true));
        when(bootcampPersistencePort.save(inputBootcamp))
                .thenReturn(Mono.just(inputBootcamp));

        Mono<Bootcamp> result = bootcampUseCase.registerBootcamp(inputBootcamp);

        StepVerifier.create(result)
                .expectNext(inputBootcamp)
                .verifyComplete();

        verify(bootcampPersistencePort, times(1)).existByName(inputBootcamp.name());
        verify(capacityExternalService, times(1)).verifyCapacitiesByIds(anyList());
        verify(bootcampPersistencePort, times(1)).save(inputBootcamp);
    }

    @Test
    @DisplayName("Should throw BootcampAlreadyExistsException when the bootcamp name already exists")
    void registerBootcamp_GivenNameAlreadyExists_WhenRegisterBootcampIsCalled_ThenThrowBootcampAlreadyExistsException() {
        Bootcamp inputBootcamp = createValidBootcamp();
        String expectedErrorMessage = String.format(Constants.BOOTCAMP_ALREADY_EXISTS, inputBootcamp.name());

        when(bootcampPersistencePort.existByName(inputBootcamp.name()))
                .thenReturn(Mono.just(true));

        Mono<Bootcamp> result = bootcampUseCase.registerBootcamp(inputBootcamp);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof BootcampAlreadyExistsException
                        && throwable.getMessage().equals(expectedErrorMessage))
                .verify();

        verify(bootcampPersistencePort, times(1)).existByName(inputBootcamp.name());
        verifyNoInteractions(capacityExternalService);
        verify(bootcampPersistencePort, never()).save(any(Bootcamp.class));
    }

    @Test
    @DisplayName("Should throw CapacityNotFoundException when external capacity verification fails")
    void registerBootcamp_GivenInvalidCapacities_WhenRegisterBootcampIsCalled_ThenThrowCapacityNotFoundException() {
        Bootcamp inputBootcamp = createValidBootcamp();

        when(bootcampPersistencePort.existByName(inputBootcamp.name()))
                .thenReturn(Mono.just(false));
        when(capacityExternalService.verifyCapacitiesByIds(anyList()))
                .thenReturn(Mono.just(false));

        Mono<Bootcamp> result = bootcampUseCase.registerBootcamp(inputBootcamp);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof CapacityNotFoundException
                        && throwable.getMessage().equals(Constants.CAPACITY_NOT_EXISTS))
                .verify();

        verify(bootcampPersistencePort, times(1)).existByName(inputBootcamp.name());
        verify(capacityExternalService, times(1)).verifyCapacitiesByIds(anyList());
        verify(bootcampPersistencePort, never()).save(any(Bootcamp.class));
    }

    private Bootcamp createValidBootcamp() {
        Capacity capacity = new Capacity(1L, "Java Backend Dev", List.of());
        return new Bootcamp(
                1L,
                "Spring Boot Advanced",
                "Intensive Bootcamp focused on reactive architectures",
                LocalDateTime.now().plusDays(5),
                160,
                List.of(capacity)
        );
    }
}
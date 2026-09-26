package org.example.riskintelligenceservice.service;


import org.example.riskintelligenceservice.dto.SimulationRequest;
import org.example.riskintelligenceservice.entity.Simulation;
import org.example.riskintelligenceservice.exception.RiskAccessDeniedException;
import org.example.riskintelligenceservice.repository.SimulationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SimulationServiceTest {

    @Mock
    private SimulationRepository repository;

    @InjectMocks
    private SimulationServiceImpl service;

    private SimulationRequest request;

    @BeforeEach
    void setUp() {

        request = new SimulationRequest(
                1L,
                1L,
                "Severe Weather Impact",
                "Heavy rainfall and flooding scenario"
        );
    }

    @Test
    void shouldCreateSimulation() {

        Simulation saved =
                Simulation.builder()
                        .id(1L)
                        .businessId(1L)
                        .policyId(1L)
                        .ownerId(100L)
                        .scenarioName(
                                "Severe Weather Impact"
                        )
                        .scenarioInput(
                                "Heavy rainfall and flooding scenario"
                        )
                        .projectedRiskScore(
                                BigDecimal.valueOf(65)
                        )
                        .resultSummary(
                                "Simulation completed"
                        )
                        .status(
                                org.example.riskintelligenceservice
                                        .model.SimulationStatus.COMPLETED
                        )
                        .createdBy(100L)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

        when(repository.save(any(Simulation.class)))
                .thenReturn(Mono.just(saved));

        StepVerifier.create(
                        service.createSimulation(
                                request,
                                100L,
                                "BUSINESS_OWNER"
                        )
                )
                .expectNextMatches(response ->
                        response.id().equals(1L)
                                && response.businessId().equals(1L)
                                && response.scenarioName()
                                .equals("Severe Weather Impact")
                                && response.status()
                                .name()
                                .equals("COMPLETED")
                )
                .verifyComplete();
    }

    @Test
    void shouldAllowOwnerToGetSimulation() {

        Simulation simulation =
                Simulation.builder()
                        .id(1L)
                        .businessId(1L)
                        .ownerId(100L)
                        .scenarioName("Flood")
                        .projectedRiskScore(
                                BigDecimal.valueOf(70)
                        )
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

        when(repository.findById(1L))
                .thenReturn(Mono.just(simulation));

        StepVerifier.create(
                        service.getSimulation(
                                1L,
                                100L,
                                "BUSINESS_OWNER"
                        )
                )
                .assertNext(response -> {
                    assertEquals(1L, response.id());
                    assertEquals(1L, response.businessId());
                    assertEquals(
                            "Flood",
                            response.scenarioName()
                    );
                })
                .verifyComplete();
    }

    @Test
    void shouldAllowRiskEngineerToGetSimulation() {

        Simulation simulation =
                Simulation.builder()
                        .id(1L)
                        .businessId(1L)
                        .ownerId(100L)
                        .scenarioName("Flood")
                        .projectedRiskScore(
                                BigDecimal.valueOf(70)
                        )
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

        when(repository.findById(1L))
                .thenReturn(Mono.just(simulation));

        StepVerifier.create(
                        service.getSimulation(
                                1L,
                                999L,
                                "RISK_ENGINEER"
                        )
                )
                .assertNext(response -> {
                    assertEquals(1L, response.id());
                    assertEquals(1L, response.businessId());
                    assertEquals(
                            "Flood",
                            response.scenarioName()
                    );
                })
                .verifyComplete();
    }

    @Test
    void shouldRejectOtherOwner() {

        Simulation simulation =
                Simulation.builder()
                        .id(1L)
                        .businessId(1L)
                        .ownerId(100L)
                        .scenarioName("Flood")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

        when(repository.findById(1L))
                .thenReturn(Mono.just(simulation));

        StepVerifier.create(
                        service.getSimulation(
                                1L,
                                999L,
                                "BUSINESS_OWNER"
                        )
                )
                .expectError(
                        RiskAccessDeniedException.class
                )
                .verify();
    }

    @Test
    void shouldReturnOwnerSimulations() {

        Simulation simulation =
                Simulation.builder()
                        .id(1L)
                        .businessId(1L)
                        .ownerId(100L)
                        .scenarioName("Flood")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

        when(repository.findByOwnerId(100L))
                .thenReturn(
                        Flux.just(simulation)
                );

        StepVerifier.create(
                        service.getSimulations(
                                100L,
                                "BUSINESS_OWNER"
                        )
                )
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void riskEngineerShouldSeeAllSimulations() {

        Simulation simulation =
                Simulation.builder()
                        .id(1L)
                        .businessId(1L)
                        .ownerId(100L)
                        .scenarioName("Flood")
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

        when(repository.findAll())
                .thenReturn(
                        Flux.just(simulation)
                );

        StepVerifier.create(
                        service.getSimulations(
                                999L,
                                "RISK_ENGINEER"
                        )
                )
                .expectNextCount(1)
                .verifyComplete();
    }
}


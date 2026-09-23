package org.example.riskintelligenceservice.controller;

//
//import org.example.riskintelligenceservice.dto.SimulationRequest;
//import org.example.riskintelligenceservice.dto.SimulationResponse;
//import org.example.riskintelligenceservice.model.RiskLevel;
//import org.example.riskintelligenceservice.model.SimulationStatus;
//import org.example.riskintelligenceservice.service.SimulationService;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.core.Authentication;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//import reactor.test.StepVerifier;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class SimulationControllerTest {
//
//    @Mock
//    private SimulationService service;
//
//    @Mock
//    private Authentication authentication;
//
//    @InjectMocks
//    private SimulationController controller;
//
//    private SimulationResponse response() {
//
//        return new SimulationResponse(
//                1L,
//                1L,
//                1L,
//                "Flood Scenario",
//                "Heavy flooding",
//                BigDecimal.valueOf(70),
//                RiskLevel.HIGH,
//                "Simulation completed",
//                SimulationStatus.COMPLETED,
//                100L,
//                LocalDateTime.now(),
//                LocalDateTime.now()
//        );
//    }
//
//    private void mockAuthentication() {
//
//        when(authentication.getName())
//                .thenReturn("100");
//
//        when(authentication.getAuthorities())
//                .thenReturn(
//                        java.util.List.of(
//                                new org.springframework.security.core.authority
//                                        .SimpleGrantedAuthority(
//                                        "ROLE_BUSINESS_OWNER"
//                                )
//                        )
//                );
//    }
//
//    @Test
//    void shouldCreateSimulation() {
//
//        mockAuthentication();
//
//        when(service.createSimulation(
//                any(SimulationRequest.class),
//                eq(100L),
//                eq("BUSINESS_OWNER")
//        )).thenReturn(
//                Mono.just(response())
//        );
//
//        SimulationRequest request =
//                new SimulationRequest(
//                        1L,
//                        1L,
//                        "Flood Scenario",
//                        "Heavy flooding"
//                );
//
//        StepVerifier.create(
//                        controller.createSimulation(
//                                request,
//                                authentication
//                        )
//                )
//                .expectNextMatches(
//                        result ->
//                                result.getStatusCode().value() == 201
//                                        && result.getBody()
//                                        .id()
//                                        .equals(1L)
//                )
//                .verifyComplete();
//    }
//
//    @Test
//    void shouldGetSimulation() {
//
//        mockAuthentication();
//
//        when(service.getSimulation(
//                1L,
//                100L,
//                "BUSINESS_OWNER"
//        )).thenReturn(
//                Mono.just(response())
//        );
//
//        StepVerifier.create(
//                        controller.getSimulation(
//                                1L,
//                                authentication
//                        )
//                )
//                .expectNextMatches(
//                        result ->
//                                result.id().equals(1L)
//                )
//                .verifyComplete();
//    }
//
//    @Test
//    void shouldGetSimulations() {
//
//        mockAuthentication();
//
//        when(service.getSimulations(
//                100L,
//                "BUSINESS_OWNER"
//        )).thenReturn(
//                Flux.just(response())
//        );
//
//        StepVerifier.create(
//                        controller.getSimulations(
//                                authentication
//                        )
//                )
//                .expectNextCount(1)
//                .verifyComplete();
//    }
//}
//
//
//

package org.example.riskintelligenceservice.service;

import org.example.riskintelligenceservice.dto.RiskAssessmentRequest;
import org.example.riskintelligenceservice.entity.RiskAssessment;
import org.example.riskintelligenceservice.exception.RiskAccessDeniedException;
import org.example.riskintelligenceservice.repository.RiskAssessmentRepository;
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
class RiskAssessmentServiceTest {

    @Mock
    private RiskAssessmentRepository repository;

    @InjectMocks
    private RiskAssessmentServiceImpl service;

    private RiskAssessmentRequest request;

    @BeforeEach
    void setUp() {

        request = new RiskAssessmentRequest(
                1L,
                1L,
                BigDecimal.valueOf(62.5),
                "High property exposure",
                "Additional safeguards recommended"
        );
    }

    @Test
    void shouldCreateRiskAssessment() {

        RiskAssessment saved =
                RiskAssessment.builder()
                        .id(1L)
                        .businessId(1L)
                        .policyId(1L)
                        .ownerId(100L)
                        .riskScore(
                                BigDecimal.valueOf(62.5)
                        )
                        .riskLevel(
                                org.example.riskintelligenceservice
                                        .model.RiskLevel.HIGH
                        )
                        .riskFactors(
                                "High property exposure"
                        )
                        .recommendation(
                                "Additional safeguards recommended"
                        )
                        .assessedBy(100L)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

        when(repository.save(any(RiskAssessment.class)))
                .thenReturn(Mono.just(saved));

        StepVerifier.create(
                        service.createAssessment(
                                request,
                                100L,
                                "BUSINESS_OWNER"
                        )
                )
                .expectNextMatches(response ->
                        response.id().equals(1L)
                                && response.businessId().equals(1L)
                                && response.riskScore()
                                .compareTo(
                                        BigDecimal.valueOf(62.5)
                                ) == 0
                                && response.riskLevel()
                                .name()
                                .equals("HIGH")
                )
                .verifyComplete();
    }

    @Test
    void shouldAllowOwnerToViewOwnAssessment() {

        RiskAssessment assessment =
                RiskAssessment.builder()
                        .id(1L)
                        .businessId(1L)
                        .policyId(1L)
                        .ownerId(100L)
                        .riskScore(BigDecimal.valueOf(40))
                        .riskLevel(
                                org.example.riskintelligenceservice.model.RiskLevel.MEDIUM
                        )
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

        when(repository.findById(1L))
                .thenReturn(Mono.just(assessment));

        StepVerifier.create(
                        service.getAssessment(
                                1L,
                                100L,
                                "BUSINESS_OWNER"
                        )
                )
                .assertNext(response -> {
                    assertEquals(1L, response.id());
                    assertEquals(1L, response.businessId());
                    assertEquals(
                            org.example.riskintelligenceservice.model.RiskLevel.MEDIUM,
                            response.riskLevel()
                    );
                })
                .verifyComplete();
    }
    @Test
    void shouldAllowRiskEngineerToViewAssessment() {

        RiskAssessment assessment =
                RiskAssessment.builder()
                        .id(1L)
                        .businessId(1L)
                        .policyId(1L)
                        .ownerId(100L)
                        .riskScore(BigDecimal.valueOf(80))
                        .riskLevel(
                                org.example.riskintelligenceservice.model.RiskLevel.CRITICAL
                        )
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

        when(repository.findById(1L))
                .thenReturn(Mono.just(assessment));

        StepVerifier.create(
                        service.getAssessment(
                                1L,
                                999L,
                                "RISK_ENGINEER"
                        )
                )
                .assertNext(response -> {
                    assertEquals(1L, response.id());
                    assertEquals(1L, response.businessId());
                    assertEquals(
                            org.example.riskintelligenceservice.model.RiskLevel.CRITICAL,
                            response.riskLevel()
                    );
                })
                .verifyComplete();
    }

    @Test
    void shouldRejectDifferentBusinessOwner() {

        RiskAssessment assessment =
                RiskAssessment.builder()
                        .id(1L)
                        .businessId(1L)
                        .ownerId(100L)
                        .riskScore(
                                BigDecimal.valueOf(50)
                        )
                        .riskLevel(
                                org.example.riskintelligenceservice
                                        .model.RiskLevel.HIGH
                        )
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

        when(repository.findById(1L))
                .thenReturn(Mono.just(assessment));

        StepVerifier.create(
                        service.getAssessment(
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
    void shouldGetBusinessAssessmentsForOwner() {

        RiskAssessment assessment =
                RiskAssessment.builder()
                        .id(1L)
                        .businessId(1L)
                        .ownerId(100L)
                        .riskScore(
                                BigDecimal.valueOf(30)
                        )
                        .riskLevel(
                                org.example.riskintelligenceservice
                                        .model.RiskLevel.MEDIUM
                        )
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

        when(repository.findByBusinessId(1L))
                .thenReturn(
                        Flux.just(assessment)
                );

        StepVerifier.create(
                        service.getByBusiness(
                                1L,
                                100L,
                                "BUSINESS_OWNER"
                        )
                )
                .expectNextCount(1)
                .verifyComplete();
    }
}


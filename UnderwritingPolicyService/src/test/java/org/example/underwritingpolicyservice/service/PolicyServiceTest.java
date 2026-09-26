package org.example.underwritingpolicyservice.service;


import org.example.underwritingpolicyservice.dto.CreatePolicyRequest;
import org.example.underwritingpolicyservice.dto.UpdatePolicyRequest;
import org.example.underwritingpolicyservice.entity.Policy;
import org.example.underwritingpolicyservice.exception.PolicyAccessDeniedException;
import org.example.underwritingpolicyservice.model.PolicyStatus;
import org.example.underwritingpolicyservice.repository.PolicyRepository;
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
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PolicyServiceTest {

    @Mock
    private PolicyRepository policyRepository;

    @InjectMocks
    private PolicyServiceImpl policyService;

    private Policy policy;

    @BeforeEach
    void setUp() {

        policy = Policy.builder()
                .id(1L)
                .businessId(10L)
                .ownerId(100L)
                .policyNumber("POL-1001")
                .policyType(PolicyType.BUSINESS)
                .coverageAmount(
                        new BigDecimal("1000000")
                )
                .premiumAmount(
                        new BigDecimal("25000")
                )
                .riskLevel(RiskLevel.MEDIUM)
                .status(PolicyStatus.DRAFT)
                .startDate(
                        LocalDate.of(2026, 1, 1)
                )
                .endDate(
                        LocalDate.of(2027, 1, 1)
                )
                .createdAt(
                        LocalDateTime.now()
                )
                .updatedAt(
                        LocalDateTime.now()
                )
                .build();
    }

    @Test
    void businessOwnerCanCreatePolicy() {

        CreatePolicyRequest request =
                new CreatePolicyRequest(
                        10L,
                        "POL-1001",
                        PolicyType.BUSINESS,
                        new BigDecimal("1000000"),
                        new BigDecimal("25000"),
                        LocalDate.of(2026, 1, 1),
                        LocalDate.of(2027, 1, 1),
                        "Initial underwriting"
                );

        when(
                policyRepository.existsByPolicyNumber(
                        "POL-1001"
                )
        ).thenReturn(Mono.just(false));

        when(
                policyRepository.save(any(Policy.class))
        ).thenReturn(Mono.just(policy));

        StepVerifier.create(
                        policyService.createPolicy(
                                request,
                                100L,
                                "BUSINESS_OWNER"
                        )
                )
                .expectNextCount(1)
                .verifyComplete();

        verify(
                policyRepository
        ).save(any(Policy.class));
    }

    @Test
    void claimsAdjusterCannotCreatePolicy() {

        CreatePolicyRequest request =
                new CreatePolicyRequest(
                        10L,
                        "POL-1002",
                        new BigDecimal("1000000"),
                        new BigDecimal("25000"),
                        LocalDate.of(2026, 1, 1),
                        LocalDate.of(2027, 1, 1),
                        null
                );

        StepVerifier.create(
                        policyService.createPolicy(
                                request,
                                100L,
                                "CLAIMS_ADJUSTER"
                        )
                )
                .expectError(
                        PolicyAccessDeniedException.class
                )
                .verify();

        verifyNoInteractions(policyRepository);
    }

    @Test
    void ownerCanAccessOwnPolicy() {

        when(
                policyRepository.findPolicyById(1L)
        ).thenReturn(Mono.just(policy));

        StepVerifier.create(
                        policyService.getPolicy(
                                1L,
                                100L,
                                "BUSINESS_OWNER"
                        )
                )
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void differentOwnerCannotAccessPolicy() {

        when(
                policyRepository.findPolicyById(1L)
        ).thenReturn(Mono.just(policy));

        StepVerifier.create(
                        policyService.getPolicy(
                                1L,
                                999L,
                                "BUSINESS_OWNER"
                        )
                )
                .expectError(
                        PolicyAccessDeniedException.class
                )
                .verify();
    }

    @Test
    void underwriterCanAccessPolicy() {

        when(
                policyRepository.findPolicyById(1L)
        ).thenReturn(Mono.just(policy));

        StepVerifier.create(
                        policyService.getPolicy(
                                1L,
                                200L,
                                "UNDERWRITER"
                        )
                )
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void riskEngineerCanAccessPolicy() {

        when(
                policyRepository.findPolicyById(1L)
        ).thenReturn(Mono.just(policy));

        StepVerifier.create(
                        policyService.getPolicy(
                                1L,
                                300L,
                                "RISK_ENGINEER"
                        )
                )
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void ownerCanUpdateDraftPolicy() {

        UpdatePolicyRequest request =
                new UpdatePolicyRequest(
                        PolicyType.BUSINESS,
                        new BigDecimal("1500000"),
                        new BigDecimal("30000"),
                        LocalDate.of(2026, 1, 1),
                        LocalDate.of(2027, 1, 1),
                        null,
                        "Updated details"
                );

        when(
                policyRepository.findPolicyById(1L)
        ).thenReturn(Mono.just(policy));

        when(
                policyRepository.save(any(Policy.class))
        ).thenReturn(Mono.just(policy));

        StepVerifier.create(
                        policyService.updatePolicy(
                                1L,
                                request,
                                100L,
                                "BUSINESS_OWNER"
                        )
                )
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void ownerCannotUpdateAnotherOwnersPolicy() {

        UpdatePolicyRequest request =
                new UpdatePolicyRequest(
                        PolicyType.BUSINESS,
                        new BigDecimal("1500000"),
                        new BigDecimal("30000"),
                        LocalDate.of(2026, 1, 1),
                        LocalDate.of(2027, 1, 1),
                        null,
                        null
                );

        when(
                policyRepository.findPolicyById(1L)
        ).thenReturn(Mono.just(policy));

        StepVerifier.create(
                        policyService.updatePolicy(
                                1L,
                                request,
                                999L,
                                "BUSINESS_OWNER"
                        )
                )
                .expectError(
                        PolicyAccessDeniedException.class
                )
                .verify();
    }

    @Test
    void underwriterCanChangeStatus() {

        when(
                policyRepository.findPolicyById(1L)
        ).thenReturn(Mono.just(policy));

        when(
                policyRepository.save(any(Policy.class))
        ).thenReturn(Mono.just(policy));

        StepVerifier.create(
                        policyService.updatePolicyStatus(
                                1L,
                                PolicyStatus.SUBMITTED,
                                200L,
                                "UNDERWRITER"
                        )
                )
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void ownerCannotChangeStatus() {

//        when(
//                policyRepository.findPolicyById(1L)
//        ).thenReturn(Mono.just(policy));

        StepVerifier.create(
                        policyService.updatePolicyStatus(
                                1L,
                                PolicyStatus.SUBMITTED,
                                100L,
                                "BUSINESS_OWNER"
                        )
                )
                .expectError(
                        PolicyAccessDeniedException.class
                )
                .verify();
    }

    @Test
    void adminCanGetAllPolicies() {

        when(
                policyRepository.findAll()
        ).thenReturn(Flux.just(policy));

        StepVerifier.create(
                        policyService.getAllPolicies(
                                1L,
                                "ADMIN"
                        )
                )
                .expectNextCount(1)
                .verifyComplete();
    }
}





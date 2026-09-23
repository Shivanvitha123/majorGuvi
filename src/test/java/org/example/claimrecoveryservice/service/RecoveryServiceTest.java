package org.example.claimrecoveryservice.service;


import org.example.claimrecoveryservice.dto.CreateRecoveryRequest;
import org.example.claimrecoveryservice.entity.Claim;
import org.example.claimrecoveryservice.entity.Recovery;
import org.example.claimrecoveryservice.exception.ClaimAccessDeniedException;
import org.example.claimrecoveryservice.model.ClaimStatus;
import org.example.claimrecoveryservice.model.ClaimType;
import org.example.claimrecoveryservice.model.RecoveryStatus;
import org.example.claimrecoveryservice.repository.ClaimRepository;
import org.example.claimrecoveryservice.repository.RecoveryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecoveryServiceTest {

    @Mock
    private RecoveryRepository recoveryRepository;

    @Mock
    private ClaimRepository claimRepository;

    @InjectMocks
    private RecoveryServiceImpl service;

    @Test
    void claimsAdjusterCanCreateRecovery() {

        Claim claim =
                Claim.builder()
                        .id(1L)
                        .ownerId(100L)
                        .businessId(1L)
                        .policyId(1L)
                        .claimNumber(
                                "CLM-2026-0001"
                        )
                        .claimType(
                                ClaimType.THEFT
                        )
                        .status(
                                ClaimStatus.APPROVED
                        )
                        .createdAt(
                                LocalDateTime.now()
                        )
                        .updatedAt(
                                LocalDateTime.now()
                        )
                        .build();

        Recovery recovery =
                Recovery.builder()
                        .id(1L)
                        .claimId(1L)
                        .ownerId(100L)
                        .recoveryAmount(
                                BigDecimal.valueOf(50000)
                        )
                        .status(
                                RecoveryStatus.INITIATED
                        )
                        .processedBy(500L)
                        .createdAt(
                                LocalDateTime.now()
                        )
                        .updatedAt(
                                LocalDateTime.now()
                        )
                        .build();

        when(claimRepository.findById(1L))
                .thenReturn(Mono.just(claim));

        when(recoveryRepository.save(
                any(Recovery.class)
        )).thenReturn(
                Mono.just(recovery)
        );

        CreateRecoveryRequest request =
                new CreateRecoveryRequest(
                        BigDecimal.valueOf(50000),
                        "Third Party",
                        "Recovery initiated"
                );

        StepVerifier.create(
                        service.createRecovery(
                                1L,
                                request,
                                500L,
                                "CLAIMS_ADJUSTER"
                        )
                )
                .expectNextMatches(
                        response ->
                                response.id().equals(1L)
                                        && response.claimId().equals(1L)
                                        && response.status()
                                        == RecoveryStatus.INITIATED
                )
                .verifyComplete();
    }

    @Test
    void businessOwnerCannotCreateRecovery() {

        CreateRecoveryRequest request =
                new CreateRecoveryRequest(
                        BigDecimal.valueOf(50000),
                        "Third Party",
                        "Recovery"
                );

        StepVerifier.create(
                        service.createRecovery(
                                1L,
                                request,
                                100L,
                                "BUSINESS_OWNER"
                        )
                )
                .expectError(
                        ClaimAccessDeniedException.class
                )
                .verify();
    }
}









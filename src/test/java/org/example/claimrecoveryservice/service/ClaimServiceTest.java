package org.example.claimrecoveryservice.service;



import org.example.claimrecoveryservice.dto.CreateClaimRequest;
import org.example.claimrecoveryservice.entity.Claim;
import org.example.claimrecoveryservice.exception.ClaimAccessDeniedException;
import org.example.claimrecoveryservice.model.ClaimStatus;
import org.example.claimrecoveryservice.model.ClaimType;
import org.example.claimrecoveryservice.repository.ClaimRepository;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClaimServiceTest {

    @Mock
    private ClaimRepository repository;

    @InjectMocks
    private ClaimServiceImpl service;

    @Test
    void ownerCanCreateClaim() {

        CreateClaimRequest request =
                new CreateClaimRequest(
                        1L,
                        1L,
                        "CLM-2026-0001",
                        ClaimType.PROPERTY_DAMAGE,
                        LocalDate.of(2026, 9, 1),
                        LocalDate.of(2026, 9, 2),
                        BigDecimal.valueOf(500000),
                        "Property damage"
                );

        Claim claim =
                Claim.builder()
                        .id(1L)
                        .businessId(1L)
                        .policyId(1L)
                        .ownerId(100L)
                        .claimNumber(
                                "CLM-2026-0001"
                        )
                        .claimType(
                                ClaimType.PROPERTY_DAMAGE
                        )
                        .claimedAmount(
                                BigDecimal.valueOf(500000)
                        )
                        .status(
                                ClaimStatus.SUBMITTED
                        )
                        .createdAt(
                                LocalDateTime.now()
                        )
                        .updatedAt(
                                LocalDateTime.now()
                        )
                        .build();

        when(repository.findByClaimNumber(
                "CLM-2026-0001"
        )).thenReturn(Mono.empty());

        when(repository.save(
                any(Claim.class)
        )).thenReturn(
                Mono.just(claim)
        );

        StepVerifier.create(
                        service.createClaim(
                                request,
                                100L,
                                "BUSINESS_OWNER"
                        )
                )
                .expectNextMatches(
                        response ->
                                response.id().equals(1L)
                                        && response.status()
                                        == ClaimStatus.SUBMITTED
                )
                .verifyComplete();
    }

    @Test
    void unauthorizedRoleCannotCreateClaim() {

        CreateClaimRequest request =
                new CreateClaimRequest(
                        1L,
                        1L,
                        "CLM-2026-0002",
                        ClaimType.FIRE,
                        LocalDate.now(),
                        LocalDate.now(),
                        BigDecimal.valueOf(100000),
                        "Fire"
                );

        StepVerifier.create(
                        service.createClaim(
                                request,
                                200L,
                                "RISK_ENGINEER"
                        )
                )
                .expectError(
                        ClaimAccessDeniedException.class
                )
                .verify();
    }

    @Test
    void ownerCanGetOwnClaims() {

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
                                ClaimType.FLOOD
                        )
                        .claimedAmount(
                                BigDecimal.valueOf(200000)
                        )
                        .status(
                                ClaimStatus.SUBMITTED
                        )
                        .createdAt(
                                LocalDateTime.now()
                        )
                        .updatedAt(
                                LocalDateTime.now()
                        )
                        .build();

        when(repository.findByOwnerId(100L))
                .thenReturn(
                        Flux.just(claim)
                );

        StepVerifier.create(
                        service.getOwnerClaims(
                                100L,
                                "BUSINESS_OWNER"
                        )
                )
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void claimsAdjusterCanGetAllClaims() {

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
                                ClaimType.FLOOD
                        )
                        .claimedAmount(
                                BigDecimal.valueOf(200000)
                        )
                        .status(
                                ClaimStatus.SUBMITTED
                        )
                        .createdAt(
                                LocalDateTime.now()
                        )
                        .updatedAt(
                                LocalDateTime.now()
                        )
                        .build();

        when(repository.findAll())
                .thenReturn(
                        Flux.just(claim)
                );

        StepVerifier.create(
                        service.getAllClaims(
                                500L,
                                "CLAIMS_ADJUSTER"
                        )
                )
                .expectNextCount(1)
                .verifyComplete();
    }
}





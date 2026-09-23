package org.example.claimrecoveryservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.example.claimrecoveryservice.client.NotificationAuditClient;
import org.example.claimrecoveryservice.dto.ClaimResponse;
import org.example.claimrecoveryservice.dto.CreateClaimRequest;
import org.example.claimrecoveryservice.dto.UpdateClaimStatusRequest;
import org.example.claimrecoveryservice.service.ClaimService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/claims")
@RequiredArgsConstructor
public class ClaimController {

    private final ClaimService service;
    private final NotificationAuditClient notificationAuditClient;

    @PostMapping
    public Mono<ResponseEntity<ClaimResponse>> createClaim(
            @Valid @RequestBody CreateClaimRequest request,
            Authentication authentication) {

        Long userId = getUserId(authentication);
        String role = getRole(authentication);

        return service
                .createClaim(request, userId, role)
                .flatMap(response ->
                        notificationAuditClient.publish(
                                        userId,
                                        role,
                                        "CREATE_CLAIM",
                                        "CLAIM",
                                        response.id(),
                                        "Claim " +
                                                response.claimNumber() +
                                                " was created.",
                                        userId,
                                        "INFO",
                                        "Claim Created",
                                        "Claim " +
                                                response.claimNumber() +
                                                " was submitted."
                                )
                                .thenReturn(
                                        ResponseEntity
                                                .status(HttpStatus.CREATED)
                                                .body(response)
                                )
                );


//        return service
//                .createClaim(request, userId, role)
//                .map(response ->
//                        ResponseEntity
//                                .status(HttpStatus.CREATED)
//                                .body(response)
//                );
    }

    @GetMapping
    public Mono<ResponseEntity<Flux<ClaimResponse>>>
    getAllClaims(
            Authentication authentication) {

        Long userId = getUserId(authentication);
        String role = getRole(authentication);

        return Mono.just(
                ResponseEntity.ok(
                        service.getAllClaims(
                                userId,
                                role
                        )
                )
        );
    }

    @GetMapping("/owner")
    public Mono<ResponseEntity<Flux<ClaimResponse>>>
    getOwnerClaims(
            Authentication authentication) {

        Long userId = getUserId(authentication);
        String role = getRole(authentication);

        return Mono.just(
                ResponseEntity.ok(
                        service.getOwnerClaims(
                                userId,
                                role
                        )
                )
        );
    }

    @GetMapping("/{claimId}")
    public Mono<ClaimResponse> getClaim(
            @PathVariable Long claimId,
            Authentication authentication) {

        Long userId = getUserId(authentication);
        String role = getRole(authentication);

        return service.getClaim(
                claimId,
                userId,
                role
        );
    }

    @PatchMapping("/{claimId}/status")
    public Mono<ClaimResponse> updateClaimStatus(
            @PathVariable Long claimId,
            @Valid @RequestBody
            UpdateClaimStatusRequest request,
            Authentication authentication) {

        Long userId = getUserId(authentication);
        String role = getRole(authentication);
        return service
                .updateClaimStatus(
                        claimId,
                        request,
                        userId,
                        role
                )
                .flatMap(response ->
                        notificationAuditClient.publish(
                                        userId,
                                        role,
                                        "UPDATE_CLAIM_STATUS",
                                        "CLAIM",
                                        response.id(),
                                        "Claim status changed to "
                                                + response.status(),
                                        userId,
                                        "INFO",
                                        "Claim Status Updated",
                                        "Claim " +
                                                response.claimNumber() +
                                                " is now " +
                                                response.status()
                                )
                                .thenReturn(response)
                );


//        return service.updateClaimStatus(
//                claimId,
//                request,
//                userId,
//                role
//        );
    }

    private Long getUserId(
            Authentication authentication) {

        if (authentication == null) {
            return null;
        }

        try {
            return Long.valueOf(
                    authentication.getName()
            );
        } catch (Exception exception) {
            return null;
        }
    }

    private String getRole(
            Authentication authentication) {

        if (authentication == null) {
            return null;
        }

        return authentication
                .getAuthorities()
                .stream()
                .findFirst()
                .map(authority ->
                        authority
                                .getAuthority()
                                .replace(
                                        "ROLE_",
                                        ""
                                )
                )
                .orElse(null);
    }
}


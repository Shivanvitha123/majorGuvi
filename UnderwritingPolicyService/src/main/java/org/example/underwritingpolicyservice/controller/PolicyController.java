package org.example.underwritingpolicyservice.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.underwritingpolicyservice.client.NotificationAuditClient;
import org.example.underwritingpolicyservice.dto.CreatePolicyRequest;
import org.example.underwritingpolicyservice.dto.PolicyResponse;
import org.example.underwritingpolicyservice.dto.UpdatePolicyRequest;
import org.example.underwritingpolicyservice.model.PolicyStatus;
import org.example.underwritingpolicyservice.service.PolicyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/policies")
@RequiredArgsConstructor
public class PolicyController {

    private final PolicyService policyService;
    private final NotificationAuditClient notificationAuditClient;

    /*
     * 26. CREATE POLICY
     */
    @PostMapping
    public Mono<ResponseEntity<PolicyResponse>> createPolicy(
            @Valid @RequestBody CreatePolicyRequest request,
            Authentication authentication) {

        Long userId =
                getUserId(authentication);

        String role =
                getRole(authentication);
        return policyService
                .createPolicy(request, userId, role)
                .flatMap(response ->
                        notificationAuditClient.publish(
                                        userId,
                                        role,
                                        "CREATE_POLICY",
                                        "POLICY",
                                        response.id(),
                                        "Policy " +
                                                response.policyNumber() +
                                                " was created.",
                                        userId,
                                        "SUCCESS",
                                        "Policy Created",
                                        "Policy " +
                                                response.policyNumber() +
                                                " was created successfully."
                                )
                                .thenReturn(
                                        ResponseEntity
                                                .status(HttpStatus.CREATED)
                                                .body(response)
                                )
                );



//        return policyService
//                .createPolicy(
//                        request,
//                        userId,
//                        role
//                )
//                .map(response ->
//                        ResponseEntity
//                                .status(HttpStatus.CREATED)
//                                .body(response)
//                );
    }

    /*
     * 28. GET OWNER POLICIES
     *
     * IMPORTANT:
     * This method is declared before the /{policyId}
     * endpoint for clarity.
     */
    @GetMapping("/owner")
    public Flux<PolicyResponse> getOwnerPolicies(
            Authentication authentication) {

        Long userId =
                getUserId(authentication);

        String role =
                getRole(authentication);

        return policyService
                .getOwnerPolicies(
                        userId,
                        role
                );
    }

    /*
     * GET ALL POLICIES
     *
     * ADMIN / UNDERWRITER / RISK_ENGINEER
     */
    @GetMapping
    public Flux<PolicyResponse> getAllPolicies(
            Authentication authentication) {

        Long userId =
                getUserId(authentication);

        String role =
                getRole(authentication);

        return policyService
                .getAllPolicies(
                        userId,
                        role
                );
    }

    /*
     * 27. GET POLICY
     */
    @GetMapping("/{policyId}")
    public Mono<ResponseEntity<PolicyResponse>> getPolicy(
            @PathVariable Long policyId,
            Authentication authentication) {

        Long userId =
                getUserId(authentication);

        String role =
                getRole(authentication);

        return policyService
                .getPolicy(
                        policyId,
                        userId,
                        role
                )
                .map(ResponseEntity::ok);
    }

    /*
     * 29. UPDATE POLICY
     */
    @PutMapping("/{policyId}")
    public Mono<ResponseEntity<PolicyResponse>> updatePolicy(
            @PathVariable Long policyId,
            @Valid @RequestBody UpdatePolicyRequest request,
            Authentication authentication) {

        Long userId =
                getUserId(authentication);

        String role =
                getRole(authentication);

        return policyService
                .updatePolicy(
                        policyId,
                        request,
                        userId,
                        role
                )
                .map(ResponseEntity::ok);
    }

    /*
     * 30. SUBMIT POLICY
     */
    @PostMapping("/{policyId}/submit")
    public Mono<ResponseEntity<PolicyResponse>> submitPolicy(
            @PathVariable Long policyId,
            Authentication authentication) {

        Long userId =
                getUserId(authentication);

        String role =
                getRole(authentication);


        return policyService
                .submitPolicy(policyId, userId, role)
                .flatMap(response ->
                        notificationAuditClient.publish(
                                        userId,
                                        role,
                                        "SUBMIT_POLICY",
                                        "POLICY",
                                        response.id(),
                                        "Policy " +
                                                response.policyNumber() +
                                                " was submitted.",
                                        userId,
                                        "ACTION_REQUIRED",
                                        "Policy Submitted",
                                        "Policy " +
                                                response.policyNumber() +
                                                " has been submitted."
                                )
                                .thenReturn(
                                        ResponseEntity.ok(response)
                                )
                );


//        return policyService
//                .submitPolicy(
//                        policyId,
//                        userId,
//                        role
//                )
//                .map(ResponseEntity::ok);
//    }
    }

    /*
     * 32. UNDERWRITER CHANGE STATUS
     */
    @PatchMapping("/{policyId}/status")
    public Mono<ResponseEntity<PolicyResponse>> updatePolicyStatus(
            @PathVariable Long policyId,
            @RequestParam PolicyStatus status,
            Authentication authentication) {

        Long userId =
                getUserId(authentication);

        String role =
                getRole(authentication);

        return policyService
                .updatePolicyStatus(
                        policyId,
                        status,
                        userId,
                        role
                )
                .flatMap(response ->
                        notificationAuditClient.publish(
                                        userId,
                                        role,
                                        "UPDATE_POLICY_STATUS",
                                        "POLICY",
                                        response.id(),
                                        "Policy status changed to "
                                                + response.status(),
                                        userId,
                                        "INFO",
                                        "Policy Status Updated",
                                        "Policy " +
                                                response.policyNumber() +
                                                " is now " +
                                                response.status()
                                )
                                .thenReturn(
                                        ResponseEntity.ok(response)
                                )
                );




//        return policyService
//                .updatePolicyStatus(
//                        policyId,
//                        status,
//                        userId,
//                        role
//                )
//                .map(ResponseEntity::ok);
    }

    private Long getUserId(
            Authentication authentication) {

        if (authentication == null
                || authentication.getPrincipal() == null) {

            return null;
        }

        try {
            return Long.parseLong(
                    authentication
                            .getPrincipal()
                            .toString()
            );
        } catch (NumberFormatException ex) {
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
                        authority.getAuthority()
                                .replace("ROLE_", "")
                )
                .orElse(null);
    }
}

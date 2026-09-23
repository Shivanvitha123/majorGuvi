package org.example.riskintelligenceservice.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.riskintelligenceservice.client.NotificationAuditClient;
import org.example.riskintelligenceservice.dto.RiskAssessmentRequest;
import org.example.riskintelligenceservice.dto.RiskAssessmentResponse;
import org.example.riskintelligenceservice.service.RiskAssessmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/risk/assessments")
@RequiredArgsConstructor
public class RiskAssessmentController {

    private final RiskAssessmentService service;
    private final NotificationAuditClient notificationAuditClient;

    @PostMapping
    public Mono<ResponseEntity<RiskAssessmentResponse>>
    createAssessment(
            @Valid @RequestBody RiskAssessmentRequest request,
            Authentication authentication) {

        Long userId =
                Long.valueOf(
                        authentication.getName()
                );

        String role =
                authentication
                        .getAuthorities()
                        .iterator()
                        .next()
                        .getAuthority()
                        .replace("ROLE_", "");

        return service
                .createAssessment(
                        request,
                        userId,
                        role
                )
                .flatMap(response ->
                        notificationAuditClient.publish(
                                        userId,
                                        role,
                                        "CREATE_RISK_ASSESSMENT",
                                        "RISK_ASSESSMENT",
                                        response.id(),
                                        "Risk assessment completed with score "
                                                + response.riskScore(),
                                        userId,
                                        "INFO",
                                        "Risk Assessment Completed",
                                        "Risk assessment has been completed."
                                )
                                .thenReturn(
                                        ResponseEntity
                                                .status(HttpStatus.CREATED)
                                                .body(response)
                                )
                );


//        return service
//                .createAssessment(
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

    @GetMapping("/{id}")
    public Mono<RiskAssessmentResponse>
    getAssessment(
            @PathVariable Long id,
            Authentication authentication) {

        Long userId =
                Long.valueOf(
                        authentication.getName()
                );

        String role =
                authentication
                        .getAuthorities()
                        .iterator()
                        .next()
                        .getAuthority()
                        .replace("ROLE_", "");

        return service.getAssessment(
                id,
                userId,
                role
        );
    }

    @GetMapping("/business/{businessId}")
    public Flux<RiskAssessmentResponse>
    getByBusiness(
            @PathVariable Long businessId,
            Authentication authentication) {

        Long userId =
                Long.valueOf(
                        authentication.getName()
                );

        String role =
                authentication
                        .getAuthorities()
                        .iterator()
                        .next()
                        .getAuthority()
                        .replace("ROLE_", "");

        return service.getByBusiness(
                businessId,
                userId,
                role
        );
    }
}


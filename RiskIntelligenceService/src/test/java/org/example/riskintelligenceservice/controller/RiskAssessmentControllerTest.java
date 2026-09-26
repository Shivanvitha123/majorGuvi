package org.example.riskintelligenceservice.controller;

//
//import org.example.riskintelligenceservice.dto.RiskAssessmentRequest;
//import org.example.riskintelligenceservice.dto.RiskAssessmentResponse;
//import org.example.riskintelligenceservice.model.RiskLevel;
//import org.example.riskintelligenceservice.service.RiskAssessmentService;
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
//class RiskAssessmentControllerTest {
//
//    @Mock
//    private RiskAssessmentService service;
//
//    @Mock
//    private Authentication authentication;
//
//    @InjectMocks
//    private RiskAssessmentController controller;
//
//    @Test
//    void shouldCreateAssessment() {
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
//
//        RiskAssessmentResponse response =
//                new RiskAssessmentResponse(
//                        1L,
//                        1L,
//                        1L,
//                        BigDecimal.valueOf(60),
//                        RiskLevel.HIGH,
//                        "High exposure",
//                        "Improve controls",
//                        100L,
//                        LocalDateTime.now(),
//                        LocalDateTime.now()
//                );
//
//        when(service.createAssessment(
//                any(RiskAssessmentRequest.class),
//                eq(100L),
//                eq("BUSINESS_OWNER")
//        )).thenReturn(
//                Mono.just(response)
//        );
//
//        RiskAssessmentRequest request =
//                new RiskAssessmentRequest(
//                        1L,
//                        1L,
//                        BigDecimal.valueOf(60),
//                        "High exposure",
//                        "Improve controls"
//                );
//
//        StepVerifier.create(
//                        controller.createAssessment(
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
//    void shouldGetAssessment() {
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
//
//        RiskAssessmentResponse response =
//                new RiskAssessmentResponse(
//                        1L,
//                        1L,
//                        1L,
//                        BigDecimal.valueOf(60),
//                        RiskLevel.HIGH,
//                        "Risk",
//                        "Recommendation",
//                        100L,
//                        LocalDateTime.now(),
//                        LocalDateTime.now()
//                );
//
//        when(service.getAssessment(
//                1L,
//                100L,
//                "BUSINESS_OWNER"
//        )).thenReturn(
//                Mono.just(response)
//        );
//
//        StepVerifier.create(
//                        controller.getAssessment(
//                                1L,
//                                authentication
//                        )
//                )
//                .expectNextMatches(
//                        result -> result.id().equals(1L)
//                )
//                .verifyComplete();
//    }
//
//    @Test
//    void shouldGetBusinessAssessments() {
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
//
//        RiskAssessmentResponse response =
//                new RiskAssessmentResponse(
//                        1L,
//                        1L,
//                        1L,
//                        BigDecimal.valueOf(60),
//                        RiskLevel.HIGH,
//                        "Risk",
//                        "Recommendation",
//                        100L,
//                        LocalDateTime.now(),
//                        LocalDateTime.now()
//                );
//
//        when(service.getByBusiness(
//                1L,
//                100L,
//                "BUSINESS_OWNER"
//        )).thenReturn(
//                Flux.just(response)
//        );
//
//        StepVerifier.create(
//                        controller.getByBusiness(
//                                1L,
//                                authentication
//                        )
//                )
//                .expectNextCount(1)
//                .verifyComplete();
//    }
//}
//



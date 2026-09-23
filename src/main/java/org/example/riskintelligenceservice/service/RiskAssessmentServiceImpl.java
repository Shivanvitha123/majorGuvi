package org.example.riskintelligenceservice.service;


import lombok.RequiredArgsConstructor;
import org.example.riskintelligenceservice.dto.RiskAssessmentRequest;
import org.example.riskintelligenceservice.dto.RiskAssessmentResponse;
import org.example.riskintelligenceservice.entity.RiskAssessment;
import org.example.riskintelligenceservice.exception.RiskAccessDeniedException;
import org.example.riskintelligenceservice.exception.RiskAssessmentNotFoundException;
import org.example.riskintelligenceservice.model.RiskLevel;
import org.example.riskintelligenceservice.repository.RiskAssessmentRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RiskAssessmentServiceImpl
        implements RiskAssessmentService {

    private final RiskAssessmentRepository repository;

    @Override
    public Mono<RiskAssessmentResponse> createAssessment(
            RiskAssessmentRequest request,
            Long userId,
            String role) {

        RiskLevel level =
                calculateRiskLevel(
                        request.riskScore()
                );

        RiskAssessment assessment =
                RiskAssessment.builder()
                        .businessId(request.businessId())
                        .policyId(request.policyId())
                        .ownerId(userId)
                        .riskScore(request.riskScore())
                        .riskLevel(level)
                        .riskFactors(request.riskFactors())
                        .recommendation(
                                request.recommendation()
                        )
                        .assessedBy(userId)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

        return repository
                .save(assessment)
                .map(this::toResponse);
    }

    @Override
    public Mono<RiskAssessmentResponse> getAssessment(
            Long id,
            Long userId,
            String role) {

        return repository
                .findById(id)
                .switchIfEmpty(
                        Mono.error(
                                new RiskAssessmentNotFoundException(id)
                        )
                )
                .flatMap(assessment -> {

                    if (isStaff(role) ||
                            assessment.getOwnerId().equals(userId)) {

                        return Mono.just(
                                toResponse(assessment)
                        );
                    }

                    return Mono.error(
                            new RiskAccessDeniedException()
                    );
                });
    }

    @Override
    public Flux<RiskAssessmentResponse> getByBusiness(
            Long businessId,
            Long userId,
            String role) {

        if (isStaff(role)) {

            return repository
                    .findByBusinessId(businessId)
                    .map(this::toResponse);
        }

        return repository
                .findByBusinessId(businessId)
                .filter(
                        assessment ->
                                assessment
                                        .getOwnerId()
                                        .equals(userId)
                )
                .map(this::toResponse);
    }

    private boolean isStaff(String role) {

        return "ADMIN".equals(role)
                || "UNDERWRITER".equals(role)
                || "RISK_ENGINEER".equals(role);
    }

    private RiskLevel calculateRiskLevel(
            java.math.BigDecimal score) {

        double value = score.doubleValue();

        if (value < 25) {
            return RiskLevel.LOW;
        }

        if (value < 50) {
            return RiskLevel.MEDIUM;
        }

        if (value < 75) {
            return RiskLevel.HIGH;
        }

        return RiskLevel.CRITICAL;
    }

    private RiskAssessmentResponse toResponse(
            RiskAssessment assessment) {

        return new RiskAssessmentResponse(
                assessment.getId(),
                assessment.getBusinessId(),
                assessment.getPolicyId(),
                assessment.getRiskScore(),
                assessment.getRiskLevel(),
                assessment.getRiskFactors(),
                assessment.getRecommendation(),
                assessment.getAssessedBy(),
                assessment.getCreatedAt(),
                assessment.getUpdatedAt()
        );
    }
}












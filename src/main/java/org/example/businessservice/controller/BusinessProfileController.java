package org.example.businessservice.controller;


import jakarta.validation.Valid;
import org.example.businessservice.dto.BusinessProfileResponse;
import org.example.businessservice.dto.BusinessSummaryResponse;
import org.example.businessservice.dto.CreateBusinessProfileRequest;
import org.example.businessservice.dto.UpdateBusinessProfileRequest;
import org.example.businessservice.service.BusinessProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/business")
public class BusinessProfileController {

    private final BusinessProfileService businessProfileService;

    public BusinessProfileController(
            BusinessProfileService businessProfileService
    ) {
        this.businessProfileService =
                businessProfileService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<BusinessProfileResponse> createBusinessProfile(
            @Valid @RequestBody CreateBusinessProfileRequest request,
            Authentication authentication
    ) {

        Long userId =
                Long.valueOf(authentication.getName());

        return businessProfileService
                .createBusinessProfile(
                        userId,
                        request
                );
    }

    @GetMapping("/me")
    public Flux<BusinessProfileResponse> getMyBusinessProfiles(
            Authentication authentication
    ) {

        Long userId =
                Long.valueOf(authentication.getName());

        return businessProfileService
                .getMyBusinessProfiles(userId);
    }

    @GetMapping("/{businessId}")
    public Mono<BusinessProfileResponse> getBusinessProfile(
            @PathVariable Long businessId,
            Authentication authentication
    ) {

        Long userId =
                Long.valueOf(authentication.getName());

        String role =
                authentication
                        .getAuthorities()
                        .stream()
                        .findFirst()
                        .map(authority ->
                                authority.getAuthority()
                                        .replace("ROLE_", "")
                        )
                        .orElse("");

        return businessProfileService
                .getBusinessProfile(
                        businessId,
                        userId,
                        role
                );
    }

    @PutMapping("/{businessId}")
    public Mono<BusinessProfileResponse> updateBusinessProfile(
            @PathVariable Long businessId,
            @Valid @RequestBody UpdateBusinessProfileRequest request,
            Authentication authentication
    ) {

        Long userId =
                Long.valueOf(authentication.getName());

        return businessProfileService
                .updateBusinessProfile(
                        businessId,
                        userId,
                        request
                );
    }

    @GetMapping
    public Flux<BusinessSummaryResponse> getAllBusinessProfiles(
            Authentication authentication
    ) {

        return businessProfileService
                .getAllBusinessProfiles();
    }
}


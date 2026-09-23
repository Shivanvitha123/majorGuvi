package org.example.notificationauditservice.controller;


import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.example.notificationauditservice.dto.AuditResponse;
import org.example.notificationauditservice.dto.CreateAuditRequest;
import org.example.notificationauditservice.service.AuditService;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService service;

    @PostMapping
    public Mono<AuditResponse> create(
            @Valid @RequestBody CreateAuditRequest request
    ) {
        return service.create(request);
    }

    @GetMapping
    public Flux<AuditResponse> getAll(
            Authentication authentication
    ) {

        return service.getAll();
    }
}


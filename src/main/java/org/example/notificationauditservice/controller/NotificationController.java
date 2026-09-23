package org.example.notificationauditservice.controller;


import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.example.notificationauditservice.dto.CreateNotificationRequest;
import org.example.notificationauditservice.dto.NotificationResponse;
import org.example.notificationauditservice.service.NotificationService;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService service;

    @PostMapping
    public Mono<NotificationResponse> create(
            @Valid @RequestBody CreateNotificationRequest request
    ) {
        return service.create(request);
    }

    @GetMapping
    public Flux<NotificationResponse> getMine(
            Authentication authentication
    ) {

        Long userId =
                Long.valueOf(authentication.getName());

        return service.getForUser(userId);
    }

    @GetMapping("/unread")
    public Flux<NotificationResponse> getUnread(
            Authentication authentication
    ) {

        Long userId =
                Long.valueOf(authentication.getName());

        return service.getUnread(userId);
    }

    @PatchMapping("/{id}/read")
    public Mono<NotificationResponse> markRead(
            @PathVariable Long id,
            Authentication authentication
    ) {

        Long userId =
                Long.valueOf(authentication.getName());

        return service.markRead(id, userId);
    }
}


package org.example.notificationauditservice.controller;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.example.notificationauditservice.dto.EventRequest;
import org.example.notificationauditservice.service.EventService;

import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService service;

    @PostMapping
    public Mono<Void> process(
            @Valid @RequestBody EventRequest request
    ) {
        return service.process(request);
    }
}

package org.example.claimrecoveryservice.client;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class NotificationAuditClient {

    private final WebClient.Builder webClientBuilder;

    public Mono<Void> publish(
            Long actorUserId,
            String actorRole,
            String action,
            String entityType,
            Long entityId,
            String description,
            Long notificationUserId,
            String notificationType,
            String notificationTitle,
            String notificationMessage
    ) {

        Map<String, Object> body = Map.of(
                "actorUserId", actorUserId,
                "actorRole", actorRole,
                "action", action,
                "entityType", entityType,
                "entityId", entityId,
                "description", description,
                "notificationUserId", notificationUserId,
                "notificationType", notificationType,
                "notificationTitle", notificationTitle,
                "notificationMessage", notificationMessage
        );

        return webClientBuilder.build()
                .post()
                .uri("http://NOTIFICATION-AUDIT-SERVICE/api/events")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Void.class)
                .onErrorResume(error -> Mono.empty());
    }
}


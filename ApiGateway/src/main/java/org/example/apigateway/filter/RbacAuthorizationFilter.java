package org.example.apigateway.filter;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

@Component
public class RbacAuthorizationFilter implements GlobalFilter, Ordered {

    private static final Logger log =
            LoggerFactory.getLogger(RbacAuthorizationFilter.class);

    private static final Map<String, Set<String>> ROLE_PERMISSIONS = Map.of(

            "/api/business",
            Set.of(
                    "ADMIN",
                    "BUSINESS_OWNER",
                    "UNDERWRITER",
                    "RISK_ENGINEER"
            ),

            "/api/policies",
            Set.of(
                    "ADMIN",
                    "BUSINESS_OWNER",
                    "UNDERWRITER",
                    "RISK_ENGINEER"
            ),

            "/api/risk",
            Set.of(
                    "ADMIN",
                    "BUSINESS_OWNER",
                    "UNDERWRITER",
                    "RISK_ENGINEER"
            ),

            "/api/claims",
            Set.of(
                    "ADMIN",
                    "BUSINESS_OWNER",
                    "CLAIMS_ADJUSTER"
            )
    );

    @Override
    public Mono<Void> filter(
            ServerWebExchange exchange,
            GatewayFilterChain chain) {

        String path = exchange
                .getRequest()
                .getURI()
                .getPath();

        /*
         * Public endpoints.
         */
        if (isPublicPath(path)) {
            return chain.filter(exchange);
        }

        /*
         * All protected endpoints must have a role
         * supplied by the JWT authentication filter.
         */
        String userRole =
                exchange.getRequest()
                        .getHeaders()
                        .getFirst("X-User-Role");

        if (userRole == null || userRole.isBlank()) {

            log.warn(
                    "RBAC denied request because role is missing: {}",
                    path
            );

            return forbidden(
                    exchange,
                    "User role is missing"
            );
        }

        String normalizedRole =
                userRole.trim().toUpperCase();

        /*
         * /api/auth/me
         *
         * Any authenticated role can access their own
         * current-user information.
         */
        if (path.equals("/api/auth/me")) {

            log.debug(
                    "Authenticated user allowed to access /api/auth/me, role={}",
                    normalizedRole
            );

            return chain.filter(exchange);
        }

        /*
         * /api/auth/users
         *
         * Only ADMIN can access the user list.
         */
        if (path.equals("/api/auth/users")) {

            if ("ADMIN".equals(normalizedRole)) {

                log.debug(
                        "ADMIN allowed to access {}",
                        path
                );

                return chain.filter(exchange);
            }

            log.warn(
                    "RBAC denied role={} path={}",
                    normalizedRole,
                    path
            );

            return forbidden(
                    exchange,
                    "Only ADMIN users can access this resource"
            );
        }

        /*
         * /api/auth/users/{id}
         *
         * Only ADMIN can access individual user details.
         */
        if (path.startsWith("/api/auth/users/")) {

            if ("ADMIN".equals(normalizedRole)) {

                log.debug(
                        "ADMIN allowed to access {}",
                        path
                );

                return chain.filter(exchange);
            }

            log.warn(
                    "RBAC denied role={} path={}",
                    normalizedRole,
                    path
            );

            return forbidden(
                    exchange,
                    "Only ADMIN users can access this resource"
            );
        }

        /*
         * Business-service RBAC.
         */
        String resource = findResource(path);

        /*
         * Deny by default if the resource has not been
         * explicitly configured.
         */
        if (resource == null) {

            log.warn(
                    "RBAC denied undefined resource: {}",
                    path
            );

            return forbidden(
                    exchange,
                    "Access to this resource is not permitted"
            );
        }

        Set<String> allowedRoles =
                ROLE_PERMISSIONS.get(resource);

        if (!allowedRoles.contains(normalizedRole)) {

            log.warn(
                    "RBAC denied role={} path={}",
                    normalizedRole,
                    path
            );

            return forbidden(
                    exchange,
                    "You do not have permission to access this resource"
            );
        }

        log.debug(
                "RBAC allowed role={} path={}",
                normalizedRole,
                path
        );

        return chain.filter(exchange);
    }

    private String findResource(String path) {

        return ROLE_PERMISSIONS.keySet()
                .stream()
                .filter(path::startsWith)
                .findFirst()
                .orElse(null);
    }

    private boolean isPublicPath(String path) {

        return path.equals("/api/auth/login")
                || path.equals("/api/auth/register")
                || path.equals("/actuator/health")
                || path.equals("/actuator/info")
                || path.startsWith("/fallback");
    }

    private Mono<Void> forbidden(
            ServerWebExchange exchange,
            String message) {

        exchange.getResponse()
                .setStatusCode(HttpStatus.FORBIDDEN);

        exchange.getResponse()
                .getHeaders()
                .setContentType(MediaType.APPLICATION_JSON);

        String path =
                exchange.getRequest()
                        .getURI()
                        .getPath();

        String response = """
                {
                    "status": 403,
                    "error": "Forbidden",
                    "message": "%s",
                    "path": "%s"
                }
                """.formatted(
                escapeJson(message),
                escapeJson(path)
        );

        byte[] bytes =
                response.getBytes(StandardCharsets.UTF_8);

        return exchange.getResponse()
                .writeWith(
                        Mono.just(
                                exchange.getResponse()
                                        .bufferFactory()
                                        .wrap(bytes)
                        )
                );
    }

    private String escapeJson(String value) {

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }

    @Override
    public int getOrder() {
        return -50;
    }
}

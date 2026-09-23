package org.example.identityservice.security;

import io.jsonwebtoken.Claims;
import org.example.identityservice.entity.User;
import org.example.identityservice.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    private final String secret =
            "RiskTwinDevelopmentSecretKeyForJWTAuthentication2026";

    @BeforeEach
    void setUp() {

        jwtService = new JwtService(
                secret,
                3600000
        );
    }

    @Test
    void shouldGenerateAndValidateToken() {

        User user = User.builder()
                .id(10L)
                .name("Admin")
                .email("admin@risktwin.com")
                .role(Role.ADMIN)
                .build();

        String token = jwtService.generateToken(user);

        assertNotNull(token);

        Claims claims =
                jwtService.validateToken(token);

        assertEquals("10", claims.getSubject());

        assertEquals(
                "ADMIN",
                claims.get("role", String.class)
        );

        assertEquals(
                "admin@risktwin.com",
                claims.get("email", String.class)
        );
    }
}


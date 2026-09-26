package org.example.apigateway.security;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;

    private final String secret =
            "RiskTwinNexusJwtSecretKeyForDevelopmentOnly123456789";

    @BeforeEach
    void setUp() {

        jwtService = new JwtService();

        ReflectionTestUtils.setField(
                jwtService,
                "secret",
                secret
        );
    }

    @Test
    void shouldValidateValidToken() {

        SecretKey key =
                Keys.hmacShaKeyFor(
                        secret.getBytes(StandardCharsets.UTF_8)
                );

        String token =
                Jwts.builder()
                        .subject("user-101")
                        .claim("role", "USER")
                        .issuedAt(new Date())
                        .expiration(
                                new Date(
                                        System.currentTimeMillis()
                                                + 60_000
                                )
                        )
                        .signWith(key)
                        .compact();

        assertTrue(
                jwtService.isValid(token)
        );

        assertEquals(
                "user-101",
                jwtService.extractUserId(token)
        );

        assertEquals(
                "USER",
                jwtService.extractRole(token)
        );
    }

    @Test
    void shouldRejectInvalidToken() {

        assertFalse(
                jwtService.isValid(
                        "invalid.jwt.token"
                )
        );
    }
}



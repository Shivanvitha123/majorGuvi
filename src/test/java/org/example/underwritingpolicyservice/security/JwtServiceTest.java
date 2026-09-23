package org.example.underwritingpolicyservice.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private static final String SECRET =
            "RiskTwinNexusJwtSecretKeyForDevelopmentOnly123456789";

    private JwtService jwtService;

    private final SecretKey secretKey =
            Keys.hmacShaKeyFor(
                    SECRET.getBytes(StandardCharsets.UTF_8)
            );

    @BeforeEach
    void setUp() {

        jwtService = new JwtService(SECRET);
    }

    @Test
    void validTokenShouldBeAccepted() {

        String token = createToken();

        assertTrue(
                jwtService.isValidToken(token)
        );
    }

    @Test
    void userIdShouldBeExtracted() {

        String token = createToken();

        assertEquals(
                "100",
                jwtService.extractUserId(token)
        );
    }

    @Test
    void roleShouldBeExtracted() {

        String token = createToken();

        assertEquals(
                "UNDERWRITER",
                jwtService.extractRole(token)
        );
    }

    @Test
    void emailShouldBeExtracted() {

        String token = createToken();

        assertEquals(
                "underwriter@risktwin.com",
                jwtService.extractEmail(token)
        );
    }

    @Test
    void invalidTokenShouldBeRejected() {

        assertFalse(
                jwtService.isValidToken("invalid-token")
        );
    }

    @Test
    void expiredTokenShouldBeRejected() {

        String token =
                Jwts.builder()
                        .subject("100")
                        .claim(
                                "role",
                                "UNDERWRITER"
                        )
                        .claim(
                                "email",
                                "underwriter@risktwin.com"
                        )
                        .issuedAt(
                                new Date(
                                        System.currentTimeMillis()
                                                - 10_000
                                )
                        )
                        .expiration(
                                new Date(
                                        System.currentTimeMillis()
                                                - 1_000
                                )
                        )
                        .signWith(secretKey)
                        .compact();

        assertFalse(
                jwtService.isValidToken(token)
        );
    }

    private String createToken() {

        return Jwts.builder()
                .subject("100")
                .claim(
                        "role",
                        "UNDERWRITER"
                )
                .claim(
                        "email",
                        "underwriter@risktwin.com"
                )
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + 3_600_000
                        )
                )
                .signWith(secretKey)
                .compact();
    }
}


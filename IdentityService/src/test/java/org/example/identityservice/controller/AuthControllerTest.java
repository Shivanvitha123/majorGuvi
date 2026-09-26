package org.example.identityservice.controller;

import org.example.identityservice.dto.AuthResponse;
import org.example.identityservice.dto.LoginRequest;
import org.example.identityservice.dto.RegisterRequest;
import org.example.identityservice.model.Role;
import org.example.identityservice.security.JwtAuthenticationWebFilter;
import org.example.identityservice.security.JwtService;
import org.example.identityservice.service.AuthService;
import org.example.identityservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
//
//@WebFluxTest(AuthController.class)
//@Import(TestSecurityConfig.class)
//class AuthControllerTest {
//
//    @Autowired
//    private WebTestClient webTestClient;
//
//    @MockitoBean
//    private AuthService authService;
//
//    @MockitoBean
//    private UserService userService;
//
//    @MockitoBean
//    private JwtService jwtService;
//
//    @MockitoBean
//    private JwtAuthenticationWebFilter jwtAuthenticationWebFilter;
//
//    @Test
//    void registerShouldReturnCreated() {
//
//        AuthResponse response = new AuthResponse(
//                "jwt-token",
//                1L,
//                "Test User",
//                "test@example.com",
//                Role.BUSINESS_OWNER
//        );
//
//        when(authService.register(any(RegisterRequest.class)))
//                .thenReturn(Mono.just(response));
//
//        RegisterRequest request = new RegisterRequest(
//                "Test User",
//                "test@example.com",
//                "Password@123"
//        );
//
//        webTestClient.post()
//                .uri("/api/auth/register")
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(request)
//                .exchange()
//                .expectStatus().isCreated()
//                .expectBody()
//                .jsonPath("$.role").isEqualTo("BUSINESS_OWNER");
//    }
//
//    @Test
//    void loginShouldReturnToken() {
//
//        AuthResponse response = new AuthResponse(
//                "jwt-token",
//                1L,
//                "Test User",
//                "test@example.com",
//                Role.BUSINESS_OWNER
//        );
//
//        when(authService.login(any(LoginRequest.class)))
//                .thenReturn(Mono.just(response));
//
//        LoginRequest request = new LoginRequest(
//                "test@example.com",
//                "Password@123"
//        );
//
//        webTestClient.post()
//                .uri("/api/auth/login")
//                .contentType(MediaType.APPLICATION_JSON)
//                .bodyValue(request)
//                .exchange()
//                .expectStatus().isOk()
//                .expectBody()
//                .jsonPath("$.token").isEqualTo("jwt-token");
//    }
//
//    @Test
//    @WithMockUser(
//            username = "1",
//            roles = "ADMIN"
//    )
//    void adminShouldAccessUsers() {
//
//        when(userService.getAllUsers())
//                .thenReturn(Flux.empty());
//
//        webTestClient.get()
//                .uri("/api/auth/users")
//                .exchange()
//                .expectStatus()
//                .isOk();
//    }
//}
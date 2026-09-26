package org.example.identityservice.service;

import org.example.identityservice.dto.LoginRequest;
import org.example.identityservice.dto.RegisterRequest;
import org.example.identityservice.entity.User;
import org.example.identityservice.model.Role;
import org.example.identityservice.repository.UserRepository;
import org.example.identityservice.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private BCryptPasswordEncoder realEncoder;

    @BeforeEach
    void setUp() {
        realEncoder = new BCryptPasswordEncoder();
    }

    @Test
    void registerShouldCreateBusinessOwner() {

        RegisterRequest request = new RegisterRequest(
                "Business Owner",
                "owner@example.com",
                "Password@123"
        );

        when(userRepository.existsByEmail("owner@example.com"))
                .thenReturn(Mono.just(false));

        when(passwordEncoder.encode("Password@123"))
                .thenReturn("encoded-password");

        User savedUser = User.builder()
                .id(1L)
                .name("Business Owner")
                .email("owner@example.com")
                .password("encoded-password")
                .role(Role.BUSINESS_OWNER)
                .active(true)
                .build();

        when(userRepository.save(any(User.class)))
                .thenReturn(Mono.just(savedUser));

        when(jwtService.generateToken(any(User.class)))
                .thenReturn("jwt-token");

        StepVerifier.create(
                        authService.register(request)
                )
                .assertNext(response -> {
                    assert response.role() == Role.BUSINESS_OWNER;
                    assert response.token().equals("jwt-token");
                })
                .verifyComplete();

        verify(userRepository).save(any(User.class));
        verify(jwtService).generateToken(any(User.class));
    }

    @Test
    void loginShouldAcceptCorrectPassword() {

        String rawPassword = "Password@123";
        String encodedPassword = realEncoder.encode(rawPassword);

        User user = User.builder()
                .id(1L)
                .name("Business Owner")
                .email("owner@example.com")
                .password(encodedPassword)
                .role(Role.BUSINESS_OWNER)
                .active(true)
                .build();

        when(userRepository.findByEmail("owner@example.com"))
                .thenReturn(Mono.just(user));

        when(passwordEncoder.matches(
                rawPassword,
                encodedPassword
        )).thenReturn(true);

        when(jwtService.generateToken(user))
                .thenReturn("jwt-token");

        LoginRequest request = new LoginRequest(
                "owner@example.com",
                rawPassword
        );

        StepVerifier.create(
                        authService.login(request)
                )
//                .assertNext(response ->
//        assert response.token().equals("jwt-token")
//                )
//                .verifyComplete();
                        .assertNext(response -> {
                            assert response.token().equals("jwt-token");
                        })
                        .verifyComplete();


        verify(jwtService).generateToken(user);
    }

    @Test
    void registerShouldFailWhenEmailAlreadyExists() {

        RegisterRequest request = new RegisterRequest(
                "Business Owner",
                "owner@example.com",
                "Password@123"
        );

        when(userRepository.existsByEmail("owner@example.com"))
                .thenReturn(Mono.just(true));

        StepVerifier.create(
                        authService.register(request)
                )
                .expectError()
                .verify();

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void loginShouldFailWhenPasswordIsInvalid() {

        User user = User.builder()
                .id(1L)
                .name("Business Owner")
                .email("owner@example.com")
                .password("encoded-password")
                .role(Role.BUSINESS_OWNER)
                .active(true)
                .build();

        when(userRepository.findByEmail("owner@example.com"))
                .thenReturn(Mono.just(user));

        when(passwordEncoder.matches(
                "WrongPassword",
                "encoded-password"
        )).thenReturn(false);

        LoginRequest request = new LoginRequest(
                "owner@example.com",
                "WrongPassword"
        );

        StepVerifier.create(
                        authService.login(request)
                )
                .expectError()
                .verify();
    }
}
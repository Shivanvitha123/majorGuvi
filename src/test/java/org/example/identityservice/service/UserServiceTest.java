package org.example.identityservice.service;


import org.example.identityservice.entity.User;
import org.example.identityservice.model.Role;
import org.example.identityservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void getUserByIdShouldReturnUser() {

        User user = User.builder()
                .id(1L)
                .name("Admin")
                .email("admin@risktwin.com")
                .role(Role.ADMIN)
                .active(true)
                .build();

        when(userRepository.findById(1L))
                .thenReturn(Mono.just(user));

        StepVerifier.create(
                        userService.getUserById(1L)
                )
                .assertNext(response -> {

                    assert response.id().equals(1L);
                    assert response.role() == Role.ADMIN;
                })
                .verifyComplete();
    }
}


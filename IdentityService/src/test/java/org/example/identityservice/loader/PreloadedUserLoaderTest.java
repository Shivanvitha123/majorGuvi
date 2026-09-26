package org.example.identityservice.loader;


import org.example.identityservice.config.SeedUserProperties;
import org.example.identityservice.entity.User;
import org.example.identityservice.model.Role;
import org.example.identityservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PreloadedUserLoaderTest {

    @Mock
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    @Mock
    private SeedUserProperties seedUserProperties;

    @Test
    void existingUserShouldNotBeCreatedAgain() {

        SeedUserProperties.SeedUser seedUser =
                new SeedUserProperties.SeedUser();

        seedUser.setName("Admin");
        seedUser.setEmail("admin@risktwin.com");
        seedUser.setPassword("Admin@123");
        seedUser.setRole(Role.ADMIN);

        when(seedUserProperties.getPreloadedUsers())
                .thenReturn(java.util.List.of(seedUser));

        User existingUser = User.builder()
                .id(1L)
                .email("admin@risktwin.com")
                .role(Role.ADMIN)
                .build();

        when(userRepository.findByEmail("admin@risktwin.com"))
                .thenReturn(Mono.just(existingUser));

        PreloadedUserLoader loader =
                new PreloadedUserLoader(
                        userRepository,
                        passwordEncoder,
                        seedUserProperties
                );

        loader.run();

        verify(userRepository, never())
                .save(any(User.class));
    }
}



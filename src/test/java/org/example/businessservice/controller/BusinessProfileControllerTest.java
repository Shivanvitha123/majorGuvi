package org.example.businessservice.controller;


//import com.risktwin.business.dto.BusinessProfileResponse;
//import com.risktwin.business.dto.BusinessSummaryResponse;
//import com.risktwin.business.dto.CreateBusinessProfileRequest;
//import com.risktwin.business.dto.UpdateBusinessProfileRequest;
//import com.risktwin.business.model.BusinessStatus;
//import com.risktwin.business.model.BusinessType;
//import com.risktwin.business.service.BusinessProfileService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//import reactor.test.StepVerifier;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class BusinessProfileControllerTest {
//
//    @Mock
//    private BusinessProfileService service;
//
//    private BusinessProfileController controller;
//
//    private Authentication ownerAuthentication;
//
//    @BeforeEach
//    void setUp() {
//
//        controller =
//                new BusinessProfileController(service);
//
//        ownerAuthentication =
//                new UsernamePasswordAuthenticationToken(
//                        "5",
//                        null,
//                        List.of()
//                );
//    }
//
//    @Test
//    void shouldCreateBusinessProfile() {
//
//        CreateBusinessProfileRequest request =
//                new CreateBusinessProfileRequest(
//                        "ABC Insurance",
//                        "REG-1001",
//                        BusinessType.PRIVATE_LIMITED,
//                        "Insurance",
//                        "ABC Street",
//                        "Hyderabad",
//                        "Telangana",
//                        "500001",
//                        "India",
//                        "contact@abcinsurance.com",
//                        "9876543210",
//                        BigDecimal.valueOf(5000000),
//                        50,
//                        LocalDate.of(2020, 1, 1)
//                );
//
//        BusinessProfileResponse response =
//                createResponse();
//
//        when(service.createBusinessProfile(
//                5L,
//                request
//        )).thenReturn(Mono.just(response));
//
//        StepVerifier.create(
//                        controller.createBusinessProfile(
//                                request,
//                                ownerAuthentication
//                        )
//                )
//                .assertNext(result ->
//        assert result.id().equals(1L)
//        )
//        .verifyComplete();
//
//        verify(service)
//                .createBusinessProfile(
//                        5L,
//                        request
//                );
//    }
//
//    @Test
//    void shouldGetMyBusinesses() {
//
//        when(service.getMyBusinessProfiles(5L))
//                .thenReturn(
//                        Flux.just(createResponse())
//                );
//
//        StepVerifier.create(
//                        controller.getMyBusinessProfiles(
//                                ownerAuthentication
//                        )
//                )
//                .assertNext(result ->
//        assert result.ownerId().equals(5L)
//        )
//        .verifyComplete();
//
//        verify(service)
//                .getMyBusinessProfiles(5L);
//    }
//
//    @Test
//    void shouldGetBusinessById() {
//
//        Authentication adminAuthentication =
//                new UsernamePasswordAuthenticationToken(
//                        "99",
//                        null,
//                        List.of(
//                                () -> "ROLE_ADMIN"
//                        )
//                );
//
//        when(service.getBusinessProfile(
//                1L,
//                99L,
//                "ADMIN"
//        )).thenReturn(
//                Mono.just(createResponse())
//        );
//
//        StepVerifier.create(
//                        controller.getBusinessProfile(
//                                1L,
//                                adminAuthentication
//                        )
//                )
//                .assertNext(result ->
//        assert result.id().equals(1L)
//        )
//        .verifyComplete();
//
//        verify(service)
//                .getBusinessProfile(
//                        1L,
//                        99L,
//                        "ADMIN"
//                );
//    }
//
//    @Test
//    void shouldUpdateBusinessProfile() {
//
//        UpdateBusinessProfileRequest request =
//                new UpdateBusinessProfileRequest(
//                        "Updated Insurance",
//                        BusinessType.PRIVATE_LIMITED,
//                        "Insurance",
//                        "Updated Address",
//                        "Hyderabad",
//                        "Telangana",
//                        "500001",
//                        "India",
//                        "updated@abcinsurance.com",
//                        "9876543210",
//                        BigDecimal.valueOf(7500000),
//                        75,
//                        LocalDate.of(2020, 1, 1)
//                );
//
//        when(service.updateBusinessProfile(
//                1L,
//                5L,
//                request
//        )).thenReturn(
//                Mono.just(createResponse())
//        );
//
//        StepVerifier.create(
//                        controller.updateBusinessProfile(
//                                1L,
//                                request,
//                                ownerAuthentication
//                        )
//                )
//                .expectNextCount(1)
//                .verifyComplete();
//
//        verify(service)
//                .updateBusinessProfile(
//                        1L,
//                        5L,
//                        request
//                );
//    }
//
//    @Test
//    void shouldGetAllBusinesses() {
//
//        BusinessSummaryResponse summary =
//                new BusinessSummaryResponse(
//                        1L,
//                        5L,
//                        "ABC Insurance",
//                        "REG-1001",
//                        BusinessType.PRIVATE_LIMITED,
//                        "Insurance",
//                        BusinessStatus.ACTIVE
//                );
//
//        Authentication adminAuthentication =
//                new UsernamePasswordAuthenticationToken(
//                        "99",
//                        null,
//                        List.of(
//                                () -> "ROLE_ADMIN"
//                        )
//                );
//
//        when(service.getAllBusinessProfiles())
//                .thenReturn(Flux.just(summary));
//
//        StepVerifier.create(
//                        controller.getAllBusinessProfiles(
//                                adminAuthentication
//                        )
//                )
//                .assertNext(result ->
//        assert result.id().equals(1L)
//        )
//        .verifyComplete();
//
//        verify(service)
//                .getAllBusinessProfiles();
//    }
//
//    private BusinessProfileResponse createResponse() {
//
//        return new BusinessProfileResponse(
//                1L,
//                5L,
//                "ABC Insurance",
//                "REG-1001",
//                BusinessType.PRIVATE_LIMITED,
//                "Insurance",
//                "ABC Street",
//                "Hyderabad",
//                "Telangana",
//                "500001",
//                "India",
//                "contact@abcinsurance.com",
//                "9876543210",
//                BigDecimal.valueOf(5000000),
//                50,
//                LocalDate.of(2020, 1, 1),
//                BusinessStatus.ACTIVE,
//                LocalDateTime.now(),
//                LocalDateTime.now()
//        );
//    }
//}

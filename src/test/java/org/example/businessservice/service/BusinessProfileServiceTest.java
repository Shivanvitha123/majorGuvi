package org.example.businessservice.service;

import org.example.businessservice.dto.CreateBusinessProfileRequest;
import org.example.businessservice.dto.UpdateBusinessProfileRequest;
import org.example.businessservice.entity.BusinessProfile;
import org.example.businessservice.exception.BusinessAccessDeniedException;
import org.example.businessservice.exception.BusinessProfileAlreadyExistsException;
import org.example.businessservice.model.BusinessStatus;
import org.example.businessservice.model.BusinessType;
import org.example.businessservice.repository.BusinessProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BusinessProfileServiceTest {

    @Mock
    private BusinessProfileRepository repository;

    private BusinessProfileService service;

    @BeforeEach
    void setUp() {
        service = new BusinessProfileServiceImpl(repository);
    }

    @Test
    void shouldCreateBusinessProfile() {

        CreateBusinessProfileRequest request = createRequest();

        when(repository.existsByRegistrationNumber("REG-1001"))
                .thenReturn(Mono.just(false));

        when(repository.save(any(BusinessProfile.class)))
                .thenAnswer(invocation -> {
                    BusinessProfile business = invocation.getArgument(0);
                    business.setId(1L);
                    return Mono.just(business);
                });

        StepVerifier.create(
                        service.createBusinessProfile(5L, request)
                )
                .assertNext(response -> {
                    assertEquals(1L, response.id());
                    assertEquals(5L, response.ownerId());
                    assertEquals("ABC Insurance", response.businessName());
                    assertEquals("REG-1001", response.registrationNumber());
                    assertEquals(BusinessStatus.ACTIVE, response.status());
                })
                .verifyComplete();

        verify(repository).existsByRegistrationNumber("REG-1001");
        verify(repository).save(any(BusinessProfile.class));
    }

    @Test
    void shouldRejectDuplicateRegistrationNumber() {

        when(repository.existsByRegistrationNumber("REG-1001"))
                .thenReturn(Mono.just(true));

        StepVerifier.create(
                        service.createBusinessProfile(
                                5L,
                                createRequest()
                        )
                )
                .expectError(BusinessProfileAlreadyExistsException.class)
                .verify();

        verify(repository, never())
                .save(any(BusinessProfile.class));
    }

    @Test
    void businessOwnerShouldAccessOwnBusiness() {

        BusinessProfile business = createBusiness(1L, 5L);

        when(repository.findById(1L))
                .thenReturn(Mono.just(business));

        StepVerifier.create(
                        service.getBusinessProfile(
                                1L,
                                5L,
                                "BUSINESS_OWNER"
                        )
                )
                .assertNext(response ->
                        assertEquals(5L, response.ownerId())
                )
                .verifyComplete();
    }

    @Test
    void businessOwnerShouldNotAccessAnotherOwnersBusiness() {

        BusinessProfile business = createBusiness(1L, 5L);

        when(repository.findById(1L))
                .thenReturn(Mono.just(business));

        StepVerifier.create(
                        service.getBusinessProfile(
                                1L,
                                10L,
                                "BUSINESS_OWNER"
                        )
                )
                .expectError(BusinessAccessDeniedException.class)
                .verify();
    }

    @Test
    void adminShouldAccessBusiness() {

        BusinessProfile business = createBusiness(1L, 5L);

        when(repository.findById(1L))
                .thenReturn(Mono.just(business));

        StepVerifier.create(
                        service.getBusinessProfile(
                                1L,
                                999L,
                                "ADMIN"
                        )
                )
                .assertNext(response ->
                        assertEquals(1L, response.id())
                )
                .verifyComplete();
    }

    @Test
    void underwriterShouldAccessBusiness() {

        BusinessProfile business = createBusiness(1L, 5L);

        when(repository.findById(1L))
                .thenReturn(Mono.just(business));

        StepVerifier.create(
                        service.getBusinessProfile(
                                1L,
                                999L,
                                "UNDERWRITER"
                        )
                )
                .assertNext(response ->
                        assertNotNull(response)
                )
                .verifyComplete();
    }

    @Test
    void riskEngineerShouldAccessBusiness() {

        BusinessProfile business = createBusiness(1L, 5L);

        when(repository.findById(1L))
                .thenReturn(Mono.just(business));

        StepVerifier.create(
                        service.getBusinessProfile(
                                1L,
                                999L,
                                "RISK_ENGINEER"
                        )
                )
                .assertNext(response ->
                        assertNotNull(response)
                )
                .verifyComplete();
    }

    @Test
    void shouldReturnMyBusinesses() {

        BusinessProfile business = createBusiness(1L, 5L);

        when(repository.findByOwnerId(5L))
                .thenReturn(Flux.just(business));

        StepVerifier.create(
                        service.getMyBusinessProfiles(5L)
                )
                .assertNext(response ->
                        assertEquals(5L, response.ownerId())
                )
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenOwnerHasNoBusinesses() {

        when(repository.findByOwnerId(5L))
                .thenReturn(Flux.empty());

        StepVerifier.create(
                        service.getMyBusinessProfiles(5L)
                )
                .verifyComplete();
    }

    @Test
    void shouldUpdateOwnedBusiness() {

        BusinessProfile business = createBusiness(1L, 5L);

        when(repository.findByIdAndOwnerId(
                1L,
                5L
        )).thenReturn(Mono.just(business));

        when(repository.save(any(BusinessProfile.class)))
                .thenAnswer(invocation ->
                        Mono.just(invocation.getArgument(0))
                );

        UpdateBusinessProfileRequest request =
                new UpdateBusinessProfileRequest(
                        "Updated Insurance",
                        BusinessType.PRIVATE_LIMITED,
                        "Insurance",
                        "Updated Address",
                        "Hyderabad",
                        "Telangana",
                        "500001",
                        "India",
                        "updated@abcinsurance.com",
                        "9876543210",
                        new BigDecimal("7500000"),
                        75,
                        LocalDate.of(2020, 1, 1)
                );

        StepVerifier.create(
                        service.updateBusinessProfile(
                                1L,
                                5L,
                                request
                        )
                )
                .assertNext(response ->
                        assertEquals(
                                "Updated Insurance",
                                response.businessName()
                        )
                )
                .verifyComplete();

        verify(repository)
                .save(any(BusinessProfile.class));
    }

    @Test
    void shouldRejectUpdateWhenUserDoesNotOwnBusiness() {

        when(repository.findByIdAndOwnerId(
                1L,
                10L
        )).thenReturn(Mono.empty());

        StepVerifier.create(
                        service.updateBusinessProfile(
                                1L,
                                10L,
                                new UpdateBusinessProfileRequest(
                                        "Updated Insurance",
                                        BusinessType.PRIVATE_LIMITED,
                                        "Insurance",
                                        "Address",
                                        "Hyderabad",
                                        "Telangana",
                                        "500001",
                                        "India",
                                        "test@example.com",
                                        "9876543210",
                                        BigDecimal.valueOf(100000),
                                        10,
                                        LocalDate.of(2020, 1, 1)
                                )
                        )
                )
                .expectError(
                        BusinessAccessDeniedException.class
                )
                .verify();
    }

    private CreateBusinessProfileRequest createRequest() {

        return new CreateBusinessProfileRequest(
                "ABC Insurance",
                "REG-1001",
                BusinessType.PRIVATE_LIMITED,
                "Insurance",
                "ABC Street",
                "Hyderabad",
                "Telangana",
                "500001",
                "India",
                "contact@abcinsurance.com",
                "9876543210",
                new BigDecimal("5000000"),
                50,
                LocalDate.of(2020, 1, 1)
        );
    }

    private BusinessProfile createBusiness(
            Long id,
            Long ownerId
    ) {

        LocalDateTime now = LocalDateTime.now();

        return BusinessProfile.builder()
                .id(id)
                .ownerId(ownerId)
                .businessName("ABC Insurance")
                .registrationNumber("REG-1001")
                .businessType(BusinessType.PRIVATE_LIMITED)
                .industry("Insurance")
                .address("ABC Street")
                .city("Hyderabad")
                .state("Telangana")
                .postalCode("500001")
                .country("India")
                .contactEmail("contact@abcinsurance.com")
                .contactPhone("9876543210")
                .annualRevenue(new BigDecimal("5000000"))
                .employeeCount(50)
                .establishedDate(LocalDate.of(2020, 1, 1))
                .status(BusinessStatus.ACTIVE)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}
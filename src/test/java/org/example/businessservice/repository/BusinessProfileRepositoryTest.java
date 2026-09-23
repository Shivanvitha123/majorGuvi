package org.example.businessservice.repository;

import org.example.businessservice.entity.BusinessProfile;
import org.example.businessservice.model.BusinessStatus;
import org.example.businessservice.model.BusinessType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootTest(
        classes = BusinessProfileRepositoryTest.TestApplication.class
)
@ActiveProfiles("test")
class BusinessProfileRepositoryTest {

    @Autowired
    private BusinessProfileRepository repository;

    @Test
    void shouldSaveAndFindBusiness() {

        BusinessProfile business =
                BusinessProfile.builder()
                        .ownerId(5L)
                        .businessName("ABC Insurance")
                        .registrationNumber("TEST-001")
                        .businessType(
                                BusinessType.PRIVATE_LIMITED
                        )
                        .industry("Insurance")
                        .address("ABC Street")
                        .city("Hyderabad")
                        .state("Telangana")
                        .postalCode("500001")
                        .country("India")
                        .contactEmail(
                                "test@abcinsurance.com"
                        )
                        .contactPhone("9876543210")
                        .annualRevenue(
                                BigDecimal.valueOf(5000000)
                        )
                        .employeeCount(50)
                        .establishedDate(
                                LocalDate.of(2020, 1, 1)
                        )
                        .status(
                                BusinessStatus.ACTIVE
                        )
                        .createdAt(
                                LocalDateTime.now()
                        )
                        .updatedAt(
                                LocalDateTime.now()
                        )
                        .build();

        StepVerifier.create(
                        repository.save(business)
                )
                .assertNext(saved ->
                        org.junit.jupiter.api.Assertions.assertNotNull(
                                saved.getId()
                        )
                )
                .verifyComplete();

        StepVerifier.create(
                        repository.findByRegistrationNumber(
                                "TEST-001"
                        )
                )
                .assertNext(found ->
                        org.junit.jupiter.api.Assertions.assertEquals(
                                "ABC Insurance",
                                found.getBusinessName()
                        )
                )
                .verifyComplete();
    }

    @Test
    void shouldFindBusinessesByOwner() {

        BusinessProfile business =
                BusinessProfile.builder()
                        .ownerId(99L)
                        .businessName("Owner Test")
                        .registrationNumber("OWNER-001")
                        .businessType(
                                BusinessType.PRIVATE_LIMITED
                        )
                        .industry("Insurance")
                        .address("Address")
                        .city("Hyderabad")
                        .state("Telangana")
                        .postalCode("500001")
                        .country("India")
                        .contactEmail("owner@test.com")
                        .contactPhone("9876543210")
                        .status(BusinessStatus.ACTIVE)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();

        StepVerifier.create(
                        repository.save(business)
                                .thenMany(
                                        repository.findByOwnerId(99L)
                                )
                )
                .assertNext(found ->
                        org.junit.jupiter.api.Assertions.assertEquals(
                                99L,
                                found.getOwnerId()
                        )
                )
                .verifyComplete();
    }

    @SpringBootApplication(
            scanBasePackages = "com.risktwin.business"
    )
    static class TestApplication {
    }
}


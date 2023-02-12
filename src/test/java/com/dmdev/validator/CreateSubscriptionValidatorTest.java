package com.dmdev.validator;

import com.dmdev.dto.CreateSubscriptionDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.DisplayName.class)
@DisplayName("Subscription validation test")
class CreateSubscriptionValidatorTest {

    private final CreateSubscriptionValidator validator = CreateSubscriptionValidator.getInstance();

    @Test
    @DisplayName("All parameters are correct = validation pass")
    void shouldPassValidation() {
        CreateSubscriptionDto subscriptionDto = CreateSubscriptionDto.builder()
                .userId(22)
                .name("Andrey")
                .provider("Google")
                .expirationDate(Instant.now().plus(Duration.ofDays(10)))
                .build();

        ValidationResult actualResult = validator.validate(subscriptionDto);

        assertFalse(actualResult.hasErrors());
    }

    @Test
    @DisplayName("Code 100 if userID is null")
    void invalidId() {
        CreateSubscriptionDto subscriptionDto = CreateSubscriptionDto.builder()
                .userId(null)
                .name("Andrey")
                .provider("Google")
                .expirationDate(Instant.now().plus(Duration.ofDays(10)))
                .build();

        ValidationResult actualResult = validator.validate(subscriptionDto);

        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().get(0).getCode()).isEqualTo(100);
    }

    @Test
    @DisplayName("Code 101 if username is empty")
    void invalidName() {
        CreateSubscriptionDto subscriptionDto = CreateSubscriptionDto.builder()
                .userId(22)
                .name("")
                .provider("Google")
                .expirationDate(Instant.now().plus(Duration.ofDays(10)))
                .build();

        ValidationResult actualResult = validator.validate(subscriptionDto);

        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().get(0).getCode()).isEqualTo(101);
    }

    @Test
    @DisplayName("Code 102 if provider name is unknown")
    void invalidProvider() {
        CreateSubscriptionDto subscriptionDto = CreateSubscriptionDto.builder()
                .userId(22)
                .name("Andrey")
                .provider("dummy")
                .expirationDate(Instant.now().plus(Duration.ofDays(10)))
                .build();

        ValidationResult actualResult = validator.validate(subscriptionDto);

        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().get(0).getCode()).isEqualTo(102);
    }

    @Test
    @DisplayName("Code 103 if expiration date is in the past")
    void invalidUserIdNameDate() {
        CreateSubscriptionDto subscriptionDto = CreateSubscriptionDto.builder()
                .userId(22)
                .name("Andrey")
                .provider("Google")
                .expirationDate(Instant.now().minus(Duration.ofDays(10)))
                .build();

        ValidationResult actualResult = validator.validate(subscriptionDto);

        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().get(0).getCode()).isEqualTo(103);
    }

    @Test
    @DisplayName("Code 103 if expiration date is null")
    void nullExpirationDate() {
        CreateSubscriptionDto subscriptionDto = CreateSubscriptionDto.builder()
                .userId(22)
                .name("Andrey")
                .provider("Google")
                .expirationDate(null)
                .build();

        ValidationResult actualResult = validator.validate(subscriptionDto);

        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().get(0).getCode()).isEqualTo(103);
    }

    @Test
    @DisplayName("Codes 100 & 101 & 103 if userID & username & expiration date are incorrect")
    void invalidUserIdUsernameExpirationDate() {
        CreateSubscriptionDto subscriptionDto = CreateSubscriptionDto.builder()
                .userId(null)
                .name("")
                .provider("Google")
                .expirationDate(Instant.now().minus(Duration.ofDays(10)))
                .build();

        ValidationResult actualResult = validator.validate(subscriptionDto);

        assertThat(actualResult.getErrors()).hasSize(3);
        var errorCodes = actualResult.getErrors().stream()
                .map(Error::getCode)
                .toList();
        assertThat(errorCodes).contains(100, 101, 103);
    }
}
package com.dmdev.service;

import com.dmdev.dao.SubscriptionDao;
import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import com.dmdev.exception.SubscriptionException;
import com.dmdev.exception.ValidationException;
import com.dmdev.mapper.CreateSubscriptionMapper;
import com.dmdev.validator.CreateSubscriptionValidator;
import com.dmdev.validator.Error;
import com.dmdev.validator.ValidationResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionDao subscriptionDao;
    @Mock
    private CreateSubscriptionMapper createSubscriptionMapper;
    @Mock
    private CreateSubscriptionValidator createSubscriptionValidator;
    @Mock
    private Clock clock;
    @InjectMocks
    private SubscriptionService subscriptionService;

    @Nested
    @DisplayName("Subscription upsert tests")
    class UpsertTest {

        @Test
        @DisplayName("Upsert successful")
        void upsertSuccess() {
            var createSubscriptionDto = getSubscriptionDto();
            var subscription = getSubscription();

            doReturn(new ValidationResult()).when(createSubscriptionValidator).validate(createSubscriptionDto);
            doReturn(List.of(subscription)).when(subscriptionDao).findByUserId(subscription.getUserId());
            doReturn(subscription).when(subscriptionDao).upsert(subscription);

            var actualResult = subscriptionService.upsert(createSubscriptionDto);

            assertThat(actualResult).isEqualTo(subscription);
            verify(subscriptionDao).upsert(subscription);
        }

        @Test
        @DisplayName("Throws ValidationException if DTO is not valid")
        void upsertShouldThrowExceptionIfDtoInvalid() {
            var createSubscriptionDto = getSubscriptionDto();
            var validationResult = new ValidationResult();
            validationResult.add(Error.of(100, "Invalid userID"));
            doReturn(validationResult).when(createSubscriptionValidator).validate(createSubscriptionDto);

            assertThrows(ValidationException.class, () -> subscriptionService.upsert(createSubscriptionDto));
            verifyNoInteractions(subscriptionDao, createSubscriptionMapper);
        }
    }

    @Nested
    @DisplayName("Subscription cancel tests")
    class CancelTest {

        @Test
        @DisplayName("Subscription is cancelled successfully")
        void cancelSuccess() {
            var subscription = getSubscription();
            var cancelledSubscription = Subscription.builder()
                    .id(1)
                    .userId(22)
                    .name("Andrey")
                    .provider(Provider.GOOGLE)
                    .status(Status.CANCELED)
                    .expirationDate(Instant.now()
                            .plus(Duration.ofDays(10))
                            .truncatedTo(ChronoUnit.DAYS))
                    .build();
            doReturn(Optional.of(subscription)).when(subscriptionDao).findById(subscription.getId());

            subscriptionService.cancel(subscription.getId());

            assertThat(subscription).isEqualTo(cancelledSubscription);
            verify(subscriptionDao).update(subscription);
        }

        @Test
        @DisplayName("Throws SubscriptionException if subscription status is not ACTIVE")
        void shouldThrowSubscriptionExceptionExceptionIfSubscriptionNotActive() {
            var subscription = getSubscription();

            subscription.setStatus(Status.CANCELED);
            doReturn(Optional.of(subscription)).when(subscriptionDao).findById(subscription.getId());

            assertThrows(SubscriptionException.class, () -> subscriptionService.cancel(subscription.getId()));
        }

        @Test
        @DisplayName("Throws IAE if no subscription present")
        void shouldThrowIllegalArgumentExceptionIfIdNotValid() {
            var subscription = new Subscription();

            assertThrows(IllegalArgumentException.class, () -> subscriptionService.cancel(subscription.getId()));
        }
    }

    @Nested
    @DisplayName("Subscription expire tests")
    class ExpireTests {

        @Test
        @DisplayName("Subscription is expired successfully")
        void expireSuccess() {
            var subscription = getSubscription();
            var expiredSubscription = Subscription.builder()
                    .id(1)
                    .userId(22)
                    .name("Andrey")
                    .provider(Provider.GOOGLE)
                    .status(Status.EXPIRED)
                    .expirationDate(Instant.now(clock))
                    .build();
            doReturn(Optional.of(subscription)).when(subscriptionDao).findById(subscription.getId());

            subscriptionService.expire(subscription.getId());

            assertThat(expiredSubscription).isEqualTo(subscription);
        }

        @Test
        @DisplayName("Throws SubscriptionException if subscription status is not EXPIRED")
        void shouldThrowSubscriptionExceptionExceptionIfSubscriptionExpired() {
            var subscription = getSubscription();
            subscription.setStatus(Status.EXPIRED);
            doReturn(Optional.of(subscription)).when(subscriptionDao).findById(subscription.getId());

            assertThrows(SubscriptionException.class, () -> subscriptionService.expire(subscription.getId()));
        }

        @Test
        @DisplayName("Throws IAE if no subscription present")
        void expireShouldThrowIllegalArgumentExceptionIfIdNotValid() {
            var subscription = new Subscription();

            assertThrows(IllegalArgumentException.class, () -> subscriptionService.expire(subscription.getId()));
        }
    }

    private static CreateSubscriptionDto getSubscriptionDto() {
        return CreateSubscriptionDto.builder()
                .userId(22)
                .name("Andrey")
                .provider("Google")
                .expirationDate(Instant.now()
                        .plus(Duration.ofDays(10)).truncatedTo(ChronoUnit.DAYS))
                .build();
    }

    private static Subscription getSubscription() {
        return Subscription.builder()
                .id(1)
                .userId(22)
                .name("Andrey")
                .provider(Provider.GOOGLE)
                .status(Status.ACTIVE)
                .expirationDate(Instant.now().plus(Duration.ofDays(10)).truncatedTo(ChronoUnit.DAYS))
                .build();
    }
}
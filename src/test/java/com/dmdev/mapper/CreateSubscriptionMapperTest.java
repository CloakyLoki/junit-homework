package com.dmdev.mapper;

import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

class CreateSubscriptionMapperTest {

    private final CreateSubscriptionMapper mapper = CreateSubscriptionMapper.getInstance();

    @Test
    @DisplayName("Mapping successful with correct data")
    void mapCheck() {
        var subscriptionDto = CreateSubscriptionDto.builder()
                .userId(22)
                .name("Andrey")
                .provider("Google")
                .expirationDate(Instant.now().plus(Duration.ofDays(10)).truncatedTo(ChronoUnit.DAYS))
                .build();

        var actualResult = mapper.map(subscriptionDto);

        var expectedResult = Subscription.builder()
                .userId(22)
                .name("Andrey")
                .provider(Provider.GOOGLE)
                .status(Status.ACTIVE)
                .expirationDate(Instant.now().plus(Duration.ofDays(10)).truncatedTo(ChronoUnit.DAYS))
                .build();

        Assertions.assertThat(actualResult).isEqualTo(expectedResult);
    }
}
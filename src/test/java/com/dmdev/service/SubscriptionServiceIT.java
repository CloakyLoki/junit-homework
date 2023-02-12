package com.dmdev.service;

import com.dmdev.dao.SubscriptionDao;
import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.integration.IntegrationTestBase;
import com.dmdev.mapper.CreateSubscriptionMapper;
import com.dmdev.validator.CreateSubscriptionValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class SubscriptionServiceIT extends IntegrationTestBase {

    private SubscriptionService subscriptionService;
    private Clock clock;

    @BeforeEach
    void init() {
        var subscriptionDao = SubscriptionDao.getInstance();
        subscriptionService = new SubscriptionService(
                subscriptionDao,
                CreateSubscriptionMapper.getInstance(),
                CreateSubscriptionValidator.getInstance(),
                clock
        );
    }

    @Test
    @DisplayName("Upsert")
    void upsertSuccess() {
        var subscriptionDto = getSubscriptionDto();

        var actualResult = subscriptionService.upsert(subscriptionDto);

        Assertions.assertNotNull(actualResult.getId());
    }

    private static CreateSubscriptionDto getSubscriptionDto() {
        return CreateSubscriptionDto.builder()
                .userId(22)
                .name("Andrey")
                .provider("Google")
                .expirationDate(Instant.now().plus(Duration.ofDays(10)).truncatedTo(ChronoUnit.DAYS))
                .build();
    }
}
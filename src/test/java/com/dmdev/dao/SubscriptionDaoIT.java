package com.dmdev.dao;

import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import com.dmdev.integration.IntegrationTestBase;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SubscriptionDaoIT extends IntegrationTestBase {

    private final SubscriptionDao subscriptionDao = SubscriptionDao.getInstance();

    @Test
    void findAll() {
        var subscription1 = subscriptionDao.insert(getSubscription(1, "Andrey"));
        var subscription2 = subscriptionDao.insert(getSubscription(2, "Anna"));
        var subscription3 = subscriptionDao.insert(getSubscription(3, "Kira"));

        var actualResult = subscriptionDao.findAll();

        assertThat(actualResult).hasSize(3);
        var subscriptions = actualResult.stream()
                .map(Subscription::getId)
                .toList();
        assertThat(subscriptions).contains(subscription1.getId(), subscription2.getId(), subscription3.getId());
    }

    @Test
    void findById() {
        var subscription = subscriptionDao.insert(getSubscription(1, "Andrey"));

        var actualResult = subscriptionDao.findById(subscription.getId());

        assertThat(actualResult).isPresent();
        assertThat(actualResult.get()).isEqualTo(subscription);
    }

    @Test
    void update() {
        var subscription = getSubscription(1, "Andrey");
        subscriptionDao.insert(subscription);
        subscription.setName("Ivan");
        subscription.setStatus(Status.CANCELED);

        subscriptionDao.update(subscription);

        var updatedSubscription = subscriptionDao.findById(subscription.getId()).get();
        assertThat(updatedSubscription).isEqualTo(subscription);
    }

    @Test
    void insert() {
        var subscription = getSubscription(1, "Andrey");

        var actualResult = subscriptionDao.insert(subscription);

        assertNotNull(actualResult.getId());
    }

    @Test
    void findByUserId() {
        var subscription = subscriptionDao.insert(getSubscription(1, "Andrey"));

        var actualResult = subscriptionDao.findByUserId(subscription.getUserId());

        assertThat(actualResult).hasSize(1);
        assertThat(actualResult).contains(subscription);
    }

    @Test
    void shouldNotFindByUserIdIfSubscriptionDoesNotExist() {
        var subscription = subscriptionDao.insert(getSubscription(1, "Andrey"));
        var actualResult = subscriptionDao.findByUserId(0);

        assertThat(actualResult).isEmpty();
    }

    private static Subscription getSubscription(Integer userId, String name) {
        return Subscription.builder()
                .userId(userId)
                .name(name)
                .provider(Provider.GOOGLE)
                .status(Status.ACTIVE)
                .expirationDate(Instant.now().plus(Duration.ofDays(10)).truncatedTo(ChronoUnit.DAYS))
                .build();
    }

    @Nested
    class DeleteTest {

        @Test
        void deleteExistingSubscription() {
            var subscription = subscriptionDao.insert(getSubscription(1, "Andrey"));

            var actualResult = subscriptionDao.delete(subscription.getId());

            assertTrue(actualResult);
        }

        @Test
        void deleteNotExistingSubscription() {
            subscriptionDao.insert(getSubscription(1, "Andrey"));

            var actualResult = subscriptionDao.delete(9999999);

            assertFalse(actualResult);
        }
    }
}
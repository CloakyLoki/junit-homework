package com.dmdev.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProviderTest {

    @Test
    @DisplayName("Provider name is known")
    void providerNameIsValid() {
        String provider = "Apple";

        var actualResult = Provider.findByName(provider);

        assertThat(actualResult).isEqualTo(Provider.APPLE);
    }

    @Test
    @DisplayName("Throws NSE exception if provider name is unknown")
    void providerNameIsNotValid() {
        String provider = "";

        assertThrows(NoSuchElementException.class, () -> Provider.findByName(provider));
    }
}
package com.kiaev.client.car;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;

class CarImagePathNormalizerTest {

    @Test
    void resolveUsesCanonicalPathWhenOnlyCaseDiffers() {
        CarImagePathNormalizer normalizer = new CarImagePathNormalizer(Map.of(
                "ev6_1.jpg", "/images/cars/EV6_1.jpg",
                "ev6_2.jpg", "/images/cars/EV6_2.jpg"));

        assertThat(normalizer.resolve("/images/cars/ev6_1.jpg"))
                .isEqualTo("/images/cars/EV6_1.jpg");
        assertThat(normalizer.resolve("/images/cars/ev6_2.jpg"))
                .isEqualTo("/images/cars/EV6_2.jpg");
    }

    @Test
    void normalizeLeavesUnknownPathsUntouched() {
        CarImagePathNormalizer normalizer = new CarImagePathNormalizer(Map.of(
                "ev6_1.jpg", "/images/cars/EV6_1.jpg"));

        assertThat(normalizer.resolve("/images/chatbot/chatbot-icon.png"))
                .isEqualTo("/images/chatbot/chatbot-icon.png");
        assertThat(normalizer.resolve("/images/cars/RayEV_1.jpg"))
                .isEqualTo("/images/cars/RayEV_1.jpg");
    }
}

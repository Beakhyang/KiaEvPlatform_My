package com.kiaev.client.promotion;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.jupiter.api.Test;

class PromotionImagePathNormalizerTest {

    @Test
    void resolveConvertsLegacyUploadsPathToCanonicalPromotionPath() {
        PromotionImagePathNormalizer normalizer = new PromotionImagePathNormalizer(Map.of(
                "니로_ev_재고_특별전_할인이벤트.jpg", "/images/promotion/니로_EV_재고_특별전_할인이벤트.jpg"));

        assertThat(normalizer.resolve("/uploads/니로_EV_재고_특별전_할인이벤트.jpg"))
                .isEqualTo("/images/promotion/니로_EV_재고_특별전_할인이벤트.jpg");
    }

    @Test
    void resolveUsesCanonicalPathWhenOnlyCaseDiffers() {
        PromotionImagePathNormalizer normalizer = new PromotionImagePathNormalizer(Map.of(
                "ray_ev_도심주행패키지.jpg", "/images/promotion/Ray_EV_도심주행패키지.jpg"));

        assertThat(normalizer.resolve("/images/promotion/ray_ev_도심주행패키지.jpg"))
                .isEqualTo("/images/promotion/Ray_EV_도심주행패키지.jpg");
    }

    @Test
    void resolveLeavesExternalUrlsUntouched() {
        PromotionImagePathNormalizer normalizer = new PromotionImagePathNormalizer(Map.of());

        assertThat(normalizer.resolve("https://example.com/promo.jpg"))
                .isEqualTo("https://example.com/promo.jpg");
    }
}

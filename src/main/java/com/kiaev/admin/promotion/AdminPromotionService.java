package com.kiaev.admin.promotion;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.kiaev.client.promotion.Promotion;
import com.kiaev.client.promotion.PromotionImagePathNormalizer;
import com.kiaev.client.promotion.PromotionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminPromotionService {

    private final PromotionRepository promotionRepository;
    private final PromotionImagePathNormalizer promotionImagePathNormalizer;

    public List<Promotion> getPromotions() {
        return promotionRepository.findAll(Sort.by(Sort.Direction.DESC, "id")).stream()
                .map(promotionImagePathNormalizer::normalize)
                .toList();
    }

    public Promotion getPromotion(Long promotionId) {
        return promotionImagePathNormalizer.normalize(
                promotionRepository.findById(promotionId)
                        .orElseThrow(() -> new IllegalArgumentException("프로모션을 찾을 수 없습니다. ID: " + promotionId)));
    }

    public Promotion createEmptyPromotion() {
        Promotion promotion = Promotion.builder().build();
        promotion.setType("EVENT");
        promotion.setTargetModel("ALL");
        promotion.setBannerOrder(0);
        promotion.setActive(true);
        promotion.setStartDate(LocalDateTime.now().withSecond(0).withNano(0));
        promotion.setEndDate(LocalDateTime.now().plusDays(7).withSecond(0).withNano(0));
        return promotion;
    }

    public void save(Promotion promotion) {
        if (promotion.getType() == null || promotion.getType().isBlank()) {
            promotion.setType("EVENT");
        }
        if (promotion.getTargetModel() == null || promotion.getTargetModel().isBlank()) {
            promotion.setTargetModel("ALL");
        }
        if (promotion.getDiscountAmount() == null) {
            promotion.setDiscountAmount(0);
        }
        if (promotion.getBannerOrder() == null) {
            promotion.setBannerOrder(0);
        }

        promotion.setBannerImageUrl(promotionImagePathNormalizer.resolve(promotion.getBannerImageUrl()));
        promotionRepository.save(promotion);
    }

    public void toggle(Long promotionId) {
        Promotion promotion = getPromotion(promotionId);
        promotion.setActive(!promotion.isActive());
        promotion.setBannerImageUrl(promotionImagePathNormalizer.resolve(promotion.getBannerImageUrl()));
        promotionRepository.save(promotion);
    }

    public void delete(Long promotionId) {
        promotionRepository.deleteById(promotionId);
    }
}

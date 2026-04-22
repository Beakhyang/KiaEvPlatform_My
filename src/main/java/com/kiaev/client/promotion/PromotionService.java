package com.kiaev.client.promotion;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PromotionService {

    private final PromotionRepository promotionRepository;
    private final PromotionImagePathNormalizer promotionImagePathNormalizer;

    public List<Promotion> getActivePromotions() {
        LocalDateTime now = LocalDateTime.now();

        return promotionRepository.findByIsActiveTrueAndStartDateBeforeAndEndDateAfterOrderByBannerOrderAsc(now, now)
                .stream()
                .filter(promotion -> !"POPUP".equalsIgnoreCase(promotion.getType()))
                .map(promotionImagePathNormalizer::normalize)
                .toList();
    }

    public Promotion getPromotionById(Long id) {
        return promotionImagePathNormalizer.normalize(
                promotionRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("해당 프로모션이 없습니다. ID: " + id)));
    }
}

package com.kiaev.client.promotion;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service // 스프링에게 이 클래스가 비즈니스 로직을 처리하는 '서비스' 역할임을 알려줍니다. (스프링 빈으로 등록됨)
@RequiredArgsConstructor // final이 붙은 필드(promotionRepository)의 생성자를 자동으로 만들어줍니다. (의존성 주입)
@Transactional(readOnly = true) // 이 클래스의 모든 메서드는 기본적으로 '읽기 전용' 트랜잭션으로 동작하여 조회 속도를 높입니다.
public class PromotionService {

    // 데이터베이스와 소통하는 인터페이스(1번에서 만드신 Repository)를 불러옵니다.
    private final PromotionRepository promotionRepository;

    /**
     * [진행 중인 프로모션 목록 조회]
     * 사용자에게 보여줄 '현재 진행 중'인 프로모션만 걸러내는 메서드입니다.
     */
    public List<Promotion> getActivePromotions() {
        // 1. 현재 시스템의 날짜와 시간을 가져옵니다.
        LocalDateTime now = LocalDateTime.now();
        
        // 2. Repository에 만들어둔 쿼리 메서드를 실행합니다.
        // 의미: 시작일이 'now' 이전이고, 종료일이 'now' 이후인 데이터만 찾아라!
        return promotionRepository.findByIsActiveTrueAndStartDateBeforeAndEndDateAfterOrderByBannerOrderAsc(now, now);
    }

    /**
     * [프로모션 상세 조회]
     * 특정 ID를 가진 프로모션 1개의 상세 정보를 가져오는 메서드입니다.
     */
    public Promotion getPromotionById(Long id) {
        // DB에서 ID로 프로모션을 찾습니다.
        return promotionRepository.findById(id)
                // 만약 해당 ID의 데이터가 없다면, IllegalArgumentException 에러를 발생시킵니다.
                .orElseThrow(() -> new IllegalArgumentException("해당 프로모션이 없습니다. ID: " + id));
    }
    
    
}

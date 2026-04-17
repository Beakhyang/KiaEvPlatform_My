package com.kiaev.client.promotion;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 프로모션 데이터 접근을 위한 인터페이스
 * JpaRepository를 상속받아 CRUD(저장, 조회, 수정, 삭제) 기능을 자동으로 부여받음
 */
public interface PromotionRepository extends JpaRepository<Promotion, Long> {

    /**
     * 진행 중인 프로모션 목록 조회
     * 시작 날짜가 현재 시점 이전이고(Before), 종료 날짜가 현재 시점 이후(After)인 데이터를 찾음
     * @param now1 현재 시각 (시작일 비교용)
     * @param now2 현재 시각 (종료일 비교용)
     * @return 조건에 맞는 프로모션 리스트
     */
    /**List<Promotion> findByStartDateBeforeAndEndDateAfter(LocalDateTime now1, LocalDateTime now2);*/
    
 // [수정됨] 활성화 상태(isActive=true)이면서, 기간이 현재 진행 중인 것만 필터링
    List<Promotion> findByIsActiveTrueAndStartDateBeforeAndEndDateAfterOrderByBannerOrderAsc(
            LocalDateTime now1,
            LocalDateTime now2
    );

    /**
     * 모든 프로모션을 최신 등록순으로 조회
     */
    List<Promotion> findAllByOrderByIdDesc();
    
 // 🌟 추가: 오늘 날짜를 기준으로 "시작일이 지났고, 종료일이 아직 안 지난" 데이터만 가져오기 (최신순 정렬)
    List<Promotion> findByStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByStartDateDesc(
            LocalDateTime now1, 
            LocalDateTime now2
    );
    
}

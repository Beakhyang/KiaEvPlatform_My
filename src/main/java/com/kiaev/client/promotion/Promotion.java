package com.kiaev.client.promotion;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

/**
 * 프로모션(이벤트, 혜택 등) 정보를 관리하는 엔티티(Entity) 클래스입니다.
 * 데이터베이스의 'PROMOTION_TBL' 테이블과 1:1로 매핑됩니다.
 */
@Entity // JPA가 관리하는 객체임을 스프링에게 알려줍니다.
@Table(name = "PROMOTION_TBL") // 실제 DB에 생성되어 있는 테이블 이름을 지정합니다.
@Getter // 모든 필드의 Getter 메서드를 자동 생성해 줍니다.
@Setter // 모든 필드의 Setter 메서드를 자동 생성해 줍니다. (필요에 따라 DTO를 쓰는 것이 더 좋을 수 있습니다.)
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 파라미터가 없는 기본 생성자를 만듭니다. (무분별한 객체 생성을 막기 위해 PROTECTED 설정)
@AllArgsConstructor // 모든 필드값을 파라미터로 받는 생성자를 만듭니다. (@Builder 사용을 위해 필요)
@Builder // 빌더 패턴을 사용하여 객체를 직관적이고 안전하게 생성할 수 있게 해줍니다.
public class Promotion {

    // 1. 프로모션 고유 ID (기본키 - PK)
    @Id // 이 필드가 테이블의 Primary Key임을 명시합니다.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB의 AUTO_INCREMENT 기능을 사용하여 값이 자동으로 1씩 증가하도록 설정합니다.
    @Column(name = "promotion_id") // DB 테이블의 실제 컬럼명과 매핑합니다.
    private Long id;

    // 2. 프로모션 유형 (예: DISCOUNT(할인), GIFT(증정), EVENT(이벤트))
    @Column(name = "type", nullable = false, length = 20) // nullable=false는 필수값(NOT NULL)을 의미합니다.
    private String type;

    // 3. 프로모션 제목 (사용자에게 보여질 굵은 글씨)
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    // 4. 프로모션 소제목 (제목 밑에 들어가는 짧은 부연 설명)
    @Column(name = "sub_title", length = 255)
    private String subTitle;

    // 5. 프로모션 상세 내용 (긴 글이나 HTML 태그가 들어갈 수 있으므로 TEXT 타입 지정)
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    // 6. 대상 차량 모델 (예: EV6, Ray EV, ALL 등) - 특정 차량 전용 혜택을 구분할 때 사용합니다.
    @Column(name = "target_model", length = 50)
    private String targetModel;

    // 7. 할인 금액 (원 단위 정액 할인, 명세서 요구사항 반영)
    @Column(name = "discount_amount")
    private Integer discountAmount;

    // 8. 프로모션 시작 일시 (이벤트가 시작되는 정확한 날짜와 시간)
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    // 9. 프로모션 종료 일시 (이벤트가 끝나는 정확한 날짜와 시간)
    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    // 10. 메인 배너 노출 순서 (숫자가 작을수록 화면 앞쪽에 배치되도록 정렬할 때 사용)
    @Column(name = "banner_order")
    private Integer bannerOrder;

    // 11. 배너 이미지 경로 (웹 화면에 띄워줄 이미지 파일의 위치)
    @Column(name = "banner_image_url", length = 255)
    private String bannerImageUrl;

    // 12. 활성화(노출) 여부 (true: 사용자 화면에 보임, false: 관리자만 보거나 숨김 처리)
    @Column(name = "is_active", nullable = false)
    private boolean isActive = true; // 기본값은 '활성화(true)' 상태로 둡니다.

    // 13. 등록 일시 (관리자가 프로모션을 처음 만든 시간)
    @CreationTimestamp // 하이버네이트가 데이터 INSERT 시 자동으로 현재 시간을 넣어줍니다.
    @Column(name = "created_at", updatable = false) // updatable=false로 설정하여 한 번 등록되면 수정되지 않게 막습니다.
    private LocalDateTime createdAt;

    // 14. 수정 일시 (관리자가 프로모션 내용을 수정한 시간)
    @UpdateTimestamp // 하이버네이트가 데이터 UPDATE 시 자동으로 현재 시간으로 갱신해 줍니다.
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * [비즈니스 로직] 
     * 현재 시간을 기준으로 이 프로모션이 '진행 중'인지 확인하는 메서드입니다.
     * * @return 진행 중이면 true, 시작 전이거나 종료되었으면 false 반환
     */
    public boolean isCurrentlyActive() {
        LocalDateTime now = LocalDateTime.now();
        // 현재 시간이 (시작일 이후 거나 같음) AND (종료일 이전 이거나 같음) 인지 판별합니다.
        return (now.isAfter(this.startDate) || now.isEqual(this.startDate)) 
                && (now.isBefore(this.endDate) || now.isEqual(this.endDate));
    }
    
    /**
     * 종료일까지 남은 일수를 계산합니다.
     * @return 남은 일수 (오늘 마감이면 0, 지났으면 음수)
     */
    public long getRemainDays() {
        if (this.endDate == null) return 999; // 종료일이 없는 경우 예외 처리
        
        // 시간은 빼고 '날짜(일)' 기준으로만 계산합니다.
        return java.time.temporal.ChronoUnit.DAYS.between(
                java.time.LocalDate.now(), 
                this.endDate.toLocalDate()
        );
    }
}
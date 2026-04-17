package com.kiaev.client.car;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "CAR_TBL")
@Getter
@Setter // 관리자 페이지에서 수정을 위해 Setter 추가를 권장합니다.
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "car_no")
    private Long carNo;             // 차량 고유 번호 (PK)

    @Column(name = "model_name", nullable = false, unique = true, length = 100)
    private String modelName;       // 모델명 (예: EV6, EV9)

    @Column(name = "car_type", nullable = false, length = 30)
    private String carType;         // 차종 (예: SUV, 세단)

    @Column(name = "car_color", nullable = false, length = 500) // 색상 리스트를 위해 길이 확장
    private String carColor;        // 차량 색상 (콤마로 구분된 리스트)

    @Column(nullable = false)
    private Long price;             // 판매 가격 (숫자 계산용)

    @Column(name = "price_display", nullable = false, length = 100)
    private String priceDisplay;    // 화면 표시용 가격 (예: 9,000만원)

    @Column(name = "battery_capacity", nullable = false, length = 50)
    private String batteryCapacity; // 배터리 용량 (예: 77.4kWh)

    @Column(name = "driving_range_km", nullable = false)
    private Integer drivingRangeKm; // 1회 충전 주행 가능 거리 (km)

    @Column(name = "fast_charge_time", length = 50)
    private String fastChargeTime;  // 급속 충전 시간 정보

    @Column(name = "charge_info", length = 100)
    private String chargeInfo;      // 충전 관련 추가 정보 (예: 10% -> 80%)

    @Column(name = "car_description", length = 2000) // 명세서에 따라 2000으로 확장
    private String carDescription;  // 차량 상세 설명

    @Column(name = "image_path", length = 255)
    private String imagePath;       // 대표 이미지 및 상세1 경로

    @Column(name = "image_detail1", length = 255)
    private String imageDetail1;    // 상세 이미지 2 경로

    @Column(name = "image_detail2", length = 255)
    private String imageDetail2;    // 상세 이미지 3 경로

    @Column(name = "sale_status", nullable = false, length = 20)
    private String saleStatus;      // 판매 상태 (판매중, 판매종료 등)

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; // 등록 일시

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; // 수정 일시

    // 데이터 저장 전 실행되는 로직
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.saleStatus == null) this.saleStatus = "판매중"; // 기본값 설정
    }

    // 데이터 수정 전 실행되는 로직
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
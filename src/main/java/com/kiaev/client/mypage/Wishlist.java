package com.kiaev.client.mypage;

import com.kiaev.client.car.Car;
import com.kiaev.client.member.Member;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "WISHLIST_TBL",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "unique_member_car",
            columnNames = {"member_no", "car_no"}
        )
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wishlist_no")
    private Long wishlistNo; // 1. 관심차량번호 (PK)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_no", nullable = false)
    private Member member;   // 2. 회원번호 (FK)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "car_no", nullable = false)
    private Car car;         // 3. 차량번호 (FK)

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 4. 등록일시

    // 데이터 저장 전 현재 시간 자동 입력 (Car 엔티티와 동일 방식)
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
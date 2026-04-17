package com.kiaev.client.charging;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "CHARGING_TBL")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Charging {

    @Id
    @Column(name = "stat_id", length = 30)
    private String statId; // 충전소ID (PK)

    @Column(name = "stat_nm", length = 200, nullable = false)
    private String statNm; // 충전소명

    @Column(name = "addr", length = 255)
    private String addr; // 주소

    @Column(name = "addr_detail", length = 255)
    private String addrDetail; // 상세주소

    @Column(name = "location_desc", length = 255)
    private String locationDesc; // 위치설명

    @Column(name = "lat", precision = 10, scale = 7, nullable = false)
    private BigDecimal lat; // 위도

    @Column(name = "lng", precision = 10, scale = 7, nullable = false)
    private BigDecimal lng; // 경도

    @Column(name = "use_time", length = 100)
    private String useTime; // 이용가능시간
}
package com.kiaev.client.charging;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
public class ChargingStationRow {
    private String statId;
    private String statNm;
    private String addr;
    private String addrDetail;
    private String locationDesc;
    private BigDecimal lat;
    private BigDecimal lng;
    private String useTime;

    // DTO를 Entity로 변환하는 메서드
    public Charging toEntity() {
        return Charging.builder()
                .statId(this.statId)
                .statNm(this.statNm)
                .addr(this.addr)
                .addrDetail(this.addrDetail)
                .locationDesc(this.locationDesc)
                .lat(this.lat)
                .lng(this.lng)
                .useTime(this.useTime)
                .build();
    }
}
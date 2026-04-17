package com.kiaev.client.charging;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * DB에서 조회한 충전소 정보를 클라이언트(화면)로 전달할 때 사용하는 DTO
 */
@Getter
@Builder
public class ChargingResponseDto {
    private String statId;       // 충전소 아이디
    private String statNm;       // 충전소 이름
    private String addr;         // 기본 주소
    private String addrDetail;   // 상세 주소
    private String locationDesc; // 위치 상세 설명
    private BigDecimal lat;      // 위도 (카카오맵 표시용)
    private BigDecimal lng;      // 경도 (카카오맵 표시용)
    private String useTime;      // 이용 가능 시간

    /**
     * Entity(DB 매핑 객체)를 DTO(화면 전달용 객체)로 변환해주는 편의 메서드
     * @param entity DB에서 꺼내온 Charging 객체
     * @return 화면에 전달할 ChargingResponseDto 객체
     */
    public static ChargingResponseDto from(Charging entity) {
        return ChargingResponseDto.builder()
                .statId(entity.getStatId())
                .statNm(entity.getStatNm())
                .addr(entity.getAddr())
                .addrDetail(entity.getAddrDetail())
                .locationDesc(entity.getLocationDesc())
                .lat(entity.getLat())
                .lng(entity.getLng())
                .useTime(entity.getUseTime())
                .build();
    }
}
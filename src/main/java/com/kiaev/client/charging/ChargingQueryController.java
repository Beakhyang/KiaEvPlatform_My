package com.kiaev.client.charging;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/charging") // 조회용 API의 기본 주소를 설정합니다.
@RequiredArgsConstructor
public class ChargingQueryController {

    // 데이터를 가져오는 비즈니스 로직이 담긴 Service를 주입받습니다.
    private final ChargingQueryService chargingQueryService;

    /**
     * 충전소 전체 목록을 JSON 형태로 응답하는 API
     * 브라우저에서 GET 방식으로 http://localhost:8080/api/charging/stations 를 호출하면 실행됩니다.
     */
    @GetMapping("/stations")
    public ResponseEntity<List<ChargingResponseDto>> getStations(
            @RequestParam(value = "location", required = false) String location) {
        
        // 1. Service를 통해 모든 충전소 목록을 가져옵니다.
        List<ChargingResponseDto> stations = (location == null || location.trim().isEmpty())
                ? chargingQueryService.getAllChargingStations()
                : chargingQueryService.searchChargingStations(location);
        
        // 2. 가져온 데이터를 정상 상태(HTTP 200 OK)와 함께 클라이언트로 보냅니다.
        return ResponseEntity.ok(stations);
    }
}

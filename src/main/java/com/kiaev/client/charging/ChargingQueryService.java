package com.kiaev.client.charging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 데이터를 읽기만 하므로 readOnly = true를 주어 성능을 최적화합니다.
public class ChargingQueryService {

    // DB 접근을 위해 Repository를 주입받습니다.
    private final ChargingRepository chargingRepository;

    /**
     * DB에 저장된 모든 충전소 목록을 조회하는 메서드
     * @return 충전소 DTO 리스트
     */
    public List<ChargingResponseDto> getAllChargingStations() {
        log.info("모든 충전소 목록 조회를 시작합니다.");

        // 1. DB에서 충전소 엔티티 목록을 전부 가져옵니다.
        List<Charging> chargingList = chargingRepository.findAll();

        // 2. 가져온 Entity 리스트를 DTO 리스트로 변환하여 반환합니다.
        return chargingList.stream()
                .map(ChargingResponseDto::from) // 각각의 Entity를 DTO로 변환 (아까 만든 from 메서드 사용)
                .collect(Collectors.toList());  // 다시 리스트 형태로 묶어줍니다.
    }
    public List<ChargingResponseDto> searchChargingStations(String keyword) {
        String normalizedKeyword = keyword == null ? "" : keyword.trim();

        if (normalizedKeyword.isEmpty()) {
            return getAllChargingStations();
        }

        return chargingRepository
                .findByStatNmContainingIgnoreCaseOrAddrContainingIgnoreCaseOrAddrDetailContainingIgnoreCaseOrLocationDescContainingIgnoreCase(
                        normalizedKeyword,
                        normalizedKeyword,
                        normalizedKeyword,
                        normalizedKeyword
                )
                .stream()
                .map(ChargingResponseDto::from)
                .collect(Collectors.toList());
    }
}

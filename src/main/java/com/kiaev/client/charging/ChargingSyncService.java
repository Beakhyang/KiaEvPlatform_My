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
public class ChargingSyncService {

    private final ChargingRepository chargingRepository;

    @Transactional
    public void syncChargingStations(List<ChargingStationRow> rows) {
        log.info("수신된 충전소 데이터 개수: {}", rows.size());

        List<Charging> chargingList = rows.stream()
                .map(ChargingStationRow::toEntity)
                .collect(Collectors.toList());

        chargingRepository.saveAll(chargingList);
        
        log.info("충전소 데이터 적재 완료");
    }
}
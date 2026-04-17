package com.kiaev.client.charging;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sync")
@RequiredArgsConstructor
public class ChargingSyncController {

    private final ChargingSyncService chargingSyncService;

    @PostMapping("/charging-stations")
    public ResponseEntity<String> syncStations(@RequestBody List<ChargingStationRow> rows) {
        try {
            chargingSyncService.syncChargingStations(rows);
            return ResponseEntity.ok("데이터 적재 성공 (총 " + rows.size() + "건)");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("데이터 적재 실패: " + e.getMessage());
        }
    }
}
package com.kiaev.client.charging;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sync")
public class ChargingSyncController {

    private final ChargingSyncService chargingSyncService;
    private final boolean chargingSyncEnabled;
    private final String chargingSyncToken;

    public ChargingSyncController(
            ChargingSyncService chargingSyncService,
            @Value("${charging.sync.enabled:false}") boolean chargingSyncEnabled,
            @Value("${charging.sync.token:}") String chargingSyncToken) {
        this.chargingSyncService = chargingSyncService;
        this.chargingSyncEnabled = chargingSyncEnabled;
        this.chargingSyncToken = chargingSyncToken;
    }

    @PostMapping("/charging-stations")
    public ResponseEntity<String> syncStations(
            @RequestBody List<ChargingStationRow> rows,
            @RequestHeader(name = "X-Sync-Token", required = false) String syncTokenHeader) {
        if (!chargingSyncEnabled) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Charging sync is disabled.");
        }

        if (hasText(chargingSyncToken) && !chargingSyncToken.equals(syncTokenHeader)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Charging sync token is invalid.");
        }

        try {
            chargingSyncService.syncChargingStations(rows);
            return ResponseEntity.ok("Stored charging stations: " + rows.size());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Charging sync failed: " + e.getMessage());
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}

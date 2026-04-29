package com.kiaev.client.charging;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class ChargingSyncControllerTest {

    @Test
    void syncStationsReturnsForbiddenWhenSyncIsDisabled() throws Exception {
        ChargingSyncService service = Mockito.mock(ChargingSyncService.class);
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new ChargingSyncController(service, false, ""))
                .build();

        mockMvc.perform(post("/api/sync/charging-stations")
                .contentType(MediaType.APPLICATION_JSON)
                .content("[]"))
                .andExpect(status().isForbidden())
                .andExpect(content().string("Charging sync is disabled."));

        verifyNoInteractions(service);
    }

    @Test
    void syncStationsReturnsUnauthorizedWhenTokenDoesNotMatch() throws Exception {
        ChargingSyncService service = Mockito.mock(ChargingSyncService.class);
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new ChargingSyncController(service, true, "expected-token"))
                .build();

        mockMvc.perform(post("/api/sync/charging-stations")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Sync-Token", "wrong-token")
                .content("[]"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Charging sync token is invalid."));

        verifyNoInteractions(service);
    }

    @Test
    void syncStationsCallsServiceWhenEnabledAndTokenMatches() throws Exception {
        ChargingSyncService service = Mockito.mock(ChargingSyncService.class);
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new ChargingSyncController(service, true, "expected-token"))
                .build();

        mockMvc.perform(post("/api/sync/charging-stations")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Sync-Token", "expected-token")
                .content("""
                        [
                          {
                            "statId": "ST001",
                            "statNm": "테스트 충전소",
                            "addr": "서울",
                            "addrDetail": "",
                            "locationDesc": "테스트 위치",
                            "lat": 37.5665,
                            "lng": 126.9780,
                            "useTime": "24시간"
                          }
                        ]
                        """))
                .andExpect(status().isOk())
                .andExpect(content().string("Stored charging stations: 1"));

        verify(service).syncChargingStations(argThat((List<ChargingStationRow> rows) ->
                rows.size() == 1 && "ST001".equals(rows.get(0).getStatId())));
    }
}

package com.kiaev.cbclient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import com.kiaev.client.car.Car;
import com.kiaev.client.car.CarRepository;
import com.kiaev.dealer.sales.SalesRepository;

class ChatbotAiServiceImplTest {

    private CarRepository carRepository;
    private SalesRepository salesRepository;
    private ChatbotAiServiceImpl chatbotAiService;

    @BeforeEach
    void setUp() {
        carRepository = mock(CarRepository.class);
        salesRepository = mock(SalesRepository.class);
        chatbotAiService = new ChatbotAiServiceImpl(carRepository, salesRepository);
    }

    @Test
    void answerUsesSalesDataForBestSellingQuestion() {
        when(salesRepository.findTopSellingModelStats(any(Pageable.class))).thenReturn(List.of(
                new Object[] { "EV6", 2L, 94_000_000L },
                new Object[] { "EV9", 2L, 154_000_000L },
                new Object[] { "EV3", 1L, 39_950_000L }));

        ChatbotAiResponse response = chatbotAiService.answer(request("제일 잘팔리는 차 뭐에요?"), null);

        assertThat(response.getAnswer())
                .contains("EV6")
                .contains("EV9")
                .contains("공동 1위")
                .contains("EV3 1건")
                .contains("누적 판매 데이터 기준");
    }

    @Test
    void buildPromptIncludesCarsBeyondAlphabeticalTopFive() {
        when(carRepository.findAll()).thenReturn(List.of(
                car("EV3"),
                car("EV3-GT"),
                car("EV4"),
                car("EV4-GT"),
                car("EV5"),
                car("EV6"),
                car("EV9"),
                car("Ray EV")));
        when(salesRepository.findTopSellingModelStats(any(Pageable.class))).thenReturn(List.of(
                new Object[] { "EV9", 3L, 231_000_000L },
                new Object[] { "EV6", 2L, 94_000_000L }));

        String prompt = ReflectionTestUtils.invokeMethod(chatbotAiService, "buildPrompt", "EV9 정보 알려줘", null, null);

        assertThat(prompt)
                .contains("EV6")
                .contains("EV9")
                .contains("Ray EV")
                .contains("참고 판매 데이터");
    }

    private ChatbotAiRequest request(String message) {
        ChatbotAiRequest request = new ChatbotAiRequest();
        request.setMessage(message);
        return request;
    }

    private Car car(String modelName) {
        return Car.builder()
                .modelName(modelName)
                .carType("SUV")
                .priceDisplay("4,700만원")
                .drivingRangeKm(400)
                .batteryCapacity("77.4kWh")
                .build();
    }
}

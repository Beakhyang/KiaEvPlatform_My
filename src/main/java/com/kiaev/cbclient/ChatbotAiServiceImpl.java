package com.kiaev.cbclient;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kiaev.client.car.Car;
import com.kiaev.client.car.CarRepository;
import com.kiaev.client.login.Login;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatbotAiServiceImpl implements ChatbotAiService {

    private static final Logger log = LoggerFactory.getLogger(ChatbotAiServiceImpl.class);

    private static final String PROVIDER = "Gemini";
    private static final int MAX_HISTORY_MESSAGES = 4;
    private static final int MAX_OUTPUT_TOKENS = 320;

    private final CarRepository carRepository;

    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://generativelanguage.googleapis.com")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

    @Value("${api.key.chatbot:}")
    private String configuredApiKey;

    @Value("${chatbot.ai.model:gemini-2.5-flash}")
    private String modelName;

    @Override
    public ChatbotAiInitResponse getInitialData(Login loginUser) {
        boolean available = hasText(resolveApiKey());

        return ChatbotAiInitResponse.builder()
                .greeting(buildGreeting(loginUser, available))
                .available(available)
                .provider(PROVIDER)
                .suggestedQuestions(defaultSuggestedQuestions())
                .build();
    }

    @Override
    public ChatbotAiResponse answer(ChatbotAiRequest request, Login loginUser) {
        String message = normalize(request != null ? request.getMessage() : null);
        if (!hasText(message)) {
            return ChatbotAiResponse.builder()
                    .answer("궁금한 내용을 입력해 주세요. 예를 들면 EV6 주행거리, 보조금, 시승 상담 같은 질문이 좋습니다.")
                    .available(hasText(resolveApiKey()))
                    .provider(PROVIDER)
                    .suggestedQuestions(defaultSuggestedQuestions())
                    .build();
        }

        String apiKey = resolveApiKey();
        if (!hasText(apiKey)) {
            return ChatbotAiResponse.builder()
                    .answer("AI 상담 키가 아직 연결되지 않았습니다. 서버 환경변수 `GEMINI_API_KEY`를 확인한 뒤 서버를 다시 시작해 주세요.")
                    .available(false)
                    .provider(PROVIDER)
                    .suggestedQuestions(defaultSuggestedQuestions())
                    .build();
        }

        try {
            GeminiGenerateContentResponse response = restClient.post()
                    .uri(uriBuilder -> uriBuilder.path("/v1beta/models/{model}:generateContent").build(modelName))
                    .header("x-goog-api-key", apiKey)
                    .body(buildGeminiRequest(message, request != null ? request.getHistory() : null, loginUser))
                    .retrieve()
                    .body(GeminiGenerateContentResponse.class);

            return ChatbotAiResponse.builder()
                    .answer(extractAnswer(response))
                    .available(true)
                    .provider(PROVIDER)
                    .suggestedQuestions(defaultSuggestedQuestions())
                    .build();
        } catch (RestClientResponseException ex) {
            log.warn("Gemini API request failed. model={}, status={}, body={}", modelName, ex.getRawStatusCode(),
                    ex.getResponseBodyAsString());
            return ChatbotAiResponse.builder()
                    .answer(buildApiErrorMessage(ex))
                    .available(true)
                    .provider(PROVIDER)
                    .suggestedQuestions(defaultSuggestedQuestions())
                    .build();
        } catch (RestClientException ex) {
            log.warn("Gemini API connection failed. model={}", modelName, ex);
            return ChatbotAiResponse.builder()
                    .answer("AI 상담 서버와 연결하는 중 문제가 발생했습니다. 잠시 후 다시 시도해 주세요.")
                    .available(true)
                    .provider(PROVIDER)
                    .suggestedQuestions(defaultSuggestedQuestions())
                    .build();
        }
    }

    private GeminiGenerateContentRequest buildGeminiRequest(String message, List<ChatbotAiMessage> history, Login loginUser) {
        return new GeminiGenerateContentRequest(
                List.of(new GeminiContent("user", List.of(new GeminiPart(buildPrompt(message, history, loginUser))))),
                new GeminiGenerationConfig(0.5, 0.8, MAX_OUTPUT_TOKENS));
    }

    private String buildPrompt(String message, List<ChatbotAiMessage> history, Login loginUser) {
        String memberSummary = loginUser == null
                ? "비로그인 사용자"
                : "%s (%s)".formatted(
                        hasText(loginUser.getMemberName()) ? normalize(loginUser.getMemberName()) : "로그인 사용자",
                        hasText(loginUser.getEmail()) ? normalize(loginUser.getEmail()) : "이메일 미등록");

        String historySummary = history == null ? ""
                : history.stream()
                        .filter(item -> item != null && hasText(item.getText()))
                        .limit(MAX_HISTORY_MESSAGES)
                        .map(item -> "%s: %s".formatted(toSpeaker(item.getRole()), normalize(item.getText())))
                        .collect(Collectors.joining("\n"));

        String carSummary = carRepository.findAll().stream()
                .sorted(Comparator.comparing(Car::getModelName, Comparator.nullsLast(String::compareToIgnoreCase)))
                .limit(5)
                .map(this::toCarSummary)
                .collect(Collectors.joining("\n"));

        String historySection = historySummary.isBlank() ? "이전 대화 없음" : historySummary;

        return """
                당신은 Kia EV 플랫폼의 AI 상담사입니다.
                항상 한국어로 짧고 정확하게 답변하세요.
                차량 추천, 주행거리, 보조금, 시승, 출고, 충전, 사이트 이용 질문을 우선 안내하세요.
                확정이 필요한 정보는 상담신청이나 공식 공고 확인이 필요하다고 덧붙이세요.
                답변은 최대 4문장으로 간결하게 작성하세요.

                현재 사용자:
                %s

                참고 차량 정보:
                %s

                이전 대화:
                %s

                현재 질문:
                %s
                """.formatted(
                memberSummary,
                carSummary.isBlank() ? "등록된 차량 정보 없음" : carSummary,
                historySection,
                message);
    }

    private String toCarSummary(Car car) {
        return "- %s | 차종: %s | 가격: %s | 주행거리: %s km | 배터리: %s"
                .formatted(
                        safeValue(car.getModelName(), "미정"),
                        safeValue(car.getCarType(), "미정"),
                        safeValue(car.getPriceDisplay(), "문의"),
                        car.getDrivingRangeKm() == null ? "문의" : car.getDrivingRangeKm(),
                        safeValue(car.getBatteryCapacity(), "문의"));
    }

    private String extractAnswer(GeminiGenerateContentResponse response) {
        if (response == null || response.candidates() == null) {
            return "지금은 AI 답변을 불러오지 못했습니다. 잠시 후 다시 시도해 주세요.";
        }

        return response.candidates().stream()
                .filter(candidate -> candidate != null && candidate.content() != null && candidate.content().parts() != null)
                .flatMap(candidate -> candidate.content().parts().stream())
                .map(GeminiPartResponse::text)
                .filter(this::hasText)
                .findFirst()
                .map(String::trim)
                .orElse("지금은 AI 답변을 불러오지 못했습니다. 잠시 후 다시 시도해 주세요.");
    }

    private String buildApiErrorMessage(RestClientResponseException ex) {
        int statusCode = ex.getRawStatusCode();

        if (statusCode == 429) {
            return "현재 Gemini API 요청 한도에 도달했습니다. 잠시 후 다시 시도하시거나 Google AI Studio에서 사용량 및 결제 설정을 확인해 주세요.";
        }

        if (statusCode == 404) {
            return "현재 설정된 Gemini 모델을 사용할 수 없습니다. 서버 설정을 최신 모델(gemini-2.5-flash 등)로 바꾼 뒤 다시 시작해 주세요.";
        }

        if (statusCode == 401 || statusCode == 403) {
            return "Gemini API 인증에 실패했습니다. API 키 권한과 프로젝트 설정을 다시 확인해 주세요.";
        }

        if (statusCode == 400) {
            return "Gemini API 요청 형식이 올바르지 않거나 모델 설정이 맞지 않습니다. 서버 설정을 확인해 주세요.";
        }

        return "Gemini API 응답 처리 중 문제가 발생했습니다. 잠시 후 다시 시도해 주세요.";
    }

    private List<String> defaultSuggestedQuestions() {
        return List.of(
                "EV6 주행거리 알려줘",
                "보조금 상담은 어떻게 받아?",
                "가족용 전기차 추천해줘",
                "시승 상담은 어디서 신청해?");
    }

    private String buildGreeting(Login loginUser, boolean available) {
        String memberName = loginUser == null ? "" : normalize(loginUser.getMemberName());
        String prefix = memberName.isBlank() ? "안녕하세요." : "안녕하세요, %s님.".formatted(memberName);

        if (available) {
            return "%s 메인 화면에서 바로 AI 상담을 시작할 수 있어요. 차량 추천, 보조금, 시승, 충전 관련 질문을 편하게 남겨 주세요."
                    .formatted(prefix);
        }

        return "%s 지금은 AI 키 연결이 확인되지 않아 준비 메시지만 보여드리고 있어요. 설정이 완료되면 바로 실시간 상담이 가능합니다."
                .formatted(prefix);
    }

    private String resolveApiKey() {
        if (hasText(configuredApiKey)) {
            return configuredApiKey.trim();
        }

        for (String envName : List.of("GEMINI_API_KEY", "Gemini_API_Key", "GEMINI API KEY", "Gemini API Key", "Gemini_ API_Key")) {
            String value = System.getenv(envName);
            if (hasText(value)) {
                return value.trim();
            }
        }

        return "";
    }

    private String toSpeaker(String role) {
        if ("assistant".equalsIgnoreCase(normalize(role)) || "model".equalsIgnoreCase(normalize(role))) {
            return "상담사";
        }
        return "고객";
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private String safeValue(String value, String fallback) {
        return hasText(value) ? value.trim() : fallback;
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private record GeminiGenerateContentRequest(
            List<GeminiContent> contents,
            GeminiGenerationConfig generationConfig) {
    }

    private record GeminiContent(
            String role,
            List<GeminiPart> parts) {
    }

    private record GeminiPart(String text) {
    }

    private record GeminiGenerationConfig(
            double temperature,
            double topP,
            int maxOutputTokens) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record GeminiGenerateContentResponse(List<GeminiCandidate> candidates) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record GeminiCandidate(GeminiContentResponse content) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record GeminiContentResponse(List<GeminiPartResponse> parts) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record GeminiPartResponse(String text) {
    }
}

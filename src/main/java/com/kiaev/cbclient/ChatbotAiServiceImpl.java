package com.kiaev.cbclient;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
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
import com.kiaev.dealer.sales.SalesRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatbotAiServiceImpl implements ChatbotAiService {

    private static final Logger log = LoggerFactory.getLogger(ChatbotAiServiceImpl.class);

    private static final String PROVIDER = "Gemini";
    private static final int MAX_HISTORY_MESSAGES = 4;
    private static final int MAX_OUTPUT_TOKENS = 1024;
    private static final int THINKING_BUDGET = 0;
    private static final int SALES_SUMMARY_LIMIT = 3;

    private final CarRepository carRepository;
    private final SalesRepository salesRepository;

    private final RestClient restClient = RestClient.builder()
            .baseUrl("https://generativelanguage.googleapis.com")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

    @Value("${api.key.chatbot:}")
    private String configuredApiKey;

    @Value("${chatbot.ai.model:gemini-2.0-flash}")
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

        ChatbotAiResponse dataBackedResponse = buildDataBackedResponse(message);
        if (dataBackedResponse != null) {
            return dataBackedResponse;
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
            log.warn("Gemini API request failed. status={}, body={}", ex.getRawStatusCode(), ex.getResponseBodyAsString());
            return ChatbotAiResponse.builder()
                    .answer(buildApiErrorMessage(ex))
                    .available(true)
                    .provider(PROVIDER)
                    .suggestedQuestions(defaultSuggestedQuestions())
                    .build();
        } catch (RestClientException ex) {
            log.warn("Gemini API connection failed", ex);
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
                new GeminiGenerationConfig(0.5, 0.8, MAX_OUTPUT_TOKENS, new GeminiThinkingConfig(THINKING_BUDGET)));
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
                .map(this::toCarSummary)
                .collect(Collectors.joining("\n"));

        String salesSummary = buildSalesSummary();
        String historySection = historySummary.isBlank() ? "이전 대화 없음" : historySummary;

        return """
                당신은 Kia EV 플랫폼의 AI 상담사입니다.
                항상 한국어로 짧고 정확하게 답변하세요.
                차량 추천, 주행거리, 보조금, 시승, 출고, 충전, 사이트 이용 질문을 우선 안내하세요.
                판매량이나 인기 모델 질문은 아래 판매 데이터를 우선 근거로 사용하세요.
                확정이 필요한 정보는 상담신청이나 공식 공고 확인이 필요하다고 덧붙이세요.
                답변은 최대 4문장으로 간결하게 작성하세요.

                현재 사용자:
                %s

                참고 차량 정보:
                %s

                참고 판매 데이터:
                %s

                이전 대화:
                %s

                현재 질문:
                %s
                """.formatted(
                memberSummary,
                carSummary.isBlank() ? "등록된 차량 정보 없음" : carSummary,
                salesSummary,
                historySection,
                message);
    }

    private ChatbotAiResponse buildDataBackedResponse(String message) {
        if (!isSalesRankingQuestion(message)) {
            return null;
        }

        return ChatbotAiResponse.builder()
                .answer(buildSalesRankingAnswer())
                .available(hasText(resolveApiKey()))
                .provider(PROVIDER)
                .suggestedQuestions(defaultSuggestedQuestions())
                .build();
    }

    private boolean isSalesRankingQuestion(String message) {
        String compressed = normalize(message).replaceAll("\\s+", "");
        boolean salesIntent = List.of("잘팔리", "많이팔리", "판매량", "판매순위", "베스트셀러", "인기모델", "인기많")
                .stream()
                .anyMatch(compressed::contains);
        boolean vehicleIntent = List.of("차", "차량", "모델", "전기차")
                .stream()
                .anyMatch(compressed::contains);

        return salesIntent && vehicleIntent;
    }

    private String buildSalesRankingAnswer() {
        List<SalesModelStat> topModels = getTopSalesModels();
        if (topModels.isEmpty()) {
            return "현재 시스템에 등록된 판매 완료 데이터가 아직 없어 어떤 모델이 가장 잘 팔렸는지 집계할 수 없어요. 판매 데이터가 쌓이면 바로 안내해드릴게요.";
        }

        long topCount = topModels.get(0).salesCount();
        List<SalesModelStat> leaders = topModels.stream()
                .filter(stat -> stat.salesCount() == topCount)
                .toList();
        String leaderNames = leaders.stream()
                .map(SalesModelStat::modelName)
                .collect(Collectors.joining(", "));

        StringBuilder answer = new StringBuilder();
        if (leaders.size() == 1) {
            answer.append("현재 시스템에 등록된 판매 완료 데이터 기준으로 가장 많이 팔린 모델은 ")
                    .append(leaderNames)
                    .append("이며 ")
                    .append(topCount)
                    .append("건입니다.");
        } else {
            answer.append("현재 시스템에 등록된 판매 완료 데이터 기준으로 ")
                    .append(leaderNames)
                    .append("가 공동 1위이며 각각 ")
                    .append(topCount)
                    .append("건입니다.");
        }

        List<SalesModelStat> runnersUp = topModels.subList(leaders.size(), topModels.size());
        if (!runnersUp.isEmpty()) {
            String runnerUpSummary = runnersUp.stream()
                    .map(stat -> "%s %d건".formatted(stat.modelName(), stat.salesCount()))
                    .collect(Collectors.joining(", "));
            answer.append(" 뒤이어 ").append(runnerUpSummary).append(" 순으로 집계됐어요.");
        }

        answer.append(" 이 답변은 외부 실시간 시장 점유율이 아니라 현재 플랫폼에 등록된 누적 판매 데이터 기준입니다.");
        return answer.toString();
    }

    private String buildSalesSummary() {
        List<SalesModelStat> topModels = getTopSalesModels();
        if (topModels.isEmpty()) {
            return "등록된 판매 완료 데이터 없음";
        }

        return topModels.stream()
                .map(stat -> "- %s | 판매 %d건 | 판매금액 합계 %s"
                        .formatted(stat.modelName(), stat.salesCount(), formatWon(stat.salesAmount())))
                .collect(Collectors.joining("\n"));
    }

    private List<SalesModelStat> getTopSalesModels() {
        return salesRepository.findTopSellingModelStats(PageRequest.of(0, SALES_SUMMARY_LIMIT)).stream()
                .map(this::toSalesModelStat)
                .toList();
    }

    private SalesModelStat toSalesModelStat(Object[] row) {
        if (row == null || row.length < 3) {
            return new SalesModelStat("미분류 차량", 0L, 0L);
        }

        String modelName = hasText(row[0] != null ? row[0].toString() : null)
                ? row[0].toString().trim()
                : "미분류 차량";
        return new SalesModelStat(modelName, toLong(row[1]), toLong(row[2]));
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
                .map(this::joinAnswerParts)
                .filter(answer -> !answer.isBlank())
                .findFirst()
                .map(String::trim)
                .orElse("지금은 AI 답변을 불러오지 못했습니다. 잠시 후 다시 시도해 주세요.");
    }

    private String joinAnswerParts(GeminiCandidate candidate) {
        return candidate.content().parts().stream()
                .map(GeminiPartResponse::text)
                .filter(this::hasText)
                .collect(Collectors.joining());
    }

    private String buildApiErrorMessage(RestClientResponseException ex) {
        int statusCode = ex.getRawStatusCode();

        if (statusCode == 429) {
            return "현재 Gemini API 요청 한도에 도달했습니다. 잠시 후 다시 시도하시거나 Google AI Studio에서 사용량 및 결제 설정을 확인해 주세요.";
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
                "제일 잘 팔리는 차는 뭐야?",
                "EV6 주행거리 알려줘",
                "가족용 전기차 추천해줘",
                "시승 상담은 어디서 신청해?");
    }

    private String buildGreeting(Login loginUser, boolean available) {
        String memberName = loginUser == null ? "" : normalize(loginUser.getMemberName());
        String prefix = memberName.isBlank() ? "안녕하세요." : "안녕하세요, %s님.".formatted(memberName);

        if (available) {
            return "%s 메인 화면에서 바로 AI 상담을 시작할 수 있어요. 차량 추천, 판매 데이터 기준 인기 모델, 보조금, 시승, 충전 관련 질문을 편하게 남겨 주세요."
                    .formatted(prefix);
        }

        return "%s 지금은 AI 키 연결이 확인되지 않아 준비 메시지만 보여드리고 있어요. 설정이 완료되면 바로 실시간 상담이 가능합니다."
                .formatted(prefix);
    }

    private String resolveApiKey() {
        if (hasText(configuredApiKey)) {
            return configuredApiKey.trim();
        }

        for (String envName : List.of("GEMINI_API_KEY", "Gemini_API_Key", "Gemini_ API_Key")) {
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

    private long toLong(Object value) {
        if (value == null) {
            return 0L;
        }

        if (value instanceof Number number) {
            return number.longValue();
        }

        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException ex) {
            return 0L;
        }
    }

    private String formatWon(long amount) {
        return String.format(Locale.KOREA, "%,d원", amount);
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
            int maxOutputTokens,
            GeminiThinkingConfig thinkingConfig) {
    }

    private record GeminiThinkingConfig(int thinkingBudget) {
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

    private record SalesModelStat(String modelName, long salesCount, long salesAmount) {
    }
}

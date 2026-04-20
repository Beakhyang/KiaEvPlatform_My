package com.kiaev.cbclient;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kiaev.client.car.Car;
import com.kiaev.client.car.CarRepository;
import com.kiaev.client.login.Login;

import lombok.RequiredArgsConstructor;

@Service
@Primary
@RequiredArgsConstructor
public class ChatbotClientServicePrimaryImpl implements ChatbotClientService {

    private final ChatbotClientRepository repository;
    private final CarRepository carRepository;
    private final ChatbotFaqService chatbotFaqService;

    @Override
    @Transactional(readOnly = true)
    public ChatbotInitResponse getInitialData(Login loginUser) {
        List<ChatbotFaqItem> faqItems = getFaqItems();
        List<String> categories = faqItems.stream()
                .map(ChatbotFaqItem::getCategory)
                .collect(Collectors.collectingAndThen(Collectors.toCollection(LinkedHashSet::new), List::copyOf));

        List<String> recommendedQuestions = faqItems.stream()
                .map(ChatbotFaqItem::getQuestion)
                .limit(6)
                .toList();

        List<ChatbotCarOption> cars = carRepository.findAll().stream()
                .limit(6)
                .map(car -> ChatbotCarOption.builder()
                        .carNo(car.getCarNo())
                        .modelName(car.getModelName())
                        .build())
                .toList();

        return ChatbotInitResponse.builder()
                .greeting("안녕하세요. Kia EV 챗봇 상담입니다. 자주 묻는 질문과 추천 질문을 통해 빠르게 답변을 받아보세요.")
                .loggedIn(loginUser != null)
                .memberName(loginUser != null ? loginUser.getMemberName() : "")
                .memberEmail(loginUser != null ? loginUser.getEmail() : "")
                .categories(categories)
                .recommendedQuestions(recommendedQuestions)
                .cars(cars)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ChatbotAnswerResponse answer(ChatbotAnswerRequest request) {
        String question = request.getQuestion() == null ? "" : request.getQuestion().trim();

        ChatbotFaqItem matched = getFaqItems().stream()
                .filter(item -> item.getQuestion().equals(question))
                .findFirst()
                .orElseGet(() -> createFallbackAnswer(question));

        return ChatbotAnswerResponse.builder()
                .category(matched.getCategory())
                .question(matched.getQuestion())
                .answer(matched.getAnswer())
                .followUpQuestions(matched.getFollowUpQuestions())
                .build();
    }

    @Override
    @Transactional
    public void registerInquiry(ChatbotInquiryRequest request, Login loginUser) {
        ChatInquiry chatInquiry = ChatInquiry.builder()
                .member_no(loginUser != null ? loginUser.getMemberNo() : null)
                .car_no(request.getCarNo())
                .category(defaultString(request.getCategory(), "기타"))
                .writer_name(resolveWriterName(request, loginUser))
                .writer_email(resolveWriterEmail(request, loginUser))
                .content(defaultString(request.getContent(), ""))
                .is_answered("N")
                .chat_source(defaultString(request.getChatSource(), "직접입력"))
                .question_summary(defaultString(request.getQuestionSummary(), request.getCategory()))
                .status(defaultString(request.getStatus(), "접수"))
                .build();

        repository.save(chatInquiry);
    }

    private String resolveWriterName(ChatbotInquiryRequest request, Login loginUser) {
        if (loginUser != null && loginUser.getMemberName() != null && !loginUser.getMemberName().isBlank()) {
            return loginUser.getMemberName();
        }
        return defaultString(request.getWriterName(), "비회원");
    }

    private String resolveWriterEmail(ChatbotInquiryRequest request, Login loginUser) {
        if (loginUser != null && loginUser.getEmail() != null && !loginUser.getEmail().isBlank()) {
            return loginUser.getEmail();
        }
        return defaultString(request.getWriterEmail(), "");
    }

    private String defaultString(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    private ChatbotFaqItem createFallbackAnswer(String question) {
        String loweredQuestion = question == null ? "" : question.toLowerCase(Locale.ROOT);

        Optional<Car> matchedCar = carRepository.findAll().stream()
                .filter(car -> loweredQuestion.contains(car.getModelName().toLowerCase(Locale.ROOT)))
                .findFirst();

        if (matchedCar.isPresent()) {
            Car car = matchedCar.get();
            return ChatbotFaqItem.builder()
                    .category("차량 안내")
                    .question(question)
                    .answer("%s의 1회 충전 주행가능거리는 %dkm이고, 배터리 용량은 %s입니다. 더 자세한 상담이 필요하시면 1:1 문의를 남겨주세요."
                            .formatted(car.getModelName(), car.getDrivingRangeKm(), car.getBatteryCapacity()))
                    .followUpQuestions(List.of(
                            "가격이 궁금해요",
                            "보조금 안내를 받고 싶어요",
                            "시승 상담을 신청하고 싶어요"))
                    .build();
        }

        return ChatbotFaqItem.builder()
                .category("기타")
                .question(question)
                .answer("현재 준비된 FAQ에서 정확한 답변을 찾지 못했습니다. 아래 1:1 문의로 남겨주시면 담당자가 확인 후 안내드리겠습니다.")
                .followUpQuestions(List.of(
                        "차량 추천을 받고 싶어요",
                        "보조금 안내를 받고 싶어요",
                        "충전 관련 문의를 하고 싶어요"))
                .build();
    }

    private List<ChatbotFaqItem> getFaqItems() {
        return chatbotFaqService.getFaqItems();
    }
}

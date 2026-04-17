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
                .greeting("\uC548\uB155\uD558\uC138\uC694. Kia EV \uCC57\uBD07 \uC0C1\uB2F4\uC785\uB2C8\uB2E4. \uC790\uC8FC \uBB3B\uB294 \uC9C8\uBB38\uACFC \uCD94\uCC9C \uC9C8\uBB38\uC744 \uD1B5\uD574 \uBE60\uB974\uAC8C \uB2F5\uBCC0\uC744 \uBC1B\uC544\uBCF4\uC138\uC694.")
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
                .category(defaultString(request.getCategory(), "\uAE30\uD0C0"))
                .writer_name(resolveWriterName(request, loginUser))
                .writer_email(resolveWriterEmail(request, loginUser))
                .content(defaultString(request.getContent(), ""))
                .is_answered("N")
                .chat_source(defaultString(request.getChatSource(), "\uC9C1\uC811\uC785\uB825"))
                .question_summary(defaultString(request.getQuestionSummary(), request.getCategory()))
                .status(defaultString(request.getStatus(), "\uC811\uC218"))
                .build();

        repository.save(chatInquiry);
    }

    private String resolveWriterName(ChatbotInquiryRequest request, Login loginUser) {
        if (loginUser != null && loginUser.getMemberName() != null && !loginUser.getMemberName().isBlank()) {
            return loginUser.getMemberName();
        }
        return defaultString(request.getWriterName(), "\uBE44\uD68C\uC6D0");
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
                    .category("\uCC28\uB7C9 \uC548\uB0B4")
                    .question(question)
                    .answer("%s\uC758 1\uD68C \uCDA9\uC804 \uC8FC\uD589\uAC00\uB2A5\uAC70\uB9AC\uB294 %dkm\uC774\uACE0, \uBC30\uD130\uB9AC \uC6A9\uB7C9\uC740 %s\uC785\uB2C8\uB2E4. \uB354 \uC790\uC138\uD55C \uC0C1\uB2F4\uC774 \uD544\uC694\uD558\uC2DC\uBA74 1:1 \uBB38\uC758\uB97C \uB0A8\uACA8\uC8FC\uC138\uC694."
                            .formatted(car.getModelName(), car.getDrivingRangeKm(), car.getBatteryCapacity()))
                    .followUpQuestions(List.of(
                            "\uAC00\uACA9\uC774 \uAD81\uAE08\uD574\uC694",
                            "\uBCF4\uC870\uAE08 \uC548\uB0B4\uB97C \uBC1B\uACE0 \uC2F6\uC5B4\uC694",
                            "\uC2DC\uC2B9 \uC0C1\uB2F4\uC744 \uC2E0\uCCAD\uD558\uACE0 \uC2F6\uC5B4\uC694"))
                    .build();
        }

        return ChatbotFaqItem.builder()
                .category("\uAE30\uD0C0")
                .question(question)
                .answer("\uD604\uC7AC \uC900\uBE44\uB41C FAQ\uC5D0\uC11C \uC815\uD655\uD55C \uB2F5\uBCC0\uC744 \uCC3E\uC9C0 \uBABB\uD588\uC2B5\uB2C8\uB2E4. \uC544\uB798 1:1 \uBB38\uC758\uB85C \uB0A8\uACA8\uC8FC\uC2DC\uBA74 \uB2F4\uB2F9\uC790\uAC00 \uD655\uC778 \uD6C4 \uC548\uB0B4\uB4DC\uB9AC\uACA0\uC2B5\uB2C8\uB2E4.")
                .followUpQuestions(List.of(
                        "\uCC28\uB7C9 \uCD94\uCC9C\uC744 \uBC1B\uACE0 \uC2F6\uC5B4\uC694",
                        "\uBCF4\uC870\uAE08 \uC548\uB0B4\uB97C \uBC1B\uACE0 \uC2F6\uC5B4\uC694",
                        "\uCDA9\uC804 \uAD00\uB828 \uBB38\uC758\uB97C \uD558\uACE0 \uC2F6\uC5B4\uC694"))
                .build();
    }

    private List<ChatbotFaqItem> getFaqItems() {
        return List.of(
                ChatbotFaqItem.builder()
                        .category("\uAD6C\uB9E4 \uC0C1\uB2F4")
                        .question("\uCC28\uB7C9 \uCD94\uCC9C\uC744 \uBC1B\uACE0 \uC2F6\uC5B4\uC694")
                        .answer("\uC8FC\uD589\uAC70\uB9AC\uC640 \uC608\uC0B0 \uC911\uC2EC\uC774\uBA74 EV3, \uD328\uBC00\uB9AC SUV\uB294 EV5 \uB610\uB294 EV9, \uD37C\uD3EC\uBA3C\uC2A4\uC640 \uC7A5\uAC70\uB9AC \uC8FC\uD589\uC744 \uD568\uAED8 \uBCF4\uC2E0\uB2E4\uBA74 EV6\uB97C \uB9CE\uC774 \uCC3E\uC73C\uC2ED\uB2C8\uB2E4. \uC6A9\uB3C4\uB97C \uB0A8\uACA8\uC8FC\uC2DC\uBA74 \uB354 \uC815\uD655\uD788 \uCD94\uCC9C\uD574\uB4DC\uB9B4 \uC218 \uC788\uC2B5\uB2C8\uB2E4.")
                        .followUpQuestions(List.of(
                                "EV3 \uAC00\uACA9\uC774 \uAD81\uAE08\uD574\uC694",
                                "EV6 \uC8FC\uD589\uAC70\uB9AC\uAC00 \uAD81\uAE08\uD574\uC694",
                                "\uC2DC\uC2B9 \uC0C1\uB2F4\uC744 \uC2E0\uCCAD\uD558\uACE0 \uC2F6\uC5B4\uC694"))
                        .build(),
                ChatbotFaqItem.builder()
                        .category("\uAD6C\uB9E4 \uC0C1\uB2F4")
                        .question("\uC2DC\uC2B9 \uC0C1\uB2F4\uC744 \uC2E0\uCCAD\uD558\uACE0 \uC2F6\uC5B4\uC694")
                        .answer("\uC2DC\uC2B9 \uBC0F \uAD6C\uB9E4 \uC0C1\uB2F4\uC740 \uC0C1\uB2F4 \uC2E0\uCCAD \uB610\uB294 1:1 \uBB38\uC758\uB85C \uC811\uC218\uD574\uC8FC\uC2DC\uBA74 \uB2F4\uB2F9\uC790\uAC00 \uC21C\uCC28\uC801\uC73C\uB85C \uC5F0\uB77D\uB4DC\uB9BD\uB2C8\uB2E4. \uD76C\uB9DD \uCC28\uC885\uACFC \uC5F0\uB77D \uAC00\uB2A5 \uC2DC\uAC04\uC744 \uD568\uAED8 \uB0A8\uACA8\uC8FC\uC2DC\uBA74 \uB354 \uBE60\uB974\uAC8C \uB3C4\uC640\uB4DC\uB9B4 \uC218 \uC788\uC2B5\uB2C8\uB2E4.")
                        .followUpQuestions(List.of(
                                "\uCC28\uB7C9 \uCD94\uCC9C\uC744 \uBC1B\uACE0 \uC2F6\uC5B4\uC694",
                                "\uCD9C\uACE0 \uAE30\uAC04\uC774 \uAD81\uAE08\uD574\uC694",
                                "\uBCF4\uC870\uAE08 \uC548\uB0B4\uB97C \uBC1B\uACE0 \uC2F6\uC5B4\uC694"))
                        .build(),
                ChatbotFaqItem.builder()
                        .category("\uAC00\uACA9/\uD61C\uD0DD")
                        .question("\uBCF4\uC870\uAE08 \uC548\uB0B4\uB97C \uBC1B\uACE0 \uC2F6\uC5B4\uC694")
                        .answer("\uC804\uAE30\uCC28 \uBCF4\uC870\uAE08\uC740 \uCC28\uC885\uACFC \uAC70\uC8FC \uC9C0\uC5ED, \uC2E0\uCCAD \uC2DC\uC810\uC5D0 \uB530\uB77C \uB2EC\uB77C\uC9D1\uB2C8\uB2E4. \uC815\uD655\uD55C \uAE08\uC561\uC740 \uCD5C\uC2E0 \uC9C0\uC790\uCCB4 \uACF5\uACE0 \uAE30\uC900\uC73C\uB85C \uD655\uC778\uD574\uC57C \uD558\uBA70, \uC6D0\uD558\uC2DC\uBA74 \uCC28\uC885\uACFC \uC9C0\uC5ED\uC744 1:1 \uBB38\uC758\uB85C \uB0A8\uACA8\uC8FC\uC138\uC694.")
                        .followUpQuestions(List.of(
                                "\uAC00\uACA9\uC774 \uAD81\uAE08\uD574\uC694",
                                "\uCD9C\uACE0 \uAE30\uAC04\uC774 \uAD81\uAE08\uD574\uC694",
                                "\uCC28\uB7C9 \uCD94\uCC9C\uC744 \uBC1B\uACE0 \uC2F6\uC5B4\uC694"))
                        .build(),
                ChatbotFaqItem.builder()
                        .category("\uAC00\uACA9/\uD61C\uD0DD")
                        .question("\uAC00\uACA9\uC774 \uAD81\uAE08\uD574\uC694")
                        .answer("\uCC28\uB7C9 \uAC00\uACA9\uC740 \uD2B8\uB9BC\uACFC \uC635\uC158\uC5D0 \uB530\uB77C \uB2EC\uB77C\uC9D1\uB2C8\uB2E4. \uCC28\uB7C9 \uBAA9\uB85D \uD398\uC774\uC9C0\uC5D0\uC11C \uAE30\uBCF8 \uAC00\uACA9\uC744 \uD655\uC778\uD558\uC2E4 \uC218 \uC788\uACE0, \uC2E4\uC81C \uAD6C\uB9E4 \uACAC\uC801\uC740 \uC635\uC158\uACFC \uBCF4\uC870\uAE08 \uBC18\uC601 \uD6C4 \uC0C1\uB2F4\uC73C\uB85C \uC548\uB0B4\uBC1B\uC73C\uC2DC\uB294 \uAC83\uC774 \uAC00\uC7A5 \uC815\uD655\uD569\uB2C8\uB2E4.")
                        .followUpQuestions(List.of(
                                "\uBCF4\uC870\uAE08 \uC548\uB0B4\uB97C \uBC1B\uACE0 \uC2F6\uC5B4\uC694",
                                "\uC2DC\uC2B9 \uC0C1\uB2F4\uC744 \uC2E0\uCCAD\uD558\uACE0 \uC2F6\uC5B4\uC694",
                                "\uCC28\uB7C9 \uCD94\uCC9C\uC744 \uBC1B\uACE0 \uC2F6\uC5B4\uC694"))
                        .build(),
                ChatbotFaqItem.builder()
                        .category("\uCD9C\uACE0/\uACC4\uC57D")
                        .question("\uCD9C\uACE0 \uAE30\uAC04\uC774 \uAD81\uAE08\uD574\uC694")
                        .answer("\uCD9C\uACE0 \uAE30\uAC04\uC740 \uCC28\uC885, \uD2B8\uB9BC, \uC0C9\uC0C1, \uC635\uC158, \uC7AC\uACE0 \uC0C1\uD669\uC5D0 \uB530\uB77C \uB2EC\uB77C\uC9D1\uB2C8\uB2E4. \uCD5C\uC2E0 \uC77C\uC815\uC740 \uC0C1\uB2F4 \uC811\uC218 \uD6C4 \uD655\uC778\uD558\uB294 \uBC29\uC2DD\uC774 \uAC00\uC7A5 \uC815\uD655\uD569\uB2C8\uB2E4.")
                        .followUpQuestions(List.of(
                                "\uCC28\uB7C9 \uCD94\uCC9C\uC744 \uBC1B\uACE0 \uC2F6\uC5B4\uC694",
                                "\uBCF4\uC870\uAE08 \uC548\uB0B4\uB97C \uBC1B\uACE0 \uC2F6\uC5B4\uC694",
                                "\uC2DC\uC2B9 \uC0C1\uB2F4\uC744 \uC2E0\uCCAD\uD558\uACE0 \uC2F6\uC5B4\uC694"))
                        .build(),
                ChatbotFaqItem.builder()
                        .category("\uCDA9\uC804")
                        .question("\uCDA9\uC804 \uAD00\uB828 \uBB38\uC758\uB97C \uD558\uACE0 \uC2F6\uC5B4\uC694")
                        .answer("\uCDA9\uC804\uC740 \uCDA9\uC804\uC18C \uC704\uCE58, \uAE09\uC18D \uCDA9\uC804 \uC9C0\uC6D0 \uC5EC\uBD80, \uCC28\uB7C9\uBCC4 \uBC30\uD130\uB9AC \uC2A4\uD399\uC5D0 \uB530\uB77C \uACBD\uD5D8\uC774 \uB2EC\uB77C\uC9D1\uB2C8\uB2E4. \uCDA9\uC804\uC18C \uCC3E\uAE30 \uBA54\uB274\uB97C \uC774\uC6A9\uD558\uC2DC\uAC70\uB098 \uCC28\uC885\uC744 \uB0A8\uACA8\uC8FC\uC2DC\uBA74 \uB354 \uC790\uC138\uD788 \uC548\uB0B4\uB4DC\uB9B4 \uC218 \uC788\uC2B5\uB2C8\uB2E4.")
                        .followUpQuestions(List.of(
                                "EV6 \uC8FC\uD589\uAC70\uB9AC\uAC00 \uAD81\uAE08\uD574\uC694",
                                "\uBCF4\uC870\uAE08 \uC548\uB0B4\uB97C \uBC1B\uACE0 \uC2F6\uC5B4\uC694",
                                "\uCC28\uB7C9 \uCD94\uCC9C\uC744 \uBC1B\uACE0 \uC2F6\uC5B4\uC694"))
                        .build(),
                ChatbotFaqItem.builder()
                        .category("\uCC28\uB7C9 \uC548\uB0B4")
                        .question("EV6 \uC8FC\uD589\uAC70\uB9AC\uAC00 \uAD81\uAE08\uD574\uC694")
                        .answer("EV6\uB294 \uC7A5\uAC70\uB9AC \uC8FC\uD589\uC5D0 \uAC15\uC810\uC774 \uC788\uB294 \uB300\uD45C \uBAA8\uB378\uC785\uB2C8\uB2E4. \uC138\uBD80 \uD2B8\uB9BC\uBCC4 \uC218\uCE58\uB294 \uCC28\uB7C9 \uC0C1\uC138 \uD398\uC774\uC9C0\uC5D0\uC11C \uBC30\uD130\uB9AC\uC640 \uC8FC\uD589\uAC00\uB2A5\uAC70\uB9AC\uB97C \uD568\uAED8 \uD655\uC778\uD558\uC2E4 \uC218 \uC788\uC2B5\uB2C8\uB2E4.")
                        .followUpQuestions(List.of(
                                "\uAC00\uACA9\uC774 \uAD81\uAE08\uD574\uC694",
                                "\uCDA9\uC804 \uAD00\uB828 \uBB38\uC758\uB97C \uD558\uACE0 \uC2F6\uC5B4\uC694",
                                "\uC2DC\uC2B9 \uC0C1\uB2F4\uC744 \uC2E0\uCCAD\uD558\uACE0 \uC2F6\uC5B4\uC694"))
                        .build(),
                ChatbotFaqItem.builder()
                        .category("\uCC28\uB7C9 \uC548\uB0B4")
                        .question("EV3 \uAC00\uACA9\uC774 \uAD81\uAE08\uD574\uC694")
                        .answer("EV3 \uAC00\uACA9\uC740 \uD2B8\uB9BC\uACFC \uC635\uC158\uC5D0 \uB530\uB77C \uB2EC\uB77C\uC9D1\uB2C8\uB2E4. \uCC28\uB7C9 \uBAA9\uB85D\uC5D0\uC11C \uAE30\uBCF8 \uAC00\uACA9\uC744 \uD655\uC778\uD55C \uB4A4, \uBCF4\uC870\uAE08\uACFC \uC635\uC158\uC744 \uBC18\uC601\uD55C \uC2E4\uC81C \uACAC\uC801\uC740 \uC0C1\uB2F4\uC744 \uD1B5\uD574 \uBC1B\uC544\uBCF4\uC2DC\uB294 \uAC83\uC744 \uAD8C\uC7A5\uB4DC\uB9BD\uB2C8\uB2E4.")
                        .followUpQuestions(List.of(
                                "\uBCF4\uC870\uAE08 \uC548\uB0B4\uB97C \uBC1B\uACE0 \uC2F6\uC5B4\uC694",
                                "\uCC28\uB7C9 \uCD94\uCC9C\uC744 \uBC1B\uACE0 \uC2F6\uC5B4\uC694",
                                "\uCD9C\uACE0 \uAE30\uAC04\uC774 \uAD81\uAE08\uD574\uC694"))
                        .build());
    }
}

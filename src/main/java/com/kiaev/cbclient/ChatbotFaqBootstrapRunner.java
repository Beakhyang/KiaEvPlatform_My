package com.kiaev.cbclient;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatbotFaqBootstrapRunner implements CommandLineRunner {

    private final ChatbotFaqService chatbotFaqService;

    @Override
    public void run(String... args) {
        if (!chatbotFaqService.getAllFaqs().isEmpty()) {
            return;
        }

        seedFaq("구매 상담", "차량 추천을 받고 싶어요",
                "주행거리와 예산 중심이면 EV3, 패밀리 SUV는 EV5 또는 EV9, 퍼포먼스와 장거리 주행을 함께 보신다면 EV6를 많이 찾으십니다. 용도를 남겨주시면 더 정확히 추천해드릴 수 있습니다.",
                1,
                List.of("EV3 가격이 궁금해요", "EV6 주행거리가 궁금해요", "시승 상담을 신청하고 싶어요"));

        seedFaq("구매 상담", "시승 상담을 신청하고 싶어요",
                "시승 및 구매 상담은 상담 신청 또는 1:1 문의로 접수해주시면 담당자가 순차적으로 연락드립니다. 희망 차종과 연락 가능한 시간을 함께 남겨주시면 더 빠르게 도와드릴 수 있습니다.",
                2,
                List.of("차량 추천을 받고 싶어요", "출고 기간이 궁금해요", "보조금 안내를 받고 싶어요"));

        seedFaq("가격/혜택", "보조금 안내를 받고 싶어요",
                "전기차 보조금은 차종과 거주 지역, 신청 시점에 따라 달라집니다. 정확한 금액은 최신 지자체 공고 기준으로 확인해야 하며, 원하시면 차종과 지역을 1:1 문의로 남겨주세요.",
                3,
                List.of("가격이 궁금해요", "출고 기간이 궁금해요", "차량 추천을 받고 싶어요"));

        seedFaq("가격/혜택", "가격이 궁금해요",
                "차량 가격은 트림과 옵션에 따라 달라집니다. 차량 목록 페이지에서 기본 가격을 확인하실 수 있고, 실제 구매 견적은 옵션과 보조금 반영 후 상담으로 안내받으시는 것이 가장 정확합니다.",
                4,
                List.of("보조금 안내를 받고 싶어요", "시승 상담을 신청하고 싶어요", "차량 추천을 받고 싶어요"));

        seedFaq("출고/계약", "출고 기간이 궁금해요",
                "출고 기간은 차종, 트림, 색상, 옵션, 재고 상황에 따라 달라집니다. 최신 일정은 상담 접수 후 확인하는 방식이 가장 정확합니다.",
                5,
                List.of("차량 추천을 받고 싶어요", "보조금 안내를 받고 싶어요", "시승 상담을 신청하고 싶어요"));

        seedFaq("충전", "충전 관련 문의를 하고 싶어요",
                "충전은 충전소 위치, 급속 충전 지원 여부, 차량별 배터리 스펙에 따라 경험이 달라집니다. 충전소 찾기 메뉴를 이용하시거나 차종을 남겨주시면 더 자세히 안내드릴 수 있습니다.",
                6,
                List.of("EV6 주행거리가 궁금해요", "보조금 안내를 받고 싶어요", "차량 추천을 받고 싶어요"));

        seedFaq("차량 안내", "EV6 주행거리가 궁금해요",
                "EV6는 장거리 주행에 강점이 있는 대표 모델입니다. 세부 트림별 수치는 차량 상세 페이지에서 배터리와 주행가능거리를 함께 확인하실 수 있습니다.",
                7,
                List.of("가격이 궁금해요", "충전 관련 문의를 하고 싶어요", "시승 상담을 신청하고 싶어요"));

        seedFaq("차량 안내", "EV3 가격이 궁금해요",
                "EV3 가격은 트림과 옵션에 따라 달라집니다. 차량 목록에서 기본 가격을 확인한 뒤, 보조금과 옵션을 반영한 실제 견적은 상담을 통해 받아보시는 것을 권장드립니다.",
                8,
                List.of("보조금 안내를 받고 싶어요", "차량 추천을 받고 싶어요", "출고 기간이 궁금해요"));
    }

    private void seedFaq(String category, String question, String answer, int order, List<String> followUps) {
        ChatbotFaq faq = new ChatbotFaq();
        faq.setCategory(category);
        faq.setQuestion(question);
        faq.setAnswer(answer);
        faq.setDisplayOrder(order);
        faq.setActiveYn("Y");
        faq.setFollowUpQuestions(String.join("\n", followUps));
        chatbotFaqService.save(faq);
    }
}

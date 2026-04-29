package com.kiaev.cbclient;

import java.util.LinkedHashSet;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatbotFaqService {

    private final ChatbotFaqRepository chatbotFaqRepository;

    public List<ChatbotFaq> getAllFaqs() {
        return chatbotFaqRepository.findAll(Sort.by(Sort.Order.asc("displayOrder"), Sort.Order.asc("faqNo")));
    }

    public List<ChatbotFaq> getActiveFaqs() {
        return chatbotFaqRepository.findByActiveYn("Y",
                Sort.by(Sort.Order.asc("displayOrder"), Sort.Order.asc("faqNo")));
    }

    public ChatbotFaq getFaq(Long faqNo) {
        return chatbotFaqRepository.findById(faqNo)
                .orElseThrow(() -> new IllegalArgumentException("FAQ를 찾을 수 없습니다. ID: " + faqNo));
    }

    public ChatbotFaq save(ChatbotFaq faq) {
        if (faq.getDisplayOrder() == null) {
            faq.setDisplayOrder(0);
        }

        if (faq.getActiveYn() == null || faq.getActiveYn().isBlank()) {
            faq.setActiveYn("Y");
        }

        return chatbotFaqRepository.save(faq);
    }

    public void delete(Long faqNo) {
        chatbotFaqRepository.deleteById(faqNo);
    }

    public List<ChatbotFaqItem> getFaqItems() {
        return getActiveFaqs().stream()
                .map(this::toItem)
                .toList();
    }

    public List<String> getCategories() {
        return getActiveFaqs().stream()
                .map(ChatbotFaq::getCategory)
                .collect(java.util.stream.Collectors.collectingAndThen(
                        java.util.stream.Collectors.toCollection(LinkedHashSet::new), List::copyOf));
    }

    public List<String> getRecommendedQuestions(int limit) {
        return getActiveFaqs().stream()
                .map(ChatbotFaq::getQuestion)
                .limit(limit)
                .toList();
    }

    private ChatbotFaqItem toItem(ChatbotFaq faq) {
        return ChatbotFaqItem.builder()
                .category(faq.getCategory())
                .question(faq.getQuestion())
                .answer(faq.getAnswer())
                .followUpQuestions(faq.getFollowUpQuestionList())
                .build();
    }
}

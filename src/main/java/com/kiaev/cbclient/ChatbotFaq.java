package com.kiaev.cbclient;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "CHATBOT_FAQ_TBL")
@Getter
@Setter
@NoArgsConstructor
public class ChatbotFaq {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "faq_no")
    private Long faqNo;

    @Column(name = "category", nullable = false, length = 50)
    private String category;

    @Column(name = "question", nullable = false, length = 255)
    private String question;

    @Column(name = "answer", nullable = false, length = 4000)
    private String answer;

    @Column(name = "follow_up_questions", length = 1000)
    private String followUpQuestions;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    @Column(name = "active_yn", nullable = false, length = 1)
    private String activeYn = "Y";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public List<String> getFollowUpQuestionList() {
        if (followUpQuestions == null || followUpQuestions.isBlank()) {
            return List.of();
        }

        return Arrays.stream(followUpQuestions.split("\\r?\\n"))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .collect(Collectors.toList());
    }
}

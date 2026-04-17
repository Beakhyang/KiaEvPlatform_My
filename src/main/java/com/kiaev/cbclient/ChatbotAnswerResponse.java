package com.kiaev.cbclient;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatbotAnswerResponse {

    private String category;
    private String question;
    private String answer;
    private List<String> followUpQuestions;
}

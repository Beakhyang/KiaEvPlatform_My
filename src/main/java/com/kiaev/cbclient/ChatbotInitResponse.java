package com.kiaev.cbclient;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatbotInitResponse {

    private String greeting;
    private boolean loggedIn;
    private String memberName;
    private String memberEmail;
    private List<String> categories;
    private List<String> recommendedQuestions;
    private List<ChatbotCarOption> cars;
}

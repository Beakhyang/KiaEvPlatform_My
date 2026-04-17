package com.kiaev.cbclient;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatbotAiInitResponse {

    private String greeting;

    private boolean available;

    private String provider;

    private List<String> suggestedQuestions;
}

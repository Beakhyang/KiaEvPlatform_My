package com.kiaev.cbclient;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatbotAiRequest {

    private String message;

    private List<ChatbotAiMessage> history;
}

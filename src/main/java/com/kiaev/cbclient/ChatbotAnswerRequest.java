package com.kiaev.cbclient;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatbotAnswerRequest {

    private String question;

    private String category;
}

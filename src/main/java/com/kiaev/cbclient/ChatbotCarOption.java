package com.kiaev.cbclient;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatbotCarOption {

    private Long carNo;
    private String modelName;
}

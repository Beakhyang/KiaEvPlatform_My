package com.kiaev.cbclient;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatbotInquiryRequest {

    private Long carNo;
    private String category;
    private String writerName;
    private String writerEmail;
    private String content;
    private String chatSource;
    private String questionSummary;
    private String status;
}

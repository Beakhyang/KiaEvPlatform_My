package com.kiaev.cbclient;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChatbotPageController {

    @GetMapping("/chatbot/main")
    public String chatbotMain() {
        return "client/chatbot/main";
    }
}

package com.kiaev.cbclient;

import com.kiaev.client.login.Login;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chatbot")
@RequiredArgsConstructor
public class ChatbotClientRestController {

    private final ChatbotClientService chatbotClientService;

    @GetMapping("/init")
    public ResponseEntity<ChatbotInitResponse> init(HttpSession session) {
        Login loginUser = (Login) session.getAttribute("loginUser");
        return ResponseEntity.ok(chatbotClientService.getInitialData(loginUser));
    }

    @PostMapping("/answer")
    public ResponseEntity<ChatbotAnswerResponse> answer(@RequestBody ChatbotAnswerRequest request) {
        return ResponseEntity.ok(chatbotClientService.answer(request));
    }

    @PostMapping("/inquiry")
    public ResponseEntity<String> saveInquiry(@RequestBody ChatbotInquiryRequest request, HttpSession session) {
        try {
            Login loginUser = (Login) session.getAttribute("loginUser");
            chatbotClientService.registerInquiry(request, loginUser);
            return ResponseEntity.ok("문의가 성공적으로 접수되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("오류 발생: " + e.getMessage());
        }
    }
}

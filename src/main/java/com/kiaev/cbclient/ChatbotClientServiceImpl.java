package com.kiaev.cbclient;

import com.kiaev.client.login.Login;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("legacyChatbotClientService")
@RequiredArgsConstructor
public class ChatbotClientServiceImpl implements ChatbotClientService {

    private final ChatbotClientRepository repository;

    @Override
    @Transactional(readOnly = true)
    public ChatbotInitResponse getInitialData(Login loginUser) {
        throw new UnsupportedOperationException("Use primary chatbot service");
    }

    @Override
    @Transactional(readOnly = true)
    public ChatbotAnswerResponse answer(ChatbotAnswerRequest request) {
        throw new UnsupportedOperationException("Use primary chatbot service");
    }

    @Override
    @Transactional
    public void registerInquiry(ChatbotInquiryRequest request, Login loginUser) {
        ChatInquiry chatInquiry = ChatInquiry.builder()
                .member_no(loginUser != null ? loginUser.getMemberNo() : null)
                .car_no(request.getCarNo())
                .category(request.getCategory())
                .writer_name(loginUser != null ? loginUser.getMemberName() : request.getWriterName())
                .writer_email(loginUser != null ? loginUser.getEmail() : request.getWriterEmail())
                .content(request.getContent())
                .build();

        repository.save(chatInquiry);
    }

    private void sendMail(ChatInquiry chatInquiry) {
        // legacy placeholder
    }
}

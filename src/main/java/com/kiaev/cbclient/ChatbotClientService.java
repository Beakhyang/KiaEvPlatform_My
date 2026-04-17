package com.kiaev.cbclient;

import com.kiaev.client.login.Login;

public interface ChatbotClientService {

	ChatbotInitResponse getInitialData(Login loginUser);

	ChatbotAnswerResponse answer(ChatbotAnswerRequest request);

	void registerInquiry(ChatbotInquiryRequest request, Login loginUser);
}

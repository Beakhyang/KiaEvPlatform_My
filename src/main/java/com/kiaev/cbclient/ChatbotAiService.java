package com.kiaev.cbclient;

import com.kiaev.client.login.Login;

public interface ChatbotAiService {

    ChatbotAiInitResponse getInitialData(Login loginUser);

    ChatbotAiResponse answer(ChatbotAiRequest request, Login loginUser);
}

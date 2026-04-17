package com.kiaev.client.charging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/charging")
public class ChargingMapController {

    // 환경변수(application.properties)에 저장해둔 카카오맵 API 키를 가져옵니다.
    @Value("${api.key.kakao-map}")
    private String kakaoApiKey;

    @GetMapping("/map")
    public String showMap(Model model) {
        // Thymeleaf 템플릿 엔진으로 카카오 API 키를 넘겨줍니다.
        model.addAttribute("kakaoApiKey", kakaoApiKey);
        
        // src/main/resources/templates/charging/map.html 파일을 화면에 렌더링합니다.
        return "client/charging/map"; 
    }
}
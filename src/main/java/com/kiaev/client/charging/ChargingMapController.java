package com.kiaev.client.charging;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.kiaev.common.aws.AwsSecretsBootstrap;

@Controller
@RequestMapping("/charging")
public class ChargingMapController {

    @Value("${api.key.kakao-map:}")
    private String kakaoApiKey;

    @GetMapping("/map")
    public String showMap(Model model) {
        model.addAttribute("kakaoApiKey", resolveKakaoApiKey());
        return "client/charging/map";
    }

    private String resolveKakaoApiKey() {
        if (hasText(kakaoApiKey)) {
            return kakaoApiKey.trim();
        }

        String resolvedSecretValue = AwsSecretsBootstrap.resolveValue(
                "api.key.kakao-map",
                "KAKAO_API_KEY",
                "Kakao API Key");
        if (hasText(resolvedSecretValue)) {
            return resolvedSecretValue.trim();
        }

        for (String propertyName : new String[] { "api.key.kakao-map", "KAKAO_API_KEY" }) {
            String value = System.getProperty(propertyName);
            if (hasText(value)) {
                return value.trim();
            }
        }

        String envValue = System.getenv("KAKAO_API_KEY");
        if (hasText(envValue)) {
            return envValue.trim();
        }

        return "";
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}

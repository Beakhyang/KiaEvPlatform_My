package com.kiaev.client.promotion;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 1. 브라우저에서 /images/promotion/... 으로 접속하면
        registry.addResourceHandler("/images/promotion/**")
                // 2. C드라이브의 프로젝트 워크스페이스 실제 경로에서 파일을 바로 찾아라! (서버 재시작 없이 즉시 반영됨)
                .addResourceLocations("file:///C:/SpringBootProject/workspace/KiaEvPlatform/src/main/resources/static/images/promotion/"); 
    }
}
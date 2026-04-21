package com.kiaev.client.promotion;

import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String projectPromotionDir = Paths.get("src/main/resources/static/images/promotion")
                .toAbsolutePath()
                .normalize()
                .toUri()
                .toString();

        registry.addResourceHandler("/images/promotion/**")
                .addResourceLocations(
                        projectPromotionDir,
                        "classpath:/static/images/promotion/");
    }
}

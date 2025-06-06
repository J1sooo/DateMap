package com.est.back.user;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${upload.path.profile-images}")
    private String uploadPath; // application.properties에서 설정한 경로

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/profile/**") // 웹에서 요청하는 URL 패턴
                .addResourceLocations("file:" + uploadPath);
    }
}
package com.est.back.alan;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class AlanService {
    private final WebClient webClient;

    @Value("${ALAN_CLIENT_ID}")
    private String ALAN_CLIENT_ID;

    public AlanService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://kdt-api-function.azurewebsites.net/api/v1/question").build();
    }

    public String AlanAiResponse(String content) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("content", content)
                        .queryParam("client_id", ALAN_CLIENT_ID)
                        .build()
                )
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}

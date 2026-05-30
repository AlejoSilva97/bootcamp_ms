package com.example.bootcamp.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${adapters.capacity-ms.url}")
    private String capacityServiceUrl;

    @Bean
    public WebClient capacityWebClient(WebClient.Builder builder) {
        return builder
                .baseUrl(capacityServiceUrl)
                .build();
    }
}
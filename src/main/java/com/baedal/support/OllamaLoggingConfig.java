package com.baedal.support;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;

@Configuration
public class OllamaLoggingConfig {

    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder()
                .requestInterceptor((request, body, execution) -> {
                    System.out.println("=== 실제 Ollama 요청 ===");
                    System.out.println(new String(body, StandardCharsets.UTF_8));
                    return execution.execute(request, body);
                });
    }
}

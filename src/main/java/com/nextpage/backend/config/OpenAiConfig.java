package com.nextpage.backend.config;

import com.theokanning.openai.service.OpenAiService;
// import com.nextpage.backend.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Duration;

@Configuration
public class OpenAiConfig {

    @Value("${GPT_API_KEY}")
    private String apiKey;

    @Bean
    public OpenAiService getOpenAiService() {
        return new OpenAiService(apiKey, Duration.ofSeconds(30));
    }
}
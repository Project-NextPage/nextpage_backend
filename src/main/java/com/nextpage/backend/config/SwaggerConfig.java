package com.nextpage.backend.config;

import com.theokanning.openai.service.OpenAiService;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.time.Duration;

@Configuration
public class SwaggerConfig {

    @Value("${GPT_API_KEY}")
    private String apiKey;

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("NextPage API")
                .description("[ Base URL: http://localhost:8080/api/v2]\n\nNextPage의 API 문서")
                .version("2.0.0");
    }

    @Bean
    public OpenAiService getOpenAiService() {
        return new OpenAiService(apiKey, Duration.ofSeconds(30));
    }

}
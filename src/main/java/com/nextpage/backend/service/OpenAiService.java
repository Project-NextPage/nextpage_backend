package com.nextpage.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OpenAiService {

    private final WebClient webClient;
    private final String apiKey;

    @Autowired
    public OpenAiService(WebClient.Builder webClientBuilder, @Value("${openai.api.key}") String apiKey) {
        this.webClient = webClientBuilder.baseUrl("https://api.openai.com/v1/").build();
        this.apiKey = apiKey;
    }

    public Mono<String> generateImage(String content) {
        String promptKeyword = "Design: a detailed digital illustration drawn with bright colors and clean lines. Please make the following images according to the previous requirements: ";
        String conditions = "When generating an image, be sure to observe the following conditions: Do not add text to the image. I want an illustration image, not contain text in the image";

        String promptImage = conditions + "\n" + promptKeyword + content;

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("prompt", promptImage);
        requestBody.put("n", 1);
        requestBody.put("size", "1024x1024");
        requestBody.put("model", "dall-e-3"); // 모델 버전 지정
        /* todo
            webclient를 사용한 이유가 있나요? feign client나 rest template 같은 선택지도 있습니다.
            고민 해보고 관련 글을 블로그로 작성하면 좋습니다.
            ps.. 사실 webclient를 사용 하는 것은 좋은 방법이지만.. 기술을 선택한 이유도 알아두면 좋아서 코멘트 남깁니다.
         */
        return this.webClient.post()
                .uri("/images/generations")
                .header("Authorization", "Bearer " + this.apiKey)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> extractImageUrl(response));
    }

    private String extractImageUrl(Map<String, Object> response) {
        List<Map<String, Object>> images = (List<Map<String, Object>>) response.get("data");
        if (images != null && !images.isEmpty()) {
            Map<String, Object> image = images.get(0);
            return (String) image.get("url");
        }
        return "이미지 생성 실패";
    }
}


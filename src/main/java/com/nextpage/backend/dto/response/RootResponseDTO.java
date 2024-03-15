package com.nextpage.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RootResponseDTO {
    private List<StoryInfo> stories;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StoryInfo {
        private Long id;
        private String userNickname;
        private String content;
        private String imageUrl;
        private LocalDateTime createdAt;
    }
}

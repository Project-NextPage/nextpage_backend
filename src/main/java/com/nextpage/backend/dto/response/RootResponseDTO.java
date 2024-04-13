package com.nextpage.backend.dto.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RootResponseDTO {
    private Long id;
    private String userNickname;
    private String content;
    private String imageUrl;
    private LocalDateTime createdAt;

    // 아래 코드는 @AllArgsConstructor로 대체 가능
    public RootResponseDTO(Long id, String userNickname, String content, String imageUrl, LocalDateTime createdAt) {
        this.id = id;
        this.userNickname = userNickname;
        this.content = content;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
    }
}
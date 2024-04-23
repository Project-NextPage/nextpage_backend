package com.nextpage.backend.dto.response;

import com.nextpage.backend.entity.Story;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RootResponseDTO {
    private Long id;
    private String userNickname;
    private String content;
    private String imageUrl;
    private LocalDateTime createdAt;

    // 아래 코드는 @AllArgsConstructor로 대체 가능 -> @Builder로 빌더 패턴까지 한 번에 작성 가능
    @Builder
    public RootResponseDTO(Long id, String userNickname, String content, String imageUrl) {
        this.id = id;
        this.userNickname = userNickname;
        this.content = content;
        this.imageUrl = imageUrl;
        this.createdAt = LocalDateTime.now();
    }

    public static RootResponseDTO of(Story story) {
        return RootResponseDTO.builder()
                .id(story.getId())
                .userNickname(story.getUserNickname())
                .content(story.getContent())
                .imageUrl(story.getImageUrl())
                .build();
    }

}
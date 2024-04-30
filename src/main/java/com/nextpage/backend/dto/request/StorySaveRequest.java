package com.nextpage.backend.dto.request;

import com.nextpage.backend.entity.Story;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class StorySaveRequest {

    @NotBlank(message = "imageUrl은 필수입니다.")
    private final String imageUrl;
    @NotBlank(message = "content는 필수입니다.")
    private final String content;

    public Story toEntity(String userNickname, String imageUrl, Story parentId) {
        LocalDateTime now = LocalDateTime.now();
        return new Story(
                content,
                imageUrl,
                now,
                now,
                false,
                userNickname,
                parentId
        );
    }
}

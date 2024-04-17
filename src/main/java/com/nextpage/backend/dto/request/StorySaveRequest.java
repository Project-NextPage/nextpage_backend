package com.nextpage.backend.dto.request;

import com.nextpage.backend.entity.Story;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class StorySaveRequest {

    @NotEmpty(message = "imageurl은 필수입니다.")
    private final String imageUrl;
    @NotEmpty(message = "content는 필수입니다.")
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

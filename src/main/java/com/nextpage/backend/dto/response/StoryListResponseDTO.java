package com.nextpage.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class StoryListResponseDTO {
    private Long id;
    private String content;
    private String imageUrl;
    private String userNickname;

}

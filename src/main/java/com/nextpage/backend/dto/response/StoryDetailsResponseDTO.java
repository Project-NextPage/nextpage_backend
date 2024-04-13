package com.nextpage.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class StoryDetailsResponseDTO {
    private Long id;
    private String content;
    private String imageUrl;
    private String userNickname;
    private Long parentId;
    private List<Long> childId;
    private List<String> childContent;
}

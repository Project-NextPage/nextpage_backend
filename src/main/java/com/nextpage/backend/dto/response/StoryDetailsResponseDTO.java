package com.nextpage.backend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor // 생성자 자동 생성 - 모든 필드 초기화 역할
@NoArgsConstructor // json 형식의 데이터를 DTO로 변환할 때 사용
public class StoryDetailsResponseDTO {
    private Long id;
    private String content;
    private String imageUrl;
    private String userNickname;
    private Long parentId;
    private List<Long> childId;
    private List<String> childContent;
}

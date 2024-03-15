package com.nextpage.backend.service;

import com.nextpage.backend.dto.response.StoryDetailsResponseDTO;
import com.nextpage.backend.entity.Story;
import com.nextpage.backend.repository.StoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class StoryService {

    private final StoryRepository storyRepository;

    public StoryService(StoryRepository storyRepository) {
        this.storyRepository = storyRepository;
    }

    public List<Story> getRootStories() { // parentId가 없는 루트 스토리들 조회
        return storyRepository.findRootStories();
    }

    public StoryDetailsResponseDTO getStoryDetails(Long storyId) { // 스토리 상세 조회
        // storyId로 스토리 찾기
        Story story = storyRepository.findById(storyId).orElseThrow(() -> new NoSuchElementException("해당 ID의 스토리를 찾을 수 없습니다 [id: " + storyId + "]"));

        // 스토리 내용을 포함한 응답 객체 생성
        StoryDetailsResponseDTO responseDTO = new StoryDetailsResponseDTO();
        responseDTO.setId(story.getId());
        responseDTO.setContent(story.getContent());
        responseDTO.setImageUrl(story.getImageUrl());
        responseDTO.setUserNickname(story.getUserNickname());

        // 부모 자식 스토리의 ID와 content
        responseDTO.setParentId(story.getParentId() != null ? story.getParentId().getId() : null);
        List<Long> childIds = story.getChildId().stream().map(Story::getId).collect(Collectors.toList());
        responseDTO.setChildId(childIds);
        List<String> childContents = story.getChildId().stream().map(Story::getContent).collect(Collectors.toList());
        responseDTO.setChildContent(childContents);

        return responseDTO;
    }
}

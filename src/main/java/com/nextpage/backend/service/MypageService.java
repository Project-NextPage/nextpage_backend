package com.nextpage.backend.service;

import com.nextpage.backend.dto.response.ScenarioResponseDTO;
import com.nextpage.backend.dto.response.StoryListResponseDTO;
import com.nextpage.backend.entity.Story;
import com.nextpage.backend.repository.StoryRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class MypageService {
    private final StoryRepository storyRepository;

    public MypageService(StoryRepository storyRepository) {
        this.storyRepository = storyRepository;
    }

    public List<StoryListResponseDTO> getStoriesByNickname(String nickname) { //내가 작성한 스토리 조회
        List<Story> result= storyRepository.findStoriesByNickname(nickname);
        List<StoryListResponseDTO> stories = new ArrayList<>(); //원하는 부분만 가져오기위해 DTO 설정
        for (Story story : result) {
            StoryListResponseDTO storyListResponseDTO = new StoryListResponseDTO(); //각 자식 스토리의 새로운 DTO객체 생성
            storyListResponseDTO.setId(story.getId());
            storyListResponseDTO.setContent(story.getContent());
            storyListResponseDTO.setUserNickname(story.getUserNickname());
            storyListResponseDTO.setImageUrl(story.getImageUrl());
            stories.add(storyListResponseDTO); //모든 필요한 부분을 채운 객체를 추가한다.
        }
        Collections.reverse(stories);
        return stories;
    }
}

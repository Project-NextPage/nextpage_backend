package com.nextpage.backend.service;

import com.nextpage.backend.dto.response.ScenarioResponseDTO;
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

    public List<ScenarioResponseDTO> getStoriesByNickname(String nickname){
        List<Story> result;

        result = storyRepository.findStoriesByNickname(nickname); //시나리오 조회


        List<ScenarioResponseDTO> stories = new ArrayList<>(); //원하는 부분만 가져오기위해 DTO 설정
        for (Story story : result) {
            ScenarioResponseDTO scenarioResponseDTO = new ScenarioResponseDTO(); //각 자식 스토리의 새로운 DTO객체 생성
            scenarioResponseDTO.setId(story.getId());

            Long parentId = null; //parentid 가져오는 부분만 따로 지정
            Optional<Story> parentStoryOptional = storyRepository.findParentByChildId(story.getId());
            if (parentStoryOptional.isPresent()) {
                parentId = parentStoryOptional.get().getId();
            }
            scenarioResponseDTO.setParentId(parentId);

            scenarioResponseDTO.setImageUrl(story.getImageUrl());
            stories.add(scenarioResponseDTO); //모든 필요한 부분을 채운 객체를 추가한다.
        }
        Collections.reverse(stories);
        return stories;
    }
}

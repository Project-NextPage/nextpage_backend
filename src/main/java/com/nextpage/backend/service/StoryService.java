package com.nextpage.backend.service;


import com.nextpage.backend.config.jwt.TokenService;
import com.nextpage.backend.dto.request.StorySaveRequest;
import com.nextpage.backend.dto.response.RootResponseDTO;
import com.nextpage.backend.dto.response.ScenarioResponseDTO;
import com.nextpage.backend.dto.response.StoryDetailsResponseDTO;
import com.nextpage.backend.dto.response.StoryListResponseDTO;
import com.nextpage.backend.entity.Story;
import com.nextpage.backend.error.exception.story.StoryNotFoundException;
import com.nextpage.backend.error.exception.user.UserNotFoundException;
import com.nextpage.backend.repository.StoryRepository;
import com.nextpage.backend.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Service
public class StoryService {
    private final StoryRepository storyRepository;
    private final ImageService imageService;
    private final TokenService tokenService;
    private final UserRepository userRepository;

    // parentId가 없는 루트 스토리 목록 조회
    public List<RootResponseDTO> getRootStories() {
        List<Story> rootStories = storyRepository.findRootStories();
        List<RootResponseDTO> rootStoriesList = rootStories.stream()
                .map(story -> RootResponseDTO.of(story))
                .collect(Collectors.toList()); // 루트 스토리 목록 리스트 생성
        if (rootStoriesList.isEmpty()) { throw new StoryNotFoundException(); }
        return rootStoriesList;
    }

    public StoryDetailsResponseDTO getStoryDetails(Long storyId) { // 스토리 상세 조회
        Story story = storyRepository.findById(storyId)
                .orElseThrow(StoryNotFoundException::new);
        Long parentId = story.getParentId() != null ? story.getParentId().getId() : null;
        // 스토리 내용을 포함한 응답 객체 생성
        return StoryDetailsResponseDTO.of(story, parentId, getChildIds(story), getChildContents(story));
    }

    public List<Long> getChildIds(Story story) {
        return story.getChildId().stream().map(Story::getId).collect(Collectors.toList());
    }

    public List<String> getChildContents(Story story) {
        return story.getChildId().stream().map(Story::getContent).collect(Collectors.toList());
    }

    public void generateStory(StorySaveRequest request, Long parentId, HttpServletRequest httpServletRequest) {
        String userNickname = getUserNickname(httpServletRequest);
        String s3Url = imageService.uploadImageToS3(request.getImageUrl());
        Optional<Story> parentStory = getParentById(parentId);
        Story story = request.toEntity(userNickname, s3Url, parentStory.orElse(null));
        storyRepository.save(story);
    }

    private String getUserNickname(HttpServletRequest httpServletRequest) {
        // 토큰에서 userId 추출 후 닉네임 조회
        Long userId = tokenService.getUserIdFromToken(httpServletRequest);
        return userRepository.findNicknameById(userId)
                .orElseThrow(() -> new UserNotFoundException());
    }

    private Optional<Story> getParentById(Long parentId) {
        // 부모 ID가 주어진 경우, 부모 스토리 조회
        return Optional.ofNullable(parentId).flatMap(storyRepository::findById);
    }

    public List<ScenarioResponseDTO> getStoriesByRootId(Long rootId) { //시나리오 조회
        List<Story> result= storyRepository.findAllChildrenByRootId(rootId); //시나리오 조회
        List<ScenarioResponseDTO> stories = new ArrayList<>(); //원하는 부분만 가져오기위해 DTO 설정
        for (Story story : result) {
            Long parentId = getParentId(story);
            ScenarioResponseDTO scenarioResponseDTO = new ScenarioResponseDTO(
                    story.getId(),
                    parentId,
                    story.getImageUrl()
            ); //각 자식 스토리의 새로운 DTO객체 생성
            stories.add(scenarioResponseDTO); //모든 필요한 부분을 채운 객체를 추가한다.
        }
        Collections.reverse(stories);
        if (stories.isEmpty()) {
            throw new NoSuchElementException("스토리가 존재하지 않습니다.");
        }
        return stories;
    }

    public List<StoryListResponseDTO> getStoriesByleafId(Long leafId) { //특정 분기 조회
        List<Story> result= storyRepository.findRecursivelyByLeafId(leafId);
        List<StoryListResponseDTO> stories = new ArrayList<>(); //원하는 부분만 가져오기위해 DTO 설정
        for (Story story : result) {
            StoryListResponseDTO storyListResponseDTO = new StoryListResponseDTO(
                    story.getId(),
                    story.getContent(),
                    story.getUserNickname(),
                    story.getImageUrl()
            ); //각 자식 스토리의 새로운 DTO객체 생성
            stories.add(storyListResponseDTO); //모든 필요한 부분을 채운 객체를 추가한다.
        }
        Collections.reverse(stories);
        if (stories.isEmpty()) { throw new NoSuchElementException("스토리가 존재하지 않습니다."); }
        return stories;
    }

    public Long getParentId(Story story){ // 부모 ID 가져오는 함수 분리
        Long parentId = null; //parentid 가져오는 부분만 따로 지정
        Optional<Story> parentStoryOptional = storyRepository.findParentByChildId(story.getId());
        if (parentStoryOptional.isPresent()) {
            parentId = parentStoryOptional.get().getId();
        }
        return parentId;
    }

}
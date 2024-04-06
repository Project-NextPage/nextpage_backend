package com.nextpage.backend.service;


import com.nextpage.backend.config.jwt.TokenService;
import com.nextpage.backend.dto.request.StorySaveRequest;
import com.nextpage.backend.dto.response.ScenarioResponseDTO;
import com.nextpage.backend.dto.response.StoryDetailsResponseDTO;
import com.nextpage.backend.dto.response.StoryListResponseDTO;
import com.nextpage.backend.entity.Story;
import com.nextpage.backend.repository.StoryRepository;
import com.nextpage.backend.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class StoryService {
    private final StoryRepository storyRepository;
    private final OpenAiService openAiService;
    private final ImageService imageService;
    private final TokenService tokenService;
    private final UserRepository userRepository;

    public StoryService(StoryRepository storyRepository, OpenAiService openAiService, ImageService imageService, TokenService tokenService, UserRepository userRepository) {
        this.storyRepository = storyRepository;
        this.openAiService = openAiService;
        this.imageService = imageService;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
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

    // 스토리 생성 메서드
    public void generateStory(StorySaveRequest request, Long parentId, HttpServletRequest httpServletRequest) {
        String s3Url = imageService.uploadImageToS3(request.getImageUrl());
        if (s3Url != null) {
            // 요청에서 토큰 추출
            String token = tokenService.resolveToken(httpServletRequest);
            // 토큰에서 userId 추출
            Long userId = tokenService.getUserIdFromToken(token);
            // userId를 사용하여 닉네임 조회
            Optional<String> userNicknameOpt = userRepository.findNicknameById(userId);

            // Optional<String> 처리
            String userNickname = userNicknameOpt.orElseThrow(() -> new UsernameNotFoundException("User not found"));

            Story story = request.toEntity();
            story.setContent(request.getContent());
            story.setCreatedAt(LocalDateTime.now());
            story.setUpdatedAt(LocalDateTime.now());
            story.setIsDeleted(false);
            story.setUserNickname(userNickname);

            if (parentId != null) {
                // 부모 노드가 있는 경우
                Optional<Story> parentStoryOptional = storyRepository.findById(parentId);
                if (parentStoryOptional.isPresent()) {
                    Story parentStory = parentStoryOptional.get();
                    // 부모 노드와 연결
                    parentStory.getChildId().add(story);
                    story.setParentId(parentStory);
                    storyRepository.save(parentStory);
                } else throw new RuntimeException("Parent story not found");
            } else storyRepository.save(story);
        } else throw new RuntimeException("이미지 업로드에 실패했습니다.");
    }
  
    public List<ScenarioResponseDTO> getStoriesByRootId(Long rootId) { //시나리오 조회
        List<Story> result= storyRepository.findAllChildrenByRootId(rootId); //시나리오 조회
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

    public List<StoryListResponseDTO> getStoriesByleafId(Long leafId) { //특정 분기 조회
        List<Story> result= storyRepository.findRecursivelyByLeafId(leafId);
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

    public Mono<String> generatePicture(String content) {
        // 프롬프트 설정
        String promptKeyword = "Design: a detailed digital illustration drawn with bright colors and clean lines. Please make the following images according to the previous requirements: ";
        String conditions = "When generating an image, be sure to observe the following conditions: Do not add text to the image. I want an illustration image, not contain text in the image";

        // 프롬프트 구성
        String promptImage = conditions + "\n" + promptKeyword + content;

        // OpenAiService를 통해 이미지 생성 요청 후 URL 반환
        return openAiService.generateImage(promptImage)
                .onErrorResume(e -> {
                    // 에러 처리 로직. 예를 들어, 로깅하거나 기본 이미지 URL 반환
                    System.err.println("Error generating image: " + e.getMessage());
                    return Mono.just("Error or default image URL");
                });
    }
}
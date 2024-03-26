package com.nextpage.backend.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.nextpage.backend.dto.request.StorySaveRequest;
import com.nextpage.backend.dto.response.ScenarioResponseDTO;
import com.nextpage.backend.dto.response.StoryDetailsResponseDTO;
import com.nextpage.backend.entity.Story;
import com.nextpage.backend.repository.StoryRepository;
import com.theokanning.openai.image.CreateImageRequest;
import com.theokanning.openai.image.Image;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;
import java.io.ByteArrayInputStream;
import java.util.stream.Collectors;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StoryService {

    private final StoryRepository storyRepository;
    private final AmazonS3 amazonS3;
    private final WebClient webClient;
    private final OpenAiService openAiService;

    public StoryService(AmazonS3 amazonS3, StoryRepository storyRepository, WebClient.Builder webClientBuilder, OpenAiService openAiService) {
        this.amazonS3 = amazonS3;
        this.storyRepository = storyRepository;
        this.webClient = webClientBuilder.build();
        this.openAiService = openAiService;
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
    public void generateStory(StorySaveRequest request, Long parentId) {
        String s3Url = uploadImageToS3(request.getImageUrl());
        if (s3Url != null) {
            Story story = request.toEntity();
            story.setContent(request.getContent());
            story.setCreatedAt(LocalDateTime.now());
            story.setUpdatedAt(LocalDateTime.now());
            story.setIsDeleted(false);
            story.setUserNickname("test");

            if (parentId != null) {
                // 부모 노드가 있는 경우
                Optional<Story> parentStoryOptional = storyRepository.findById(parentId);
                if (parentStoryOptional.isPresent()) {
                    Story parentStory = parentStoryOptional.get();
                    // 부모 노드와 연결
                    parentStory.getChildId().add(story);
                    story.setParentId(parentStory);
                    storyRepository.save(parentStory);
                } else {
                    throw new RuntimeException("Parent story not found");
                }
            } else {
                // 부모 노드가 없는 경우
                storyRepository.save(story);
            }
        } else {
            throw new RuntimeException("이미지 업로드에 실패했습니다.");
        }
    }

    // 이미지 다운로드 후 S3에 업로드
    public String uploadImageToS3(String imageUrl) {
        try {
            byte[] imageBytes = downloadImage(imageUrl);
            if (imageBytes != null) {
                String fileName = System.currentTimeMillis() + ".webp";
                ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(imageBytes.length);
                PutObjectRequest putObjectRequest = new PutObjectRequest("bucketnextpage", fileName, inputStream, metadata);
                amazonS3.putObject(putObjectRequest);
                return amazonS3.getUrl("bucketnextpage", fileName).toString();
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 이미지 다운로드 및 변환
    private byte[] downloadImage(String imageUrl) {
        try {
            byte[] imageBytes = webClient.get()
                    .uri(imageUrl)
                    .accept(MediaType.APPLICATION_OCTET_STREAM)
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();

            return imageBytes;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<ScenarioResponseDTO> getStoriesByRootId(Long rootId) { //시나리오 조회
        List<Story> result = storyRepository.findAllChildrenByRootId(rootId); //자식 스토리들 전체 내용 일단 가져옴
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
        return stories;
    }

    public String generatePicture(String content) {
        try {
            // 이미지 생성 요청 객체 생성
            CreateImageRequest request = new CreateImageRequest();
            request.setPrompt(content); // 요청 객체에 내용 설정

            // OpenAI 서비스를 사용하여 이미지 생성 요청
            List<Image> images = openAiService.createImage(request).getData();
            if (images != null && !images.isEmpty()) {
                // 첫 번째 이미지의 URL 반환
                return images.get(0).getUrl();
            } else {
                throw new RuntimeException("이미지 생성에 실패했습니다.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null; // 이미지 생성에 실패하면 null 반환
        }
    }
}

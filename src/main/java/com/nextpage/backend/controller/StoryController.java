package com.nextpage.backend.controller;

import com.nextpage.backend.dto.response.ApiResponse;
import com.nextpage.backend.dto.response.RootResponseDTO;
import com.nextpage.backend.dto.response.ScenarioResponseDTO;
import com.nextpage.backend.dto.response.StoryDetailsResponseDTO;
import com.nextpage.backend.entity.Story;
import com.nextpage.backend.service.StoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v2/stories") // 공통 api
public class StoryController {

    private final StoryService storyService;

    public StoryController(StoryService storyService) {
        this.storyService = storyService;
    }

    @GetMapping // 루트 스토리 조회
    public ResponseEntity<?> getRootStories() {
        try {
            List<Story> rootStories = storyService.getRootStories();
            if (rootStories.isEmpty()) {
                return ResponseEntity.ok()
                        .body(new ApiResponse(404, "루트 스토리가 없습니다.", null));
            }
            List<RootResponseDTO.StoryInfo> storyInfos = rootStories.stream()
                    .map(story -> new RootResponseDTO.StoryInfo(
                            story.getId(),
                            story.getUserNickname(),
                            story.getContent(),
                            story.getImageUrl(),
                            story.getCreatedAt()
                    ))
                    .collect(Collectors.toList()); // 루트 스토리 목록 리스트 생성
            RootResponseDTO responseData = new RootResponseDTO(storyInfos);
            return ResponseEntity.ok()
                    .body(new ApiResponse(200, "루트 스토리 목록을 정상적으로 불러왔습니다.", responseData));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(500, "루트 스토리 조회 중 오류가 발생했습니다.", null));
        }
    }

    @GetMapping("/details/{storyId}") // 스토리 상세 조회
    public ResponseEntity<?> getStoryDetails(@PathVariable("storyId") Long storyId) {
        try {
            StoryDetailsResponseDTO storyDetails = storyService.getStoryDetails(storyId);
            return ResponseEntity.ok()
                    .body(new ApiResponse(200, "스토리를 정상적으로 조회 했습니다. [id: " + storyDetails.getId() + "]", storyDetails));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(404, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(500, "스토리 조회 중 오류가 발생했습니다.", null));
        }
    }

    @GetMapping("/{rootId}") //시나리오 조회
    public ResponseEntity<ApiResponse> getStoriesByRootId(@PathVariable Long rootId) {
        List<ScenarioResponseDTO> storiesByRoot = storyService.getStoriesByRootId(rootId);
        if (storiesByRoot.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(HttpStatus.NOT_FOUND.value(), "해당 ID를 가진 스토리가 존재하지 않습니다.", null));
        } else {
            return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "시나리오 조회 완료.", storiesByRoot));
        }
    }
}

package com.nextpage.backend.controller;

import com.nextpage.backend.dto.request.StorySaveRequest;
import com.nextpage.backend.dto.response.ApiResponse;
import com.nextpage.backend.dto.response.StoryDetailsResponseDTO;
import com.nextpage.backend.service.StoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v2/stories") // 공통 api
public class StoryController {

    private final StoryService storyService;

    public StoryController(StoryService storyService) {
        this.storyService = storyService;
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

    @PostMapping()
    public ResponseEntity<?> createStory(@RequestBody StorySaveRequest storyRequest, @RequestParam(required = false) Long parentId) {
        try {
            storyService.generateStory(storyRequest, parentId);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("스토리 생성에 실패했습니다.: " + e.getMessage());
        }
    }

}

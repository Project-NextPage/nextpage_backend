package com.nextpage.backend.controller;

import com.nextpage.backend.dto.response.ApiResponse;
import com.nextpage.backend.dto.response.StoryDetailsResponseDTO;
import com.nextpage.backend.service.StoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v2/stories") // 공통 api
public class StoryController {

    private final StoryService storyService;

    public StoryController(StoryService storyService) {
        this.storyService = storyService;
    }

//    @GetMapping("/stories")
//    public List<Story> getStoriesByNickname(@RequestParam String userNickname) {
//        return storyService.getStoriesByUserNickname(userNickname);
//    }

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
}

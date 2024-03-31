package com.nextpage.backend.controller;

import com.nextpage.backend.dto.request.StorySaveRequest;
import com.nextpage.backend.dto.response.ApiResponse;
import com.nextpage.backend.dto.response.RootResponseDTO;
import com.nextpage.backend.dto.response.ScenarioResponseDTO;
import com.nextpage.backend.dto.response.StoryDetailsResponseDTO;
import com.nextpage.backend.entity.Story;
import com.nextpage.backend.service.StoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Tag(name = "Users", description = "User 관리")
@RestController
@RequestMapping("/api/v2/stories") // 공통 api
public class StoryController {

    private final StoryService storyService;

    public StoryController(StoryService storyService) {
        this.storyService = storyService;
    }

    @Operation(summary = "루트 스토리 조회", description = "루트 스토리의 목록을 조회합니다.")
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

    @Operation(summary = "스토리 상세 조회", description = "단일 스토리의 상세 내용을 조회합니다.")
    @Parameter(name = "storyId", description = "조회할 스토리 아이디")
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

    @Operation(summary = "스토리 생성", description = "새로운 스토리를 생성합니다.")
    @PostMapping()
    public ResponseEntity<ApiResponse> createStory(@RequestBody StorySaveRequest storyRequest, @RequestParam(required = false) Long parentId) {
        try {
            storyService.generateStory(storyRequest, parentId);
            // 스토리 생성 성공 응답
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(201, "스토리 생성을 완료했습니다.", null));
        } catch (Exception e) {
            // 스토리 생성 실패 응답
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(new ApiResponse(422, "스토리 생성에 실패했습니다.: " + e.getMessage(), null));
        }
    }


    @Operation(summary = "시나리오 조회", description = "시나리오의 스토리 목록을 조회합니다.")
    @Parameter(name = "rootId", description = "조회할 시나리오의 루트 스토리 아이디")
    @GetMapping("/{rootId}") // 시나리오 조회
    public ResponseEntity<ApiResponse> getStoriesByRootId(@PathVariable Long rootId) {
        List<ScenarioResponseDTO> storiesByRoot = storyService.getStoriesById(rootId, true);
        if (storiesByRoot.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(HttpStatus.NOT_FOUND.value(), "해당 ID를 가진 스토리가 존재하지 않습니다.", null));
        } else {
            return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "시나리오 조회 완료.", storiesByRoot));
        }
    }

    @Operation(summary = "특정 분기 조회", description = "특정 분기의 스토리들을 조회합니다.")
    @Parameter(name = "storyId", description = "조회할 분기의 리프 스토리 아이디")
    @GetMapping("/branch/{storyId}") // 특정 분기 조회
    public ResponseEntity<ApiResponse> getStoriesByleafId(@PathVariable Long storyId) {
        List<ScenarioResponseDTO> storiesByLeaf = storyService.getStoriesById(storyId ,false);
        if (storiesByLeaf.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(HttpStatus.NOT_FOUND.value(), "해당 ID를 가진 스토리가 존재하지 않습니다.", null));
        } else {
            return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "시나리오 조회 완료.", storiesByLeaf));
        }
    }

    @Operation(summary = "이미지 생성", description = "스토리의 관련된 이미지를 생성합니다.")
    @PostMapping("/images")
    public Mono<ResponseEntity<ApiResponse>> generateImage(@RequestParam String content) {
        return storyService.generatePicture(content)
                .map(imageUrl -> ResponseEntity.ok().body(new ApiResponse(200, "이미지 생성에 성공했습니다.", imageUrl)))
                .onErrorResume(e -> Mono.just(
                        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(new ApiResponse(500, "이미지 생성 중 오류가 발생했습니다.", null))
                ));
    }
}

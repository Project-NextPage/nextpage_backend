package com.nextpage.backend.controller;

import com.nextpage.backend.dto.request.StorySaveRequest;
import com.nextpage.backend.dto.response.*;
import com.nextpage.backend.result.ResultResponse;
import com.nextpage.backend.service.OpenAiService;
import com.nextpage.backend.service.StoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.nextpage.backend.result.ResultCode.*;


@Tag(name = "Stories", description = "Story 관리")
@RestController
@RequestMapping("/api/v2/stories") // 공통 api
public class StoryController {

    private final StoryService storyService;
    private final OpenAiService openAiService;

    public StoryController(StoryService storyService, OpenAiService openAiService) {
        this.storyService = storyService;
        this.openAiService = openAiService;
    }

    @Operation(summary = "루트 스토리 조회", description = "루트 스토리의 목록을 조회합니다.")
    @GetMapping // 루트 스토리 조회
    public ResponseEntity<ResultResponse> getRootStories() {
        List<RootResponseDTO> rootStoriesList = storyService.getRootStories();
        return ResponseEntity.ok(ResultResponse.of(STORY_LIST_SUCCESS, rootStoriesList));
    }

    @Operation(summary = "스토리 상세 조회", description = "단일 스토리의 상세 내용을 조회합니다.")
    @Parameter(name = "storyId", description = "조회할 스토리 아이디")
    @GetMapping("/details/{storyId}") // 스토리 상세 조회
    public ResponseEntity<ResultResponse> getStoryDetails(@PathVariable Long storyId) {
        StoryDetailsResponseDTO storyDetails = storyService.getStoryDetails(storyId);
        return ResponseEntity.ok(ResultResponse.of(STORY_DETAIL_INFO_SUCCESS, storyDetails));
    }

    @Operation(summary = "스토리 생성", description = "새로운 스토리를 생성합니다.")
    @PostMapping()
    public ResponseEntity<ApiResponse> createStory(@RequestBody @Valid StorySaveRequest storyRequest,
                                                   @RequestParam(required = false) Long parentId,
                                                   HttpServletRequest request) {
        storyService.generateStory(storyRequest, parentId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(201, "스토리 생성을 완료했습니다.", null));
    }

    @Operation(summary = "시나리오 조회", description = "시나리오의 스토리 목록을 조회합니다.")
    @Parameter(name = "rootId", description = "조회할 시나리오의 루트 스토리 아이디")
    @GetMapping("/{rootId}") // 시나리오 조회
    public ResponseEntity<ApiResponse> getStoriesByRootId(@PathVariable Long rootId) {
        List<ScenarioResponseDTO> storiesByRoot = storyService.getStoriesByRootId(rootId);
        return ResponseEntity.ok()
                .body(new ApiResponse(HttpStatus.OK.value(), "시나리오 조회 완료.", storiesByRoot));
    }

    @Operation(summary = "특정 분기 조회", description = "특정 분기의 스토리들을 조회합니다.")
    @Parameter(name = "storyId", description = "조회할 분기의 리프 스토리 아이디")
    @GetMapping("/branch/{storyId}") // 특정 분기 조회
    public ResponseEntity<ApiResponse> getStoriesByleafId(@PathVariable Long storyId) {
        List<StoryListResponseDTO> storiesByLeaf = storyService.getStoriesByleafId(storyId);
        return ResponseEntity.ok()
                .body(new ApiResponse(HttpStatus.OK.value(), "시나리오 조회 완료.", storiesByLeaf));
    }

    @Operation(summary = "이미지 생성", description = "스토리의 관련된 이미지를 생성합니다.")
    @PostMapping("/images")
    public ResponseEntity<ApiResponse> generateImage(@RequestParam String content) {
        String imageUrl = openAiService.generateImage(content);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(200, "이미지 생성에 성공했습니다.", imageUrl));
    }
}

package com.nextpage.backend.controller;

import com.nextpage.backend.dto.response.ApiResponse;
import com.nextpage.backend.dto.response.StoryListResponseDTO;
import com.nextpage.backend.result.ResultResponse;
import com.nextpage.backend.service.MypageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.nextpage.backend.result.ResultCode.MYPAGE_MYSTORY_LIST_SUCCESS;
import static com.nextpage.backend.result.ResultCode.STORY_LIST_SUCCESS;

@Tag(name = "Mypage", description = "Mypage 관리")
@RestController
@RequestMapping("/api/v2/mypage") // 공통 api
public class MypageController {

    private final MypageService mypageService;

    public MypageController(MypageService mypageService) {
        this.mypageService = mypageService;
    }

    @Operation(summary = "내가 쓴 스토리 조회", description = "특정 닉네임의 스토리를 조회합니다.")
    @Parameter(name = "nickname", description = "조회할 스토리들의 작성자 닉네임")
    @GetMapping("/mystories/{nickname}") // 특정 분기 조회
    public ResponseEntity<ResultResponse> getStoriesByNickname(@PathVariable String nickname) {
        List<StoryListResponseDTO> storiesByNickname = mypageService.getStoriesByNickname(nickname);
        return ResponseEntity.ok(ResultResponse.of(MYPAGE_MYSTORY_LIST_SUCCESS, storiesByNickname));
    }
}

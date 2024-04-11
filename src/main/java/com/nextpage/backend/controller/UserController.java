package com.nextpage.backend.controller;

import com.nextpage.backend.config.jwt.TokenService;
import com.nextpage.backend.dto.request.UserCreateRequest;
import com.nextpage.backend.dto.response.ApiResponse;
import com.nextpage.backend.dto.response.UserResponseDTO;
import com.nextpage.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Users", description = "User 관리")
@RestController
@RequestMapping("/api/v2/users") // 공통 api
public class UserController {
    private final UserService userService;
    private final TokenService tokenService;

    public UserController(UserService userService, TokenService tokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
    }

    @Operation(summary = "회원가입 및 로그인", description = "회원가입 및 로그인을 진행합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse> createUser(@RequestBody UserCreateRequest request) {
        UserResponseDTO user = userService.createUser(request);
        return ResponseEntity.ok()
                .body(new ApiResponse(HttpStatus.OK.value(), "유저 정보를 불러왔습니다.", user));
    }

    @Operation(summary = "회원가입 및 로그인", description = "회원가입 및 로그인을 진행합니다.")
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok()
                .body(new ApiResponse(HttpStatus.OK.value(), "유저 정보를 삭제했습니다.", null));
    }

    @Operation(summary = "토큰으로 사용자 조회 API", description = "토큰을 통해 사용자 정보를 조회합니다.")
    @GetMapping("/details") // 토큰으로 사용자 조회
    public ResponseEntity<ApiResponse> getUserByToken(HttpServletRequest request) {
        String token = tokenService.resolveToken(request);
        Long userId = tokenService.getUserIdFromToken(token);
        UserResponseDTO userResponseDTO = userService.getUserInfo(userId);
        return ResponseEntity.ok()
                .body(new ApiResponse(HttpStatus.OK.value(), "조회 완료." , userResponseDTO));


    }
}

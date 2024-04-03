package com.nextpage.backend.controller;

import com.nextpage.backend.dto.request.UserCreateRequest;
import com.nextpage.backend.dto.response.ApiResponse;
import com.nextpage.backend.dto.response.UserResponseDTO;
import com.nextpage.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Users", description = "User 관리")
@RestController
@RequestMapping("/api/v2/users") // 공통 api
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "회원가입 및 로그인", description = "회원가입 및 로그인을 진행합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse> createUser(@RequestBody UserCreateRequest request) {
        UserResponseDTO user = userService.createUser(request);
        return ResponseEntity.ok()
                .body(new ApiResponse(HttpStatus.OK.value(), "유저 정보를 불러왔습니다.", user));
    }
}

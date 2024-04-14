package com.nextpage.backend.service;

import com.nextpage.backend.config.jwt.TokenService;
import com.nextpage.backend.dto.request.UserCreateRequest;
import com.nextpage.backend.dto.response.UserResponseDTO;
import com.nextpage.backend.entity.User;
import com.nextpage.backend.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final TokenService tokenService;

    public UserService(UserRepository userRepository, TokenService tokenService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    public UserResponseDTO createUser(UserCreateRequest request) {
        String email = request.getEmail();
        String nickname = request.getNickname();
        if (userRepository.existsByEmail(email)) { // 이미 존재하는 이메일이면 유저 생성 x
            throw new NoSuchElementException("이미 존재하는 이메일입니다.");
        }
        User newUser = new User(); // 유저 생성
        newUser.setEmail(email);
        newUser.setNickname(nickname);
        newUser.setCreatedAt(LocalDateTime.now());
        newUser = userRepository.save(newUser); // db에 유저 저장 - 회원 가입
        return updateUser(newUser.getId(), newUser.getNickname());
    }

    public UserResponseDTO updateUser(Long id, String nickname) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다."));
        user.update(nickname + "#" + user.getId());
        userRepository.save(user);
        return new UserResponseDTO(user);
    }

    public void deleteUser(HttpServletRequest request) {
        Long userId = tokenService.getUserIdFromToken(request);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다."));
        userRepository.delete(user);
    }

    public UserResponseDTO getUserInfo(HttpServletRequest request){
        tokenService.validateAccessToken(request); // 만료 검사
        Long userId = tokenService.getUserIdFromToken(request);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 유저입니다."));
        return new UserResponseDTO(user);
    }
}

package com.nextpage.backend.service;

import com.nextpage.backend.dto.request.UserCreateRequest;
import com.nextpage.backend.dto.response.UserResponseDTO;
import com.nextpage.backend.entity.User;
import com.nextpage.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponseDTO createUser(UserCreateRequest request) {
        String email = request.getEmail();
        String nickname = request.getNickname();
        if (userRepository.existsByEmail(email)) { // 이미 존재하는 이메일이면 유저 생성 x
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        } else if (userRepository.existsByNickname(nickname)) {
            throw new RuntimeException("중복된 닉네임입니다.");
        }
        User newUser = new User(); // 유저 생성
        newUser.setEmail(email);
        newUser.setNickname(nickname);
        newUser.setCreatedAt(LocalDateTime.now());
        newUser = userRepository.save(newUser); // db에 유저 저장 - 회원 가입
        return new UserResponseDTO(newUser);
    }
}

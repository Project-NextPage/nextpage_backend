package com.nextpage.backend.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "\"users\"")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"id\"", columnDefinition = "INT")
    private Long id;

    @Column(name = "\"email\"", unique = true, nullable = false)
    private String email;

    @Column(name = "\"nickname\"", nullable = false)
    private String nickname;

    @Column(name = "\"createdAt\"", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "\"updatedAt\"")
    private LocalDateTime updatedAt;

    @Column(name = "\"isDeleted\"", nullable = false)
    private boolean isDeleted;

    @Builder // 빌더 패턴 구현
    public User(String email, String nickname) {
        this.email = email;
        this.nickname = nickname;
        this.createdAt = LocalDateTime.now(); // 현재 시간
    }

    public User updateGoogle(String nickname) { // 구글 소셜 로그인 시에 사용
        this.nickname = nickname;

        return this;
    }

    public User update(String nickname) { // 프로필 수정 시 사용
        this.nickname = nickname;
        this.updatedAt = LocalDateTime.now();

        return this;
    }
}


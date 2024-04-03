package com.nextpage.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Setter
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

    public User update(String nickname) { // 프로필 수정 시 사용
        this.nickname = nickname;
        this.updatedAt = LocalDateTime.now();

        return this;
    }
}


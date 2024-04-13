package com.nextpage.backend.dto.response;

import com.nextpage.backend.entity.User;
import lombok.Getter;

@Getter
public class UserResponseDTO {
    private Long id;
    private String email;
    private String nickname;

    public UserResponseDTO(User user) {
        this.id=user.getId();
        this.email=user.getEmail();
        this.nickname=user.getNickname();
    }
}

package com.onhz.server.dto.response;

import com.onhz.server.common.enums.Role;
import com.onhz.server.entity.user.UserEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String userName;
    private String profilePath;
    private Role role;

    public static UserResponse from (UserEntity user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .userName(user.getUserName())
                .profilePath(user.getProfilePath())
                .role(user.getRole())
                .build();
    }
}

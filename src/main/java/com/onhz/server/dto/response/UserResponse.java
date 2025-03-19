package com.onhz.server.dto.response;

import com.onhz.server.common.enums.Role;
import com.onhz.server.entity.user.UserEntity;
import com.onhz.server.exception.NotFoundException;
import com.onhz.server.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Builder
public class UserResponse {
    private final Long id;
    private final String email;
    private final String userName;
    private final String profilePath;
    private final boolean isSocial;
    private final String socialType;
    private final Role role;

    public static UserResponse from (UserEntity user) {
        if (user == null) {
            throw new NotFoundException(ErrorCode.NOT_FOUND_EXCEPTION, "유저를 찾을 수 없습니다.");
        }
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .userName(user.getUserName())
                .profilePath(user.getProfilePath())
                .isSocial(user.isSocial())
                .socialType(user.getSocial() != null ? user.getSocial().getCode() : null)
                .role(user.getRole())
                .build();
    }
}

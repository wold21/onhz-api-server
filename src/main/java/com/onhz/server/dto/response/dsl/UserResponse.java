package com.onhz.server.dto.response.dsl;

import com.onhz.server.common.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private  Long id;
    private  String email;
    private  String userName;
    private  String profilePath;
    private  Role role;
}

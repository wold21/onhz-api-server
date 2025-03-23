package com.onhz.server.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserRequest {
    @JsonProperty("new_password")
    private String newPassword;
    @NotBlank(message = "닉네임을 입력해주세요.")
    @JsonProperty("user_name")
    private String userName;
}

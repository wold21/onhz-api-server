package com.onhz.server.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SignUpRequestDto {

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "이메일 형식을 지켜주세요.")
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;

    @NotBlank(message = "유저명을 입력해주세요.")
    private String userName;
}

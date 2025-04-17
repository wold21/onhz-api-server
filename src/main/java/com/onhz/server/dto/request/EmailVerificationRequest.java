package com.onhz.server.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class EmailVerificationRequest {
    @NotBlank(message = "이메일을 입력해주세요.")
    @Schema(description = "사용자 이메일 주소", example = "user@example.com")
    private String email;

    @Schema(description = "인증코드", example = "asd15TQS")
    private String code;
}

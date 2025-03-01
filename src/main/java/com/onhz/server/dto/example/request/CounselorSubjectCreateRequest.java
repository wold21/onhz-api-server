package com.onhz.server.dto.example.request;

import jakarta.validation.constraints.NotNull;

public record CounselorSubjectCreateRequest(
        @NotNull
//        @Schema(description = "상담 분야 아이디", example = "1")
        Long id,
        @NotNull
//        @Schema(description = "상담 분야 가격", example = "10000")
        Long price
) {
}

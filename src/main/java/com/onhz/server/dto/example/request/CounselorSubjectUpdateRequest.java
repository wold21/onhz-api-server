package com.onhz.server.dto.example.request;

public record CounselorSubjectUpdateRequest(
//        @Schema(description = "상담 분야 아이디", example = "1")
        Long id,
//        @Schema(description = "상담 분야 가격", example = "10000")
        Long price
) {
}

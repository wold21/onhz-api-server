package com.onhz.server.dto.example.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CounselorCreateRequest(
        @NotNull
//        @Schema(description = "상담가 이름", example = "상담가")
        String name,
//        @Schema(description = "상담가 설명", example = "상담가입니다.")
        String introduction,
        @NotNull
        @JsonProperty("is_use")
//        @Schema(description = "사용 여부", example = "true")
        boolean isUse,
        @NotNull @Valid
        @JsonProperty("counselor_subjects")
        List<CounselorSubjectCreateRequest> counselorSubjects
) {
}


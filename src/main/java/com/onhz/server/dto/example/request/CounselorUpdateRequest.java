package com.onhz.server.dto.example.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record CounselorUpdateRequest(
//        @Schema(description = "상담가 이름", example = "상담가")
        String name,
//        @Schema(description = "상담가 설명", example = "상담가입니다.")
        String introduction,
        @JsonProperty("is_user")
//        @Schema(description = "사용 여부", example = "true")
        Boolean isUse,
        @JsonProperty("delete_subjects")
        List<CounselorSubjectUpdateRequest> deleteSubjects,
        @JsonProperty("update_subjects")
        List<CounselorSubjectUpdateRequest> updateSubjects,
        @JsonProperty("new_subjects")
        List<CounselorSubjectUpdateRequest> newSubjects

) {
}


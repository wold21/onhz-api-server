package com.onhz.server.dto.example.response;

import java.time.LocalDateTime;

public record CounselorListResponse(
        Long id,
        String name,
        String introduction,
        LocalDateTime createAt,
        boolean isUse
) {
}


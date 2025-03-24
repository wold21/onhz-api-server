package com.onhz.server.dto.response.review;

import com.onhz.server.common.enums.ReviewType;
import com.onhz.server.dto.response.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class ReviewLatestResponse {
    private final Long id;
    private final UserResponse user;
    private final String content;
    private final ReviewType reviewType;
    private final Long entityId;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final double rating;
    private final String entityName;
    private final String entityFilePath;
    private final int likeCount;
}

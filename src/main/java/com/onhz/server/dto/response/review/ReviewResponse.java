package com.onhz.server.dto.response.review;

import com.onhz.server.common.enums.ReviewType;
import com.onhz.server.dto.response.UserResponse;
import com.onhz.server.entity.review.ReviewEntity;
import com.onhz.server.exception.NotFoundException;
import com.onhz.server.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Builder
public class ReviewResponse {
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
    private final Boolean isLiked;

    public static ReviewResponse from(ReviewEntity review, String entityName, String entityFilePath, int likeCount, Boolean isLiked) {
        if (review == null) {
            throw new NotFoundException(ErrorCode.NOT_FOUND_EXCEPTION, "리뷰를 찾을 수 없습니다.");
        }
        return ReviewResponse.builder()
                .id(review.getId())
                .user(UserResponse.from(review.getUser()))
                .content(review.getContent())
                .reviewType(review.getReviewType())
                .entityId(review.getEntityId())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .rating(review.getRating())
                .entityName(entityName)
                .entityFilePath(entityFilePath)
                .likeCount(likeCount)
                .isLiked(isLiked)
                .build();
    }
}

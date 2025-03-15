package com.onhz.server.dto.response;

import com.onhz.server.common.enums.Review;
import com.onhz.server.entity.review.ReviewEntity;
import com.onhz.server.exception.NotFoundException;
import com.onhz.server.exception.example.ErrorCode;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReviewResponse {
    private final Long id;
    private final UserResponse user;
    private final String content;
    private final Review review;
    private final Long entityId;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final double rating;
    private final int likeCount;
    private final Boolean isLiked;

    public static ReviewResponse from(ReviewEntity review, int likeCount, Boolean isLiked) {
        if (review == null) {
            throw new NotFoundException(ErrorCode.NOT_FOUND_EXCEPTION, "리뷰를 찾을 수 없습니다.");
        }
        return ReviewResponse.builder()
                .id(review.getId())
                .user(UserResponse.from(review.getUser()))
                .content(review.getContent())
                .review(review.getReview())
                .entityId(review.getEntityId())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .rating(review.getRating())
                .likeCount(likeCount)
                .isLiked(isLiked)
                .build();
    }
}

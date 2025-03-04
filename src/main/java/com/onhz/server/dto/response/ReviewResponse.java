package com.onhz.server.dto.response;

import com.onhz.server.common.enums.Review;
import com.onhz.server.entity.review.ReviewEntity;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReviewResponse {
    private Long id;
    private UserResponse user;
    private String content;
    private Review review;
    private Long entityId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private double rating;

    public static ReviewResponse from(ReviewEntity review){
        return ReviewResponse.builder()
                .id(review.getId())
                .user(UserResponse.from(review.getUser()))
                .content(review.getContent())
                .review(review.getReview())
                .entityId(review.getEntityId())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .rating(review.getRating())
                .build();
    }
}

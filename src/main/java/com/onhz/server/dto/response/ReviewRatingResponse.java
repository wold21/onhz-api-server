package com.onhz.server.dto.response;

import com.onhz.server.entity.RatingSummaryEntity;
import com.onhz.server.entity.review.ReviewEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;

@Getter
public class ReviewRatingResponse extends SummaryResponse {
    private final Long userReviewId;
    private final Double userRating;

    public ReviewRatingResponse(Long id, Double averageRating, int ratingCount, Object ratingDist, LocalDateTime lastUpdatedAt, Long userReviewId, Double userRating) {
        super(id, averageRating, ratingCount, ratingDist, lastUpdatedAt);
        this.userReviewId = userReviewId;
        this.userRating = userRating;
    }

    public static ReviewRatingResponse from(RatingSummaryEntity ratingSummaryEntity, Long entityId, Optional<ReviewEntity> reviewEntity) {
        Long userReviewId = reviewEntity.map(ReviewEntity::getId).orElse(null);
        Double userRating = reviewEntity.map(ReviewEntity::getRating).orElse(-1.0);
        if (ratingSummaryEntity == null) {
            return new ReviewRatingResponse(entityId, 0.0, 0, null, null, userReviewId, userRating);
        }
        return new ReviewRatingResponse(
                ratingSummaryEntity.getId(),
                ratingSummaryEntity.getAverageRating(),
                ratingSummaryEntity.getRatingCount(),
                (ratingSummaryEntity.getRatingDist()),
                ratingSummaryEntity.getLastUpdated(),
                userReviewId,
                userRating
        );
    }
}

package com.onhz.server.dto.response;

import com.onhz.server.entity.RatingSummaryEntity;
import com.onhz.server.entity.review.ReviewEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class RatingResponse {
    private final Long id;
    private final Double averageRating;
    private final int ratingCount;
    private final Object ratingDist;
    private final LocalDateTime lastUpdatedAt;
    private final Long userReviewId;
    private final Double userRating;

    public static RatingResponse from(RatingSummaryEntity ratingSummaryEntity, Long entityId, Optional<ReviewEntity> reviewEntity) {
        Long userReviewId = reviewEntity.map(ReviewEntity::getId).orElse(null);
        Double userRating = reviewEntity.map(ReviewEntity::getRating).orElse(-1.0);
        if (ratingSummaryEntity == null) {
            return new RatingResponse(entityId, 0.0, 0, null, null, userReviewId, userRating);
        }
        return new RatingResponse(
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

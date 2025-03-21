package com.onhz.server.dto.response;

import com.onhz.server.entity.RatingSummaryEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class RatingResponse {
    private final Long id;
    private final double averageRating;
    private final int ratingCount;
    private final Object ratingDist;
    private final LocalDateTime lastUpdatedAt;
    private final double userRating;

    public static RatingResponse from(RatingSummaryEntity ratingSummaryEntity, Long entityId, double userRating) {
        if (ratingSummaryEntity == null) {
            return new RatingResponse(entityId, 0.0, 0, null, null, -1.0);
        }
        return new RatingResponse(
                ratingSummaryEntity.getId(),
                ratingSummaryEntity.getAverageRating(),
                ratingSummaryEntity.getRatingCount(),
                (ratingSummaryEntity.getRatingDist()),
                ratingSummaryEntity.getLastUpdated(),
                userRating
        );
    }

    public static RatingResponse of(RatingSummaryEntity ratingSummaryEntity, double userRating) {
        return new RatingResponse(
                ratingSummaryEntity.getId(),
                ratingSummaryEntity.getAverageRating(),
                ratingSummaryEntity.getRatingCount(),
                (ratingSummaryEntity.getRatingDist()),
                ratingSummaryEntity.getLastUpdated(),
                userRating
        );
    }
}

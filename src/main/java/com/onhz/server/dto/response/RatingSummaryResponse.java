package com.onhz.server.dto.response;

import com.onhz.server.entity.RatingSummaryEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class RatingSummaryResponse {
    private final Long id;
    private final double averageRating;
    private final int ratingCount;
    private final Object ratingDist;
    private final LocalDateTime lastUpdatedAt;

    public static RatingSummaryResponse from(RatingSummaryEntity ratingSummaryEntity, Long entityId) {
        if (ratingSummaryEntity == null) {
            return new RatingSummaryResponse(entityId, 0.0, 0, null, null);
        }
        return new RatingSummaryResponse(
                ratingSummaryEntity.getId(),
                ratingSummaryEntity.getAverageRating(),
                ratingSummaryEntity.getRatingCount(),
                (ratingSummaryEntity.getRatingDist()),
                ratingSummaryEntity.getLastUpdated()
        );
    }

    public static RatingSummaryResponse of(RatingSummaryEntity ratingSummaryEntity) {
        return new RatingSummaryResponse(
                ratingSummaryEntity.getId(),
                ratingSummaryEntity.getAverageRating(),
                ratingSummaryEntity.getRatingCount(),
                (ratingSummaryEntity.getRatingDist()),
                ratingSummaryEntity.getLastUpdated()
        );
    }
}

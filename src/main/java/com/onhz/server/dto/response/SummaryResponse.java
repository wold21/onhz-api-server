package com.onhz.server.dto.response;

import com.onhz.server.entity.RatingSummaryEntity;
import com.onhz.server.entity.user.UserRatingSummaryEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
public class SummaryResponse {
    private final Long id;
    private final Double averageRating;
    private final int ratingCount;
    private final Object ratingDist;
    private final LocalDateTime lastUpdatedAt;

    public static SummaryResponse from(UserRatingSummaryEntity userRatingSummaryEntity) {
        if (userRatingSummaryEntity == null) {
            return new SummaryResponse(0L, 0.0, 0, null, null);
        }
        return SummaryResponse.builder()
                .id(userRatingSummaryEntity.getId())
                .averageRating(userRatingSummaryEntity.getAverageRating())
                .ratingCount(userRatingSummaryEntity.getRatingCount())
                .ratingDist(userRatingSummaryEntity.getRatingDist())
                .lastUpdatedAt(userRatingSummaryEntity.getLastUpdated())
                .build();
    }
}

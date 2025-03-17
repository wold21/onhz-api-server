package com.onhz.server.entity.track;

import com.onhz.server.entity.RatingSummaryEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "track_rating_summary")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrackRatingSummaryEntity implements RatingSummaryEntity {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "track_id")
    private TrackEntity track;

    @Column(name = "average_rating")
    private Double averageRating;

    @Column(name = "rating_count")
    private Integer ratingCount;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "rating_dist", columnDefinition = "jsonb")
    private Object ratingDist;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    public static TrackRatingSummaryEntity create(TrackEntity track) {
        TrackRatingSummaryEntity entity = new TrackRatingSummaryEntity();
        entity.setTrack(track);
        return entity;
    }

    public static TrackRatingSummaryEntity create(TrackEntity track,
                                                  Double avgRating,
                                                  Integer ratingCount,
                                                  Object ratingDist) {
        TrackRatingSummaryEntity entity = create(track);
        entity.setAverageRating(avgRating);
        entity.setRatingCount(ratingCount);
        entity.setRatingDist(ratingDist);
        return entity;
    }

    public void updateStats(Double avgRating, Integer ratingCount, Object ratingDist) {
        this.setAverageRating(avgRating);
        this.setRatingCount(ratingCount);
        this.setRatingDist(ratingDist);
    }
}

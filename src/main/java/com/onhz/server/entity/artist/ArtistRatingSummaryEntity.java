package com.onhz.server.entity.artist;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "artist_rating_summary")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArtistRatingSummaryEntity {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "artist_id")
    private ArtistEntity artist;

    @Column(name = "average_rating")
    private Double averageRating;

    @Column(name = "rating_count")
    private Integer ratingCount;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "rating_dist", columnDefinition = "jsonb")
    private Object ratingDist;

    @UpdateTimestamp
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    public static ArtistRatingSummaryEntity create(ArtistEntity artist) {
        ArtistRatingSummaryEntity entity = new ArtistRatingSummaryEntity();
        entity.setArtist(artist);
        return entity;
    }

    public static ArtistRatingSummaryEntity create(ArtistEntity artist,
                                                 Double avgRating,
                                                 Integer ratingCount,
                                                 Object ratingDist) {
        ArtistRatingSummaryEntity entity = create(artist);
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

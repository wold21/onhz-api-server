package com.onhz.server.entity.album;

import com.onhz.server.entity.RatingSummaryEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashMap;

@Entity
@Table(name = "album_rating_summary")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AlbumRatingSummaryEntity implements RatingSummaryEntity {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "album_id")
    private AlbumEntity album;

    @Column(name = "average_rating")
    private Double averageRating;

    @Column(name = "rating_count")
    private Integer ratingCount;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "rating_dist", columnDefinition = "jsonb")
    private Object ratingDist;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    public static AlbumRatingSummaryEntity create(AlbumEntity album) {
        AlbumRatingSummaryEntity entity = new AlbumRatingSummaryEntity();
        entity.setAlbum(album);
        return entity;
    }

    public static AlbumRatingSummaryEntity create(AlbumEntity album,
                                                   Double avgRating,
                                                   Integer ratingCount,
                                                   Object ratingDist) {
        AlbumRatingSummaryEntity entity = create(album);
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


    public static AlbumRatingSummaryEntity createEmpty(Long albumId) {
        return AlbumRatingSummaryEntity.builder()
                .id(albumId)
                .averageRating(0.0)
                .ratingCount(0)
                .ratingDist(new HashMap<>())
                .lastUpdated(LocalDateTime.now())
                .build();
    }
}

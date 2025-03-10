package com.onhz.server.entity.track;

import com.onhz.server.entity.artist.ArtistEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "track_rating_summary")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TrackRatingSummaryEntity {

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



}

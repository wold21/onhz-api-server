package com.onhz.server.entity.artist;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "artist_rating_summary")
@Getter
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

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;



}

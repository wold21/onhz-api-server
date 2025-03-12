package com.onhz.server.entity.user;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_rating_summary")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserRatingSummaryEntity {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private UserEntity user;

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

    public static UserRatingSummaryEntity create(UserEntity user) {
        UserRatingSummaryEntity entity = new UserRatingSummaryEntity();
        entity.setUser(user);
        return entity;
    }

    public static UserRatingSummaryEntity create(UserEntity user,
                                                 Double avgRating,
                                                 Integer ratingCount,
                                                 Object ratingDist) {
        UserRatingSummaryEntity entity = create(user);
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

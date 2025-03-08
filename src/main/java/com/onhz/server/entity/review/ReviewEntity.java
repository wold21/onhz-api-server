package com.onhz.server.entity.review;

import com.onhz.server.common.enums.Review;
import com.onhz.server.common.enums.Role;
import com.onhz.server.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Optional;

@Entity
@Table(name = "review_tb")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ReviewEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "reviewType", nullable = false)
    private Review review;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private double rating;

    public void updateContent(String content) {
        this.content = content;
    }
    public void updateRating(double rating) {
        this.rating = rating;
    }
    public void updateUpdateAt() {
        this.updatedAt = LocalDateTime.now();
    }

}

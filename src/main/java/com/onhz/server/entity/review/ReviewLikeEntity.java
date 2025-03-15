package com.onhz.server.entity.review;

import com.onhz.server.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "like_tb")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ReviewLikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private ReviewEntity review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    public static ReviewLikeEntity create(ReviewEntity review, UserEntity user) {
        ReviewLikeEntity like = ReviewLikeEntity.builder()
                .review(review)
                .user(user)
                .build();
        review.addLike(like);
        return like;
    }
}
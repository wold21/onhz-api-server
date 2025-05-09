package com.onhz.server.entity.review;

import com.onhz.server.common.enums.ReviewType;
import com.onhz.server.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private ReviewType reviewType;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private Double rating;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewLikeEntity> likes = new ArrayList<>();

    @Column(name = "like_count", nullable = false)
    @Builder.Default
    private Integer likeCount = 0;

    public void increaseLikeCount() {
        this.likeCount++;
    }
    public void decreaseLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    public void updateContent(String content) {
        this.content = content;
    }
    public void updateRating(Double rating) { this.rating = rating; }

    public void addLike(ReviewLikeEntity like) {
        likes.add(like);
    }
    public void removeLike(ReviewLikeEntity like) {
        likes.remove(like);
    }

    public void updateUser(UserEntity user) {
        this.user = user;
    }
}

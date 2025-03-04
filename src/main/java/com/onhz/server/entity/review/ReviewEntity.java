package com.onhz.server.entity.review;

import com.onhz.server.common.enums.Review;
import com.onhz.server.common.enums.Role;
import com.onhz.server.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "review_tb")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Review review;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private double rating;

}

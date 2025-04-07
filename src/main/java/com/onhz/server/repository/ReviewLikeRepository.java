package com.onhz.server.repository;

import com.onhz.server.entity.review.ReviewLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewLikeRepository extends JpaRepository<ReviewLikeEntity, Long> {
    Optional<ReviewLikeEntity> findByReviewIdAndUserId(Long reviewId, Long userId);
    List<ReviewLikeEntity> findAllByUserId(Long userId);
    void deleteByUserId(Long userId);
}

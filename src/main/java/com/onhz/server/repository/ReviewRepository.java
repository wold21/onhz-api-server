package com.onhz.server.repository;

import com.onhz.server.common.enums.Review;
import com.onhz.server.entity.review.ReviewEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {
    List<ReviewEntity> findByReviewAndEntityId(Review review, Long entityId, Pageable pageable);
}

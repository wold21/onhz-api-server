package com.onhz.server.repository.dsl;

import com.onhz.server.common.enums.Review;
import com.onhz.server.dto.response.dsl.ReviewResponse;
import com.onhz.server.entity.review.ReviewEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewDSLRepository {
    List<ReviewResponse> findAllReviews(Pageable pageable);
    List<ReviewResponse> findReviewsWithLikesAndUserLike(Review reviewType, Long entityId, Long userId, Pageable pageable);
    Optional<ReviewResponse> findReviewDetail(Long userId, Long reviewId);
}

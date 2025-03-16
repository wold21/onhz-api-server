package com.onhz.server.repository.dsl;

import com.onhz.server.common.enums.ReviewType;
import com.onhz.server.dto.response.ReviewResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewDSLRepository {
    List<ReviewResponse> findAllReviews(Pageable pageable);
    List<ReviewResponse> findReviewsWithLikesAndUserLike(ReviewType reviewType, Long entityId, Long userId, Pageable pageable);
    Optional<ReviewResponse> findReviewDetail(Long userId, Long reviewId);
}

package com.onhz.server.service.review;

import com.onhz.server.common.enums.Review;
import com.onhz.server.common.utils.PageUtils;
import com.onhz.server.dto.request.ReviewRequest;
import com.onhz.server.dto.response.ReviewResponse;
import com.onhz.server.entity.review.ReviewEntity;
import com.onhz.server.entity.user.UserEntity;
import com.onhz.server.repository.ReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public List<ReviewResponse> getEntityReviews(Review reviewType, Long entityId, int offset, int limit, String orderBy) {
        Pageable pageable = PageUtils.createPageable(offset, limit, orderBy, ReviewEntity.class);
        return reviewRepository.findByReviewAndEntityId(reviewType, entityId, pageable).stream()
                .map(ReviewResponse::from)
                .toList();
    }

    public ReviewResponse getReviewDetail(Long reviewId) {
        var entity = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));
        return ReviewResponse.from(entity);
    }
    @Transactional
    public ReviewResponse createReview(UserEntity user, Review reviewType, Long entityId, ReviewRequest request) {
        Optional<ReviewEntity> existingReview = reviewRepository.findByUserAndEntityIdAndReview(user, entityId, reviewType);
        if (existingReview.isPresent()) {
            throw new IllegalStateException("이미 해당 항목에 리뷰를 남겼습니다.");
        }
        ReviewEntity review = ReviewEntity.builder()
                .user(user)
                .content(request.getContent() != null ? request.getContent() : "")
                .review(reviewType)
                .entityId(entityId)
                .rating(request.getRating() != null ? request.getRating() : 0.0)
                .build();
        ReviewEntity savedReview = reviewRepository.save(review);

        return ReviewResponse.from(savedReview);
    }

    @Transactional
    public void updateReview(Long reviewId, ReviewRequest request) {
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));
        if (request.getContent() != null) {
            review.updateContent(request.getContent());
        }
        if (request.getRating() != null) {
            review.updateRating(request.getRating());
        }
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));
        reviewRepository.delete(review);
    }


}

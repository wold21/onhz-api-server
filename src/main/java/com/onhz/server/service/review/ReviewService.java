package com.onhz.server.service.review;

import com.onhz.server.common.enums.ReviewType;
import com.onhz.server.common.utils.PageUtils;
import com.onhz.server.dto.request.ReviewRequest;
import com.onhz.server.dto.response.RatingSummaryResponse;
import com.onhz.server.dto.response.ReviewLatestResponse;
import com.onhz.server.dto.response.ReviewResponse;
import com.onhz.server.entity.RatingSummaryEntity;
import com.onhz.server.entity.review.ReviewEntity;
import com.onhz.server.entity.review.ReviewLikeEntity;
import com.onhz.server.entity.user.UserEntity;
import com.onhz.server.repository.*;
import com.onhz.server.repository.dsl.ReviewDSLRepository;
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
    private final ReviewLikeRepository reviewLikeRepository;
    private final ReviewDSLRepository reviewDSLRepository;
    private final AlbumRatingSummaryRepository albumRatingSummaryRepository;
    private final ArtistRatingSummaryRepository artistRatingSummaryRepository;
    private final TrackRatingSummaryRepository trackRatingSummaryRepository;

    public List<ReviewLatestResponse> getReviews(int offset, int limit, String orderBy) {
        Pageable pageable = PageUtils.createPageable(offset, limit, orderBy, ReviewEntity.class);
        return reviewDSLRepository.findAllReviews(pageable);

    }

    public List<ReviewResponse> getEntityReviews(UserEntity user, ReviewType reviewType, Long entityId, int offset, int limit, String orderBy) {
        Pageable pageable = PageUtils.createPageable(offset, limit, orderBy, ReviewEntity.class);
        return reviewDSLRepository.findReviewsWithLikesAndUserLike(reviewType, entityId, user.getId(), pageable);
    }

    public ReviewResponse getReviewDetail(UserEntity user, Long reviewId) {
        return reviewDSLRepository.findReviewDetail(user.getId(), reviewId)
                .orElseThrow(() -> new EntityNotFoundException("리뷰를 찾을 수 없습니다."));
    }
    @Transactional
    public ReviewResponse createReview(UserEntity user, ReviewType reviewType, Long entityId, ReviewRequest request) {
        Optional<ReviewEntity> existingReview = reviewRepository.findByUserAndEntityIdAndReviewType(user, entityId, reviewType);
        if (existingReview.isPresent()) {
            throw new IllegalStateException("이미 해당 항목에 리뷰를 남겼습니다.");
        }
        ReviewEntity review = ReviewEntity.builder()
                .user(user)
                .content(request.getContent() != null ? request.getContent() : "")
                .reviewType(reviewType)
                .entityId(entityId)
                .rating(request.getRating() != null ? request.getRating() : 0.0)
                .build();
        ReviewEntity savedReview = reviewRepository.save(review);

        return ReviewResponse.from(savedReview, 0, false);
    }

    @Transactional
    public void updateReview(Long reviewId, ReviewRequest request) {
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("리뷰를 찾을 수 없습니다."));
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
                .orElseThrow(() -> new EntityNotFoundException("리뷰를 찾을 수 없습니다."));
        reviewRepository.delete(review);
    }

    @Transactional
    public void toggleLike(UserEntity user, Long reviewId) {
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("리뷰를 찾을 수 없습니다."));

        Optional<ReviewLikeEntity> existingLike = reviewLikeRepository.findByReviewIdAndUserId(reviewId, user.getId());

        if (existingLike.isPresent()) {
            review.removeLike(existingLike.get());
            reviewLikeRepository.delete(existingLike.get());
        } else {
            ReviewLikeEntity like = ReviewLikeEntity.create(review, user);
            review.addLike(like);
            reviewLikeRepository.save(like);
        }
    }

    public RatingSummaryResponse getRatingSummary(ReviewType reviewType, Long entityId) {
        RatingSummaryEntity ratingSummaryEntity = switch (reviewType) {
            case ALBUM -> albumRatingSummaryRepository.findByAlbumId(entityId).orElse(null);
            case ARTIST -> artistRatingSummaryRepository.findByArtistId(entityId).orElse(null);
            case TRACK -> trackRatingSummaryRepository.findByTrackId(entityId).orElse(null);
        };
        return RatingSummaryResponse.from(ratingSummaryEntity, entityId);
    }

}

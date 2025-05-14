package com.onhz.server.service.review;

import com.onhz.server.common.enums.ReviewType;
import com.onhz.server.common.utils.PageUtils;
import com.onhz.server.dto.request.ReviewRequest;
import com.onhz.server.dto.response.ReviewRatingResponse;
import com.onhz.server.dto.response.review.ReviewResponse;
import com.onhz.server.entity.RatingSummaryEntity;
import com.onhz.server.entity.review.ReviewEntity;
import com.onhz.server.entity.review.ReviewLikeEntity;
import com.onhz.server.entity.user.UserEntity;
import com.onhz.server.repository.*;
import com.onhz.server.repository.dsl.ReviewDSLRepository;
import com.onhz.server.service.schedule.AlbumScheduleService;
import com.onhz.server.service.schedule.ArtistScheduleService;
import com.onhz.server.service.schedule.TrackScheduleService;
import com.onhz.server.service.schedule.UserScheduleService;
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
    private final ArtistScheduleService artistScheduleService;
    private final AlbumScheduleService albumScheduleService;
    private final TrackScheduleService trackScheduleService;
    private final UserScheduleService userScheduleService;

    public List<ReviewResponse> getReviews(UserEntity user, Long lastId, String lastOrderValue, int limit, String orderBy) {
        Pageable pageable = PageUtils.createPageable(0, limit, orderBy, ReviewEntity.class);
        Long userId = (user != null) ? user.getId() : null;
        return reviewDSLRepository.findAllReviews(userId, lastId, lastOrderValue, pageable);
    }

    public List<ReviewResponse> getEntityReviews(UserEntity user, ReviewType reviewType, Long entityId, Long lastId, String lastOrderValue, int limit, String orderBy) {
        Pageable pageable = PageUtils.createPageable(0, limit, orderBy, ReviewEntity.class);
        Long userId = (user != null) ? user.getId() : null;
        return reviewDSLRepository.findReviewsWithLikesAndUserLike(reviewType, entityId, userId, lastId, lastOrderValue, pageable);
    }

    public ReviewResponse getReviewDetail(UserEntity user, Long reviewId) {
        Long userId = (user != null) ? user.getId() : null;
        return reviewDSLRepository.findReviewDetail(userId, reviewId)
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
        updateRatingSummary(user.getId(), reviewType, entityId);
        return getReviewDetail(user, savedReview.getId());
    }

    @Transactional
    public void updateReview(UserEntity user, ReviewType reviewType, Long entityId, Long reviewId, ReviewRequest request) {
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("리뷰를 찾을 수 없습니다."));
        if (request.getContent() != null) {
            review.updateContent(request.getContent());
        }
        if (request.getRating() != null) {
            review.updateRating(request.getRating());
        }
        updateRatingSummary(user.getId(), reviewType, entityId);
    }

    @Transactional
    public void deleteReview(UserEntity user, ReviewType reviewType, Long entityId, Long reviewId) {
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("리뷰를 찾을 수 없습니다."));
        reviewRepository.delete(review);
        updateRatingSummary(user.getId(), reviewType, entityId);
    }

    @Transactional
    public void toggleLike(UserEntity user, Long reviewId) {
        ReviewEntity review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("리뷰를 찾을 수 없습니다."));

        Optional<ReviewLikeEntity> existingLike = reviewLikeRepository.findByReviewIdAndUserId(reviewId, user.getId());

        if (existingLike.isPresent()) {
            review.removeLike(existingLike.get());
            review.decreaseLikeCount();
            reviewLikeRepository.delete(existingLike.get());
        } else {
            ReviewLikeEntity like = ReviewLikeEntity.create(review, user);
            review.addLike(like);
            review.increaseLikeCount();
            reviewLikeRepository.save(like);
        }
    }

    public ReviewRatingResponse getRatingSummary(UserEntity user, ReviewType reviewType, Long entityId) {
        RatingSummaryEntity ratingSummaryEntity = switch (reviewType) {
            case ALBUM -> albumRatingSummaryRepository.findByAlbumId(entityId).orElse(null);
            case ARTIST -> artistRatingSummaryRepository.findByArtistId(entityId).orElse(null);
            case TRACK -> trackRatingSummaryRepository.findByTrackId(entityId).orElse(null);
        };
        Optional<ReviewEntity> reviewEntity = reviewRepository.findByUserAndEntityIdAndReviewType(user, entityId, reviewType);
        return ReviewRatingResponse.from(ratingSummaryEntity, entityId, reviewEntity);
    }

    private void updateRatingSummary(Long userId, ReviewType type, Long entityId) {
        switch (type) {
            case ARTIST -> artistScheduleService.updateSummaryImmediately(entityId);
            case ALBUM -> albumScheduleService.updateSummaryImmediately(entityId);
            case TRACK -> trackScheduleService.updateSummaryImmediately(entityId);
        }
        userScheduleService.updateSummaryImmediately(userId);
    }
}

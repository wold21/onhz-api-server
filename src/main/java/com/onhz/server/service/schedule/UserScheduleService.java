package com.onhz.server.service.schedule;

import com.onhz.server.common.schedule.RatingScheduleInterface;
import com.onhz.server.common.utils.PageUtils;
import com.onhz.server.common.utils.SummaryUtils;
import com.onhz.server.entity.review.ReviewEntity;
import com.onhz.server.entity.user.UserEntity;
import com.onhz.server.entity.user.UserRatingSummaryEntity;
import com.onhz.server.repository.ReviewRepository;
import com.onhz.server.repository.UserRatingSummaryRepository;
import com.onhz.server.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserScheduleService implements RatingScheduleInterface {
    private static final int BATCH_SIZE = 1000;
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final UserRatingSummaryRepository userRatingSummaryRepository;
    private final SummaryUtils summaryUtils;

    @Override
    @Transactional
    public void ratings() {
        int page = 0;
        boolean hasNextUser = true;
        while(hasNextUser) {
            Page<?> result = findEntitiesWithReviews(PageUtils.createPageable(page, BATCH_SIZE));
            Page<UserEntity> users = (Page<UserEntity>) result;

            if(users.hasContent()){
                batch(users);
                page++;
            }
            hasNextUser = users.hasNext();
        }
    }

    @Override
    public void batch(Page<?> entities) {
        for (Object entity : entities) {
            UserEntity user = null;
            try {
                user = (UserEntity) entity;
                entityInsertAndUpdate(user.getId());
            } catch (Exception e) {
                log.error("사용자 ID {} 처리 중 오류 발생: {}", user.getId(), e.getMessage(), e);
            }
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void entityInsertAndUpdate(Long entityId) {
        UserEntity user = userRepository.findById(entityId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다: " + entityId));

        List<ReviewEntity> reviews = reviewRepository.findByUserId(user.getId());

        if(reviews.isEmpty()){
            log.info("사용자 ID {}에 대한 리뷰가 없습니다.", user.getId());
            return;
        }

        Double avgRating = reviews.stream()
                .mapToDouble(ReviewEntity::getRating)
                .average()
                .orElse(0.0);

        avgRating = Math.round(avgRating * 10) / 10.0;

        int ratingCount = reviews.size();

        Map<String, Integer> distribution = summaryUtils.calculateRatingDistributionFromObjects(
                reviews, ReviewEntity::getRating);

        String jsonDistribution = summaryUtils.convertToJson(distribution);

        log.info("사용자 ID {}에 대한 리뷰 처리 완료: 평균 평점 {}, 리뷰 수 {}, 평점 분포 {}",
                user.getId(), avgRating, ratingCount, jsonDistribution);

        Optional<UserRatingSummaryEntity> existing = userRatingSummaryRepository.findByUser(user);

        UserRatingSummaryEntity summary;
        if (existing.isPresent()) {
            summary = existing.get();
            summary.updateStats(avgRating, ratingCount, jsonDistribution);
        } else {
            summary = UserRatingSummaryEntity.create(user, avgRating, ratingCount, jsonDistribution);
        }

        try {
            userRatingSummaryRepository.save(summary);
        } catch (Exception e) {
            log.error("요약 정보 저장 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public Page<?> findEntitiesWithReviews(Pageable pageable) {
        return reviewRepository.findDistinctUserIdsByRatingIsNotNull(pageable);
    }
}

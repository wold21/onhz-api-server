package com.onhz.server.service.schedule;

import com.onhz.server.common.enums.ReviewType;
import com.onhz.server.common.schedule.RatingScheduleInterface;
import com.onhz.server.common.utils.PageUtils;
import com.onhz.server.common.utils.SummaryUtils;
import com.onhz.server.entity.album.AlbumEntity;
import com.onhz.server.entity.album.AlbumRatingSummaryEntity;
import com.onhz.server.entity.review.ReviewEntity;
import com.onhz.server.repository.AlbumRatingSummaryRepository;
import com.onhz.server.repository.ReviewRepository;
import com.onhz.server.repository.AlbumRepository;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class AlbumScheduleService implements RatingScheduleInterface {
    private static final int BATCH_SIZE = 1000;
    private final ReviewRepository reviewRepository;
    private final AlbumRepository albumRepository;
    private final AlbumRatingSummaryRepository albumRatingSummaryRepository;
    private final SummaryUtils summaryUtils;

    @Override
    @Transactional
    public void ratings() {
        int page = 0;
        boolean hasNext = true;
        while(hasNext) {
            Page<?> entityIds = findEntitiesWithReviews(PageUtils.createPageable(page, BATCH_SIZE));

            if(entityIds.hasContent()){
                batch(entityIds);
                page++;
            }
            hasNext = entityIds.hasNext();
        }
    }

    @Override
    public void batch(Page<?> entities) {
        for (Object entity : entities) {
            try {
                Long entityId = (Long) entity;
                long reviewCount = reviewRepository.countByUserIdAndRatingIsNotNull(entityId);
                if (reviewCount <= BATCH_SIZE) {
                    log.info("앨범 {} _ 리뷰 수 {}건(1000건 이하)이므로 이미 업데이트 수행됨", entityId, reviewCount);
                    return;
                }
                entityInsertAndUpdate(entityId);
            } catch (Exception e) {
                log.error("앨범 ID {} 처리 중 오류 발생: {}", entity, e.getMessage(), e);
            }
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void entityInsertAndUpdate(Long entityId) {
        AlbumEntity album = albumRepository.findById(entityId)
                .orElseThrow(() -> new EntityNotFoundException("앨범을 찾을 수 없습니다: " + entityId));

        List<ReviewEntity> reviews = reviewRepository.findByReviewTypeAndEntityId(ReviewType.ALBUM, album.getId());
        Double avgRating = reviews.stream()
                .mapToDouble(ReviewEntity::getRating)
                .average()
                .orElse(0.0);

        avgRating = Math.round(avgRating * 10) / 10.0;

        int ratingCount = reviews.size();

        Map<String, Integer> distribution = summaryUtils.calculateRatingDistributionFromObjects(
                reviews, ReviewEntity::getRating);

        String jsonDistribution = summaryUtils.convertToJson(distribution);

        AlbumRatingSummaryEntity summary = albumRatingSummaryRepository.findByAlbum(album)
                .orElse(AlbumRatingSummaryEntity.create(album));

        summary.updateStats(avgRating, ratingCount, jsonDistribution);
        albumRatingSummaryRepository.save(summary);

    }

    @Override
    public Page<?> findEntitiesWithReviews(Pageable pageable) {
        return reviewRepository.findDistinctEntityIdsByReviewTypeAndRatingIsNotNull(ReviewType.ALBUM, pageable);
    }

    public void updateSummaryImmediately(Long entityId) {
        long reviewCount = reviewRepository.countByReviewTypeAndEntityIdAndRatingIsNotNull(ReviewType.ALBUM, entityId);
        if (reviewCount <= BATCH_SIZE) {
            log.info("앨범 {} _ 리뷰 수 {}건(1000건 이하)이므로 즉시 Summary 테이블 업데이트 수행", entityId, reviewCount);
            entityInsertAndUpdate(entityId);
        }
    }
}

package com.onhz.server.service.schedule;

import com.onhz.server.common.enums.ReviewType;
import com.onhz.server.common.schedule.RatingScheduleInterface;
import com.onhz.server.common.utils.PageUtils;
import com.onhz.server.common.utils.SummaryUtils;
import com.onhz.server.entity.artist.ArtistEntity;
import com.onhz.server.entity.artist.ArtistRatingSummaryEntity;
import com.onhz.server.entity.review.ReviewEntity;
import com.onhz.server.repository.ArtistRatingSummaryRepository;
import com.onhz.server.repository.ArtistRepository;
import com.onhz.server.repository.ReviewRepository;
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
public class ArtistScheduleService implements RatingScheduleInterface {
    private static final int BATCH_SIZE = 1000;
    private final ReviewRepository reviewRepository;
    private final ArtistRepository artistRepository;
    private final ArtistRatingSummaryRepository artistRatingSummaryRepository;
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
                entityInsertAndUpdate((Long) entity);
            } catch (Exception e) {
                log.error("아티스트 ID {} 처리 중 오류 발생: {}", entity, e.getMessage(), e);
            }
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void entityInsertAndUpdate(Long entityId) {
        ArtistEntity artist = artistRepository.findById(entityId)
                .orElseThrow(() -> new EntityNotFoundException("아티스트를 찾을 수 없습니다: " + entityId));

        List<ReviewEntity> reviews = reviewRepository.findByReviewTypeAndEntityId(ReviewType.ARTIST, artist.getId());

        if(reviews.isEmpty()) {
            log.info("아티스트 ID {}에 대한 리뷰가 없습니다.", entityId);
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

        ArtistRatingSummaryEntity summary = artistRatingSummaryRepository.findByArtist(artist)
                .orElse(ArtistRatingSummaryEntity.create(artist));

        summary.updateStats(avgRating, ratingCount, jsonDistribution);
        artistRatingSummaryRepository.save(summary);

    }

    @Override
    public Page<?> findEntitiesWithReviews(Pageable pageable) {
        return reviewRepository.findDistinctEntityIdsByReviewTypeAndRatingIsNotNull(ReviewType.ARTIST, pageable);
    }
}

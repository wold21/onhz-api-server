package com.onhz.server.repository.dsl;

import com.onhz.server.entity.artist.QArtistTrackEntity;
import com.onhz.server.entity.track.QTrackRatingSummaryEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TrackRatingSummaryDSLRepositoryImpl implements TrackRatingSummaryDSLRepository {
    private final JPAQueryFactory queryFactory;
    private final QTrackRatingSummaryEntity trackRatingSummaryEntity = QTrackRatingSummaryEntity.trackRatingSummaryEntity;
    private final QArtistTrackEntity artistTrackEntity = QArtistTrackEntity.artistTrackEntity;
    @Override
    public List<Long> findTrackIdsByArtistIdWithRating(Long artistId, Pageable pageable) {
        List<Long> trackIds = queryFactory
                .select(artistTrackEntity.track.id)
                .from(artistTrackEntity)
                .leftJoin(trackRatingSummaryEntity)
                .on(artistTrackEntity.track.id.eq(trackRatingSummaryEntity.track.id))
                .where(artistTrackEntity.artist.id.eq(artistId))
                .orderBy(
                        trackRatingSummaryEntity.ratingCount.desc(),
                        trackRatingSummaryEntity.averageRating.desc()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return trackIds;
    }
}

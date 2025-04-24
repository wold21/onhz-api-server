package com.onhz.server.repository.dsl;

import com.onhz.server.entity.artist.QArtistTrackEntity;
import com.onhz.server.entity.track.QTrackRatingSummaryEntity;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class TrackRatingSummaryDSLRepositoryImpl implements TrackRatingSummaryDSLRepository {
    private final JPAQueryFactory queryFactory;
    private final QTrackRatingSummaryEntity trackRatingSummaryEntity = QTrackRatingSummaryEntity.trackRatingSummaryEntity;
    private final QArtistTrackEntity artistTrackEntity = QArtistTrackEntity.artistTrackEntity;
    @Override
    public List<Long> findTrackIdsByArtistIdWithRating(Long artistId, Pageable pageable) {
        List<Tuple> dataList = queryFactory
                .select(trackRatingSummaryEntity.track.id,
                        trackRatingSummaryEntity.ratingCount,
                        trackRatingSummaryEntity.averageRating
                )
                .from(trackRatingSummaryEntity)
                .leftJoin(artistTrackEntity)
                .on(artistTrackEntity.track.eq(trackRatingSummaryEntity.track))
                .where(artistTrackEntity.artist.id.eq(artistId))
                .orderBy(
                        trackRatingSummaryEntity.ratingCount.desc(),
                        trackRatingSummaryEntity.averageRating.desc()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<Long> result = dataList.stream()
                .map(data -> data.get(trackRatingSummaryEntity.track.id))
                .collect(Collectors.toList());

        return result;
    }
}

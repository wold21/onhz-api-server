package com.onhz.server.repository.dsl;

import com.onhz.server.dto.response.track.TrackResponse;
import com.onhz.server.entity.artist.QArtistTrackEntity;
import com.onhz.server.entity.track.QTrackEntity;
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
    private final QTrackEntity trackEntity = QTrackEntity.trackEntity;

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

    public List<TrackResponse> findTrackByAlbumIdOrderByTrackRank(Long albumId) {
        List<Tuple> dataList = queryFactory
                .select(
                        trackEntity.id,
                        trackEntity.title,
                        trackEntity.trackRank,
                        trackEntity.duration,
                        trackEntity.album.id,
                        trackEntity.createdAt,
                        trackRatingSummaryEntity.averageRating
                )
                .from(trackEntity)
                .leftJoin(trackRatingSummaryEntity)
                .on(trackEntity.id.eq(trackRatingSummaryEntity.track.id))
                .where(trackEntity.album.id.eq(albumId))
                .orderBy(
                        trackEntity.trackRank.asc()
                )
                .fetch();

        return dataList.stream()
                .map(tuple -> TrackResponse.builder()
                        .id(tuple.get(trackEntity.id))
                        .title(tuple.get(trackEntity.title))
                        .rank(tuple.get(trackEntity.trackRank) != null ? tuple.get(trackEntity.trackRank) : 0)
                        .duration(tuple.get(trackEntity.duration) != null ? tuple.get(trackEntity.duration) : 0)
                        .albumId(tuple.get(trackEntity.album.id))
                        .createdAt(tuple.get(trackEntity.createdAt))
                        .rating(tuple.get(trackRatingSummaryEntity.averageRating) != null ? tuple.get(trackRatingSummaryEntity.averageRating) : 0.0)
                        .build())
                .collect(Collectors.toList());
    }

}

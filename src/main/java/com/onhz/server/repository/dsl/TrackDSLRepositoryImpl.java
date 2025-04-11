package com.onhz.server.repository.dsl;

import com.onhz.server.common.utils.QueryDslUtil;
import com.onhz.server.dto.response.artist.ArtistSimpleResponse;
import com.onhz.server.dto.response.track.TrackDetailResponse;
import com.onhz.server.entity.album.QAlbumEntity;
import com.onhz.server.entity.artist.QArtistEntity;
import com.onhz.server.entity.artist.QArtistTrackEntity;
import com.onhz.server.entity.track.QTrackEntity;
import com.onhz.server.entity.track.QTrackRatingSummaryEntity;
import com.onhz.server.entity.track.TrackEntity;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class TrackDSLRepositoryImpl implements TrackDSLRepository {
    private final JPAQueryFactory queryFactory;
    private final QTrackEntity trackEntity = QTrackEntity.trackEntity;
    private final QArtistTrackEntity artistTrackEntity = QArtistTrackEntity.artistTrackEntity;
    private final QAlbumEntity albumEntity = QAlbumEntity.albumEntity;
    private final QArtistEntity artistEntity = QArtistEntity.artistEntity;
    private final QTrackRatingSummaryEntity trackRatingSummaryEntity = QTrackRatingSummaryEntity.trackRatingSummaryEntity;
    PathBuilder<TrackEntity> entityPath = new PathBuilder<>(TrackEntity.class, "trackEntity");

    @Override
    public List<Long> findTrackIdsByArtistId(Long artistId, Long lastId, String lastOrderValue, Pageable pageable) {
        JPAQuery<Long> query = queryFactory
                .select(artistTrackEntity.track.id)
                .from(artistTrackEntity)
                .leftJoin(artistTrackEntity.track, trackEntity)
                .where(artistTrackEntity.artist.id.eq(artistId));

        if (lastId != null) {
            query.where(QueryDslUtil.buildCursorCondition(pageable, entityPath, lastId, lastOrderValue));
        }

        return query
                .orderBy(QueryDslUtil.buildOrderSpecifiers(pageable, entityPath))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    @Override
    public List<TrackDetailResponse> getTracksWithArtistsAndRatingByIds(List<Long> trackIds) {
        Map<Long, TrackDetailResponse> tracks = getTrackWithAlbumAndRating(trackIds);
        List<Tuple> artists = getArtistsByTrackIds(trackIds);

        for (Tuple tuple : artists) {
            Long trackId = tuple.get(artistTrackEntity.track.id);
            Long artistId = tuple.get(artistEntity.id);
            String name = tuple.get(artistEntity.name);
            String profilePath = tuple.get(artistEntity.profilePath);
            String role = tuple.get(artistTrackEntity.artistRole);
            Double rating = tuple.get(trackRatingSummaryEntity.averageRating);

            TrackDetailResponse response = tracks.get(trackId);
            if (response != null) {
                ArtistSimpleResponse artistResponse = ArtistSimpleResponse.builder()
                        .id(artistId)
                        .name(name)
                        .profilePath(profilePath)
                        .role(role)
                        .build();

                response.getArtists().add(artistResponse);
            }
        }

        return trackIds.stream()
                .map(tracks::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    private Map<Long, TrackDetailResponse> getTrackWithAlbumAndRating(List<Long> trackIds){
        Map<Long, TrackDetailResponse> trackMap = new LinkedHashMap<>();
        List<Tuple> trackResults = queryFactory
                .select(
                        trackEntity.id,
                        trackEntity.trackName,
                        trackEntity.trackRank,
                        trackEntity.duration,
                        trackEntity.createdAt,
                        albumEntity.id,
                        albumEntity.coverPath,
                        trackRatingSummaryEntity.averageRating
                )
                .from(trackEntity)
                .join(trackEntity.album, albumEntity)
                .leftJoin(trackRatingSummaryEntity)
                .on(trackRatingSummaryEntity.track.id.eq(trackEntity.id))
                .where(trackEntity.id.in(trackIds))
                .fetch();

        for (Tuple tuple : trackResults) {
            Long trackId = tuple.get(trackEntity.id);
            String trackName = tuple.get(trackEntity.trackName);
            Integer trackRank = tuple.get(trackEntity.trackRank);
            Integer duration = tuple.get(trackEntity.duration) == null ? 0 : tuple.get(trackEntity.duration);
            LocalDateTime createdAt = tuple.get(trackEntity.createdAt);
            Long albumId = tuple.get(albumEntity.id);
            String coverPath = tuple.get(albumEntity.coverPath);
            Double rating = tuple.get(trackRatingSummaryEntity.averageRating) == null ? 0.0 : tuple.get(trackRatingSummaryEntity.averageRating);

            TrackDetailResponse response = TrackDetailResponse.builder()
                    .id(trackId)
                    .title(trackName)
                    .rank(trackRank)
                    .duration(duration)
                    .albumId(albumId)
                    .coverPath(coverPath)
                    .createdAt(createdAt)
                    .rating(rating)
                    .artists(new ArrayList<>())
                    .build();

            trackMap.put(trackId, response);
        }
        return trackMap;
    }

    private List<Tuple> getArtistsByTrackIds(List<Long> trackIds){
        return  queryFactory
                .select(
                        artistTrackEntity.track.id,
                        artistEntity.id,
                        artistEntity.name,
                        artistEntity.profilePath,
                        artistTrackEntity.artistRole
                )
                .from(artistTrackEntity)
                .join(artistTrackEntity.artist, artistEntity)
                .where(artistTrackEntity.track.id.in(trackIds))
                .fetch();
    }

    @Override
    public List<TrackEntity> findTracksByKeyword(String keyword, Long lastId, String lastOrderValue, Pageable pageable) {
        JPAQuery<TrackEntity> query = queryFactory
                .selectFrom(trackEntity)
                .where(trackEntity.trackName.containsIgnoreCase(keyword));
        if (lastId != null) {
            query.where(QueryDslUtil.buildCursorCondition(pageable, entityPath, lastId, lastOrderValue));
        }
        return query
                .orderBy(QueryDslUtil.buildOrderSpecifiers(pageable, entityPath))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }
}

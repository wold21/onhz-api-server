package com.onhz.server.repository.dsl;

import com.onhz.server.common.utils.QueryDslUtil;
import com.onhz.server.entity.QGenreEntity;
import com.onhz.server.entity.album.QAlbumEntity;
import com.onhz.server.entity.album.QAlbumGenreEntity;
import com.onhz.server.entity.album.QAlbumRatingSummaryEntity;
import com.onhz.server.entity.artist.QArtistAlbumEntity;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class AlbumRatingSummaryDSLRepositoryImpl implements AlbumRatingSummaryDSLRepository{
    private final JPAQueryFactory queryFactory;
    private final QAlbumRatingSummaryEntity albumRatingSummaryEntity = QAlbumRatingSummaryEntity.albumRatingSummaryEntity;
    private final QArtistAlbumEntity artistAlbumEntity = QArtistAlbumEntity.artistAlbumEntity;
    private final QAlbumEntity albumEntity = QAlbumEntity.albumEntity;
    private final QAlbumGenreEntity albumGenreEntity = QAlbumGenreEntity.albumGenreEntity;
    private final QGenreEntity genreEntity = QGenreEntity.genreEntity;

    @Override
    public Page<Long> findAllIdsWithRatingAndGenre(String genreCode, Pageable pageable) {
        List<Tuple> dataList = queryFactory
                .select(
                        albumRatingSummaryEntity.album.id,
                        albumRatingSummaryEntity.ratingCount,
                        albumRatingSummaryEntity.averageRating
                )
                .distinct()
                .from(albumRatingSummaryEntity)
                .leftJoin(albumRatingSummaryEntity.album, albumEntity)
                .leftJoin(albumEntity.albumGenres, albumGenreEntity)
                .leftJoin(albumGenreEntity.genre, genreEntity)
                .where(genreEntity.code.lower().like("%" + genreCode.toLowerCase() + "%"))
                .orderBy(
                        albumRatingSummaryEntity.ratingCount.desc(),
                        albumRatingSummaryEntity.averageRating.desc()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<Long> content = dataList.stream()
                .map(data -> data.get(albumRatingSummaryEntity.album.id))
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, 0L);
    }

    @Override
    public List<Long> findAlbumIdsByArtistIdWithRating(Long artistId, Pageable pageable) {
        List<Tuple> dataList = queryFactory
                .select(
                        albumRatingSummaryEntity.album.id,
                        albumRatingSummaryEntity.ratingCount,
                        albumRatingSummaryEntity.averageRating
                )
                .distinct()
                .from(albumRatingSummaryEntity)
                .leftJoin(artistAlbumEntity)
                .on(artistAlbumEntity.album.eq(albumRatingSummaryEntity.album))
                .where(artistAlbumEntity.artist.id.eq(artistId))
                .orderBy(
                        albumRatingSummaryEntity.ratingCount.desc(),
                        albumRatingSummaryEntity.averageRating.desc()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        List<Long> result = dataList.stream()
                .map(data -> data.get(albumRatingSummaryEntity.album.id))
                .collect(Collectors.toList());

        return result;
    }
}

package com.onhz.server.repository.dsl;

import com.onhz.server.entity.QGenreEntity;
import com.onhz.server.entity.album.QAlbumEntity;
import com.onhz.server.entity.album.QAlbumGenreEntity;
import com.onhz.server.entity.album.QAlbumRatingSummaryEntity;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AlbumRatingSummaryDSLRepositoryImpl implements AlbumRatingSummaryDSLRepository{
    private final JPAQueryFactory queryFactory;
    private final QAlbumRatingSummaryEntity albumRatingSummaryEntity = QAlbumRatingSummaryEntity.albumRatingSummaryEntity;
    private final QAlbumEntity albumEntity = QAlbumEntity.albumEntity;
    private final QAlbumGenreEntity albumGenreEntity = QAlbumGenreEntity.albumGenreEntity;
    private final QGenreEntity genreEntity = QGenreEntity.genreEntity;
    @Override
    public Page<Long> findAllIdsWithRatingAndGenre(String genreCode, Pageable pageable) {
        List<Long> content = queryFactory
                .select(albumRatingSummaryEntity.album.id)
                .from(albumRatingSummaryEntity)
                .join(albumRatingSummaryEntity.album, albumEntity)
                .join(albumEntity.albumGenres, albumGenreEntity)
                .join(albumGenreEntity.genre, genreEntity)
                .where(genreEntity.code.lower().like("%" + genreCode.toLowerCase() + "%"))
                .groupBy(albumRatingSummaryEntity.album.id, albumRatingSummaryEntity.ratingCount, albumRatingSummaryEntity.averageRating)
                .orderBy(albumRatingSummaryEntity.ratingCount.desc(), albumRatingSummaryEntity.averageRating.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, 0L);
    }
}

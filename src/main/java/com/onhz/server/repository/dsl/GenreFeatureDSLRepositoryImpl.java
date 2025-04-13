package com.onhz.server.repository.dsl;

import com.onhz.server.entity.GenreFeaturedEntity;
import com.onhz.server.entity.QGenreEntity;
import com.onhz.server.entity.QGenreFeaturedEntity;
import com.onhz.server.entity.album.QAlbumEntity;
import com.onhz.server.entity.album.QAlbumGenreEntity;
import com.onhz.server.entity.album.QAlbumRatingSummaryEntity;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class GenreFeatureDSLRepositoryImpl  implements GenreFeatureDSLRepository {
    private final JPAQueryFactory queryFactory;
    private final QGenreFeaturedEntity qGenreFeaturedEntity = QGenreFeaturedEntity.genreFeaturedEntity;
    private final QAlbumEntity qAlbumEntity = QAlbumEntity.albumEntity;
    private final QAlbumGenreEntity qAlbumGenreEntity = QAlbumGenreEntity.albumGenreEntity;
    private final QGenreEntity qGenreEntity = QGenreEntity.genreEntity;
    private final QAlbumRatingSummaryEntity qAlbumRatingSummaryEntity = QAlbumRatingSummaryEntity.albumRatingSummaryEntity;
    @Override
    public List<GenreFeaturedEntity> findFeatureGenresWithRated() {
        return queryFactory
                .selectFrom(qGenreFeaturedEntity)
                .where(JPAExpressions
                        .selectOne()
                        .from(qAlbumEntity)
                        .innerJoin(qAlbumRatingSummaryEntity).on(qAlbumEntity.id.eq(qAlbumRatingSummaryEntity.album.id))
                        .innerJoin(qAlbumGenreEntity).on(qAlbumEntity.id.eq(qAlbumGenreEntity.album.id))
                        .innerJoin(qGenreEntity).on(qAlbumGenreEntity.genre.id.eq(qGenreEntity.id))
                        .where(
                                qAlbumRatingSummaryEntity.averageRating.isNotNull(),
                                qGenreEntity.code.like(qGenreFeaturedEntity.code.concat("%"))
                        )
                        .exists())
                .orderBy(Expressions.numberTemplate(Double.class, "function('random')").asc())
                .fetch();
    }
}

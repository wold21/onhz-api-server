package com.onhz.server.repository.dsl;

import com.onhz.server.entity.GenreFeaturedEntity;
import com.onhz.server.entity.QGenreEntity;
import com.onhz.server.entity.QGenreFeaturedEntity;
import com.onhz.server.entity.album.QAlbumEntity;
import com.onhz.server.entity.album.QAlbumGenreEntity;
import com.onhz.server.entity.album.QAlbumRatingSummaryEntity;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
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
                                genreMatchingCondition(qGenreEntity, qGenreFeaturedEntity)
                        )
                        .exists())
                .orderBy(Expressions.numberTemplate(Double.class, "function('random')").asc())
                .fetch();
    }

    private BooleanExpression genreMatchingCondition(QGenreEntity genre, QGenreFeaturedEntity featuredGenre) {
        return featuredGenre.code.lower().eq("pop")
                .and(
                        genre.code.lower().eq("pop")
                                .or(genre.code.lower().like("% pop"))
                                .or(genre.code.lower().like("%-pop"))
                                .or(genre.code.lower().eq("j-pop"))
                                .and(genre.code.lower().notLike("%k-pop%"))
                )
                .or(
                        featuredGenre.code.lower().in("hip hop", "hiphop")
                                .and(
                                        genre.code.lower().eq("hip hop")
                                                .or(genre.code.lower().eq("hiphop"))
                                                .or(genre.code.lower().like("% hip hop"))
                                                .or(genre.code.lower().like("% hiphop"))
                                )
                )
                .or(
                        featuredGenre.code.lower().in("kpop", "k-pop")
                                .and(
                                        genre.code.lower().eq("kpop")
                                                .or(genre.code.lower().eq("k-pop"))
                                )
                )
                .or(
                        featuredGenre.code.lower().notIn("pop", "hip hop", "hiphop", "kpop", "k-pop")
                                .and(
                                        genre.code.lower().eq(featuredGenre.code.lower())
                                                .or(genre.code.lower().like("% " + featuredGenre.code.lower().toString()))
                                                .or(genre.code.lower().like("%-" + featuredGenre.code.lower().toString()))
                                )
                );
    }
}
